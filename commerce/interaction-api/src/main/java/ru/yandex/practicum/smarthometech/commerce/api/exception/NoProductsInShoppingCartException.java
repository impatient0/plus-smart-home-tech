package ru.yandex.practicum.smarthometech.commerce.api.exception;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class NoProductsInShoppingCartException extends RuntimeException {

    private final String username;
    private final List<UUID> productIds;

    public NoProductsInShoppingCartException(String username, List<UUID> productIds) {
        super(String.format("User '%s' does not have item with ID '%s' in their shopping cart.", username, productIds.toString()));
        this.username = username;
        this.productIds = productIds;
    }
}
