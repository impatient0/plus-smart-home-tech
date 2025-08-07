package ru.yandex.practicum.smarthometech.commerce.store.application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductState;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.QuantityState;
import ru.yandex.practicum.smarthometech.commerce.store.domain.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "productName", source = "name")
    ProductDto productToProductDto(Product product);

    @Mapping(target = "name", source = "productName")
    Product productDtoToProduct(ProductDto productDto);

    ru.yandex.practicum.smarthometech.commerce.store.domain.ProductCategory toDomain(
        ProductCategory apiCategory);

    ru.yandex.practicum.smarthometech.commerce.store.domain.ProductState toDomain(
        ProductState apiState);

    ru.yandex.practicum.smarthometech.commerce.store.domain.QuantityState toDomain(
        QuantityState apiState);
}
