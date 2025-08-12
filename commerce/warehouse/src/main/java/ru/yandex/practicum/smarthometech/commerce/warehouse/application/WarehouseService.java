package ru.yandex.practicum.smarthometech.commerce.warehouse.application;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.InsufficientQuantityException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductAlreadyExistsException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.WarehouseItem;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.WarehouseRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest request) {
        log.debug("Registering new product in warehouse with id: {}", request.getProductId());
        if (warehouseRepository.existsById(request.getProductId())) {
            throw new ProductAlreadyExistsException(request.getProductId());
        }
        WarehouseItem newItem = warehouseMapper.newProductRequestToWarehouseItem(request);
        warehouseRepository.save(newItem);
        log.info("Successfully registered new product in warehouse with id: {}", request.getProductId());
    }

    @Transactional
    public void increaseProductQuantity(AddProductToWarehouseRequest request) {
        log.debug("Adding {} units of product id: {}", request.getQuantity(), request.getProductId());
        WarehouseItem item = warehouseRepository.findById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        item.setQuantity(item.getQuantity() + request.getQuantity());
        warehouseRepository.save(item);
        log.info("Successfully added {} units to product id: {}. New quantity: {}",
            request.getQuantity(), request.getProductId(), item.getQuantity());
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
                throw new ProductNotFoundException(productId);
            }
            if (warehouseItem.getQuantity() < requestedQuantity) {
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

    public AddressDto getWarehouseAddress() {
        return new AddressDto()
            .country(CURRENT_ADDRESS)
            .city(CURRENT_ADDRESS)
            .street(CURRENT_ADDRESS)
            .house(CURRENT_ADDRESS)
            .flat(CURRENT_ADDRESS);
    }
}