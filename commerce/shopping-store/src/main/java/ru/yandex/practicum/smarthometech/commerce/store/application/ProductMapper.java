package ru.yandex.practicum.smarthometech.commerce.store.application;

import org.mapstruct.Mapper;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductState;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.QuantityState;
import ru.yandex.practicum.smarthometech.commerce.store.domain.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto productToProductDto(Product product);

    Product productDtoToProduct(ProductDto productDto);

    ru.yandex.practicum.smarthometech.commerce.store.domain.ProductCategory toDomain(
        ProductCategory apiCategory);

    ru.yandex.practicum.smarthometech.commerce.store.domain.ProductState toDomain(
        ProductState apiState);

    ru.yandex.practicum.smarthometech.commerce.store.domain.QuantityState toDomain(
        QuantityState apiState);
}
