package ru.yandex.practicum.smarthometech.commerce.warehouse.application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.DimensionDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.WarehouseItem;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "quantity", constant = "0L")
    @Mapping(target = "weightKg", source = "weight")
    @Mapping(target = "isFragile", source = "fragile")
    @Mapping(target = "widthM", source = "dimension.width")
    @Mapping(target = "heightM", source = "dimension.height")
    @Mapping(target = "depthM", source = "dimension.depth")
    WarehouseItem newProductRequestToWarehouseItem(NewProductInWarehouseRequest request);

    @Mapping(target = "width", source = "widthM")
    @Mapping(target = "height", source = "heightM")
    @Mapping(target = "depth", source = "depthM")
    DimensionDto warehouseItemToDimensionDto(WarehouseItem item);
}