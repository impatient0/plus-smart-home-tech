package ru.yandex.practicum.smarthometech.commerce.store.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductState;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.QuantityState;
import ru.yandex.practicum.smarthometech.commerce.store.domain.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "productName", source = "name")
    ProductDto productToProductDto(Product product);

    @Mapping(target = "name", source = "productName")
    Product productDtoToProduct(ProductDto productDto);

    ru.yandex.practicum.smarthometech.commerce.store.domain.enums.ProductCategory toDomain(
        ProductCategory apiCategory);

    ru.yandex.practicum.smarthometech.commerce.store.domain.enums.ProductState toDomain(
        ProductState apiState);

    ru.yandex.practicum.smarthometech.commerce.store.domain.enums.QuantityState toDomain(
        QuantityState apiState);
}
