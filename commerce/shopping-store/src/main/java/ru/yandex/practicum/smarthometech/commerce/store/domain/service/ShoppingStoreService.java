package ru.yandex.practicum.smarthometech.commerce.store.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductState;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.smarthometech.commerce.store.domain.entity.Product;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.store.domain.repository.ProductRepository;
import ru.yandex.practicum.smarthometech.commerce.store.domain.mapper.ProductMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import ru.yandex.practicum.smarthometech.commerce.store.exception.ProductNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.debug("Request for products by category: {}, page: {}", category, pageable);

        List<ProductDto> products = productRepository.findByProductCategoryAndProductState(category,
                ProductState.ACTIVE, pageable).stream()
            .map(productMapper::productToProductDto)
            .collect(Collectors.toList());

        log.debug("Found {} products", products.size());
        return products;
    }

    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        log.debug("Creating new product: {}", productDto);

        Product product = productMapper.productDtoToProduct(productDto);
        Product savedProduct = productRepository.save(product);

        log.info("New product created with id: {}", savedProduct.getProductId());
        return productMapper.productToProductDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        log.debug("Updating product: {}", productDto);

        if (productDto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID must not be null for an update");
        }
        if (!productRepository.existsById(productDto.getProductId())) {
            throw new ProductNotFoundException(productDto.getProductId());
        }

        Product product = productMapper.productDtoToProduct(productDto);
        Product updatedProduct = productRepository.save(product);

        log.info("Product with id: {} updated", updatedProduct.getProductId());
        return productMapper.productToProductDto(updatedProduct);
    }

    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        log.debug("Deactivating product with id: {}", productId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);

        log.info("Product with id: {} deactivated", productId);
        return true;
    }

    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.debug("Setting quantity state for product id: {}, state: {}", request.getProductId(), request.getQuantityState());

        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);

        log.info("Quantity state for product id: {} updated to {}", request.getProductId(), request.getQuantityState());
        return true;
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        log.debug("Request for product with id: {}", productId);

        return productRepository.findById(productId)
            .map(productMapper::productToProductDto)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}