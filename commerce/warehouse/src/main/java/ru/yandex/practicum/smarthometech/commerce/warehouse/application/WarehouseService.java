package ru.yandex.practicum.smarthometech.commerce.warehouse.application;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.smarthometech.commerce.api.exception.BookingNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.InsufficientQuantityException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.OrderBookingAlreadyExistsException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductAlreadyExistsException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.BookedItem;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.BookingStatus;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.OrderBooking;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.OrderBookingRepository;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.WarehouseItem;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.WarehouseRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository bookingRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest request) {
        log.debug("Attempting to register new product in warehouse with id: {}", request.getProductId());
        if (warehouseRepository.existsById(request.getProductId())) {
            log.warn("Product with id: {} already exists in the warehouse.", request.getProductId());
            throw new ProductAlreadyExistsException(request.getProductId());
        }
        WarehouseItem newItem = warehouseMapper.newProductRequestToWarehouseItem(request);
        warehouseRepository.save(newItem);
        log.info("Successfully registered new product in warehouse with id: {}", request.getProductId());
    }

    @Transactional
    public void increaseProductQuantity(AddProductToWarehouseRequest request) {
        log.debug("Attempting to add {} units of product id: {}", request.getQuantity(), request.getProductId());
        WarehouseItem item = warehouseRepository.findById(request.getProductId())
            .orElseThrow(() -> {
                log.error("Product with id: {} not found in warehouse.", request.getProductId());
                return new ProductNotFoundException(request.getProductId());
            });

        long newQuantity = item.getQuantity() + request.getQuantity();
        item.setQuantity(newQuantity);
        warehouseRepository.save(item);
        log.info("Successfully added {} units to product id: {}. New quantity: {}",
            request.getQuantity(), request.getProductId(), newQuantity);
    }

    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCart) {
        log.debug("Checking stock for shopping cart id: {}", shoppingCart.getShoppingCartId());

        Set<UUID> productIds = shoppingCart.getProducts().keySet().stream()
            .map(UUID::fromString)
            .collect(Collectors.toSet());

        Map<UUID, WarehouseItem> warehouseItems = warehouseRepository.findAllById(productIds).stream()
            .collect(Collectors.toMap(WarehouseItem::getProductId, item -> item));

        for (Map.Entry<String, Long> cartEntry : shoppingCart.getProducts().entrySet()) {
            UUID productId = UUID.fromString(cartEntry.getKey());
            Long requestedQuantity = cartEntry.getValue();

            WarehouseItem warehouseItem = warehouseItems.get(productId);

            if (warehouseItem == null) {
                log.error("Product with id: {} not found in warehouse during stock check.", productId);
                throw new ProductNotFoundException(productId);
            }
            if (warehouseItem.getQuantity() < requestedQuantity) {
                log.warn("Insufficient quantity for product id: {}. Requested: {}, Available: {}",
                    productId, requestedQuantity, warehouseItem.getQuantity());
                throw new InsufficientQuantityException(productId, requestedQuantity, warehouseItem.getQuantity());
            }
        }

        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        boolean isFragile = false;

        for (Map.Entry<String, Long> cartEntry : shoppingCart.getProducts().entrySet()) {
            UUID itemId = UUID.fromString(cartEntry.getKey());
            WarehouseItem item = warehouseItems.get(itemId);
            BigDecimal quantity = BigDecimal.valueOf(cartEntry.getValue());

            totalWeight = totalWeight.add(item.getWeightKg().multiply(quantity));
            BigDecimal volume = item.getWidthM().multiply(item.getHeightM()).multiply(item.getDepthM());
            totalVolume = totalVolume.add(volume.multiply(quantity));
            if (item.getIsFragile()) {
                isFragile = true;
            }
        }

        log.info("Stock check successful for shopping cart id: {}", shoppingCart.getShoppingCartId());
        return new BookedProductsDto()
            .deliveryWeight(totalWeight.doubleValue())
            .deliveryVolume(totalVolume.doubleValue())
            .fragile(isFragile);
    }

    @Transactional
    public BookedProductsDto assembleOrder(AssemblyProductsForOrderRequest request) {
        log.debug("Attempting to assemble order with id: {}", request.getOrderId());

        if (bookingRepository.existsById(request.getOrderId())) {
            log.warn("Order booking for id: {} already exists.", request.getOrderId());
            throw new OrderBookingAlreadyExistsException(request.getOrderId());
        }

        OrderBooking booking = new OrderBooking();
        booking.setOrderId(request.getOrderId());

        boolean isFragile = false;
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;

        Set<UUID> productIds = request.getProducts().keySet().stream()
            .map(UUID::fromString)
            .collect(Collectors.toSet());
        Map<UUID, WarehouseItem> warehouseItems = warehouseRepository.findAllById(productIds).stream()
            .collect(Collectors.toMap(WarehouseItem::getProductId, item -> item));

        for (Entry<String, Long> orderEntry : request.getProducts().entrySet()) {
            UUID productId = UUID.fromString(orderEntry.getKey());
            Long requestedQuantity = orderEntry.getValue();

            WarehouseItem item = warehouseItems.get(productId);
            if (item == null) {
                log.error("Product with id: {} not found during order assembly for order id: {}",
                    productId, request.getOrderId());
                throw new ProductNotFoundException(productId);
            }
            if (item.getQuantity() < requestedQuantity) {
                log.warn("Insufficient quantity for product id: {} while assembling order id: {}. Requested: {}, Available: {}",
                    productId, request.getOrderId(), requestedQuantity, item.getQuantity());
                throw new InsufficientQuantityException(productId, requestedQuantity,
                    item.getQuantity());
            }
            if (item.getIsFragile()) {
                isFragile = true;
            }

            item.setQuantity(item.getQuantity() - requestedQuantity);

            BookedItem bookedItem = warehouseMapper.toBookedItem(item);
            bookedItem.setQuantity(requestedQuantity);
            booking.addItem(bookedItem);
            totalWeight = totalWeight.add(
                item.getWeightKg().multiply(BigDecimal.valueOf(requestedQuantity)));
            BigDecimal volume = item.getWidthM().multiply(item.getHeightM()).multiply(item.getDepthM());
            totalVolume = totalVolume.add(volume.multiply(BigDecimal.valueOf(requestedQuantity)));
        }

        warehouseRepository.saveAll(warehouseItems.values());
        bookingRepository.save(booking);

        log.info("Successfully assembled and booked products for order with id: {}", request.getOrderId());

        return new BookedProductsDto()
            .fragile(isFragile)
            .deliveryWeight(totalWeight.doubleValue())
            .deliveryVolume(totalVolume.doubleValue());
    }

    @Transactional
    public void markAsShippedForDelivery(ShippedToDeliveryRequest request) {
        log.debug("Attempting to mark order with id: {} as shipped for delivery id: {}",
            request.getOrderId(), request.getDeliveryId());
        OrderBooking booking = bookingRepository.findById(request.getOrderId())
            .orElseThrow(() -> {
                log.error("Booking for order with id: '{}' not found.", request.getOrderId());
                return new BookingNotFoundException(request.getOrderId());
            });
        booking.setDeliveryId(request.getDeliveryId());
        booking.setStatus(BookingStatus.SHIPPED);
        bookingRepository.save(booking);

        log.info("Booking for order with id: {} successfully marked as shipped with delivery id: {}",
            request.getOrderId(), request.getDeliveryId());
    }

    @Transactional
    public void acceptProductReturn(Map<String, Long> productsToReturn) {
        log.debug("Attempting to accept product return for {} product types.", productsToReturn.size());
        for (Map.Entry<String, Long> returnEntry : productsToReturn.entrySet()) {
            UUID itemId = UUID.fromString(returnEntry.getKey());
            Long quantity = returnEntry.getValue();
            log.debug("Returning {} units of product id: {}", quantity, itemId);
            WarehouseItem item = warehouseRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Product with id: {} not found during return process.", itemId);
                    return new ProductNotFoundException(itemId);
                });
            long newQuantity = item.getQuantity() + quantity;
            item.setQuantity(newQuantity);
            warehouseRepository.save(item);
            log.info("Successfully returned {} units for product id: {}. New quantity: {}",
                quantity, itemId, newQuantity);
        }

        log.info("Successfully processed all product returns.");
    }

    public AddressDto getWarehouseAddress() {
        log.debug("Fetching warehouse address.");
        AddressDto address = new AddressDto()
            .country(CURRENT_ADDRESS)
            .city(CURRENT_ADDRESS)
            .street(CURRENT_ADDRESS)
            .house(CURRENT_ADDRESS)
            .flat(CURRENT_ADDRESS);
        log.debug("Returning warehouse address: {}", address);
        return address;
    }
}