package ru.yandex.practicum.smarthometech.commerce.store.application;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.store.domain.Product;
import ru.yandex.practicum.smarthometech.commerce.store.domain.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.store.domain.ProductState;
import ru.yandex.practicum.smarthometech.commerce.store.domain.ProductRepository;
import ru.yandex.practicum.smarthometech.commerce.store.domain.QuantityState;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(
        ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory apiProductCategory, Pageable pageable) {
        ProductCategory domainCategory =
            productMapper.toDomain(apiProductCategory);

        log.debug("Request for products by category: {}, page: {}", domainCategory, pageable);

        Page<Product> productPage = productRepository.findByCategory(domainCategory, pageable);
        Page<ProductDto> products = productPage.map(productMapper::productToProductDto);

        log.debug("Found {} products on page {}", products.getTotalElements(), products.getNumber());
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
    public boolean setProductQuantityState(UUID productId,
        ru.yandex.practicum.smarthometech.commerce.api.dto.store.QuantityState apiQuantityState) {
        log.debug("Setting quantity state for product id: {}, state: {}", productId, apiQuantityState);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        QuantityState domainQuantityState = productMapper.toDomain(apiQuantityState);
        product.setQuantityState(domainQuantityState);
        productRepository.save(product);

        log.info("Quantity state for product id: {} updated to {}", productId, apiQuantityState);
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