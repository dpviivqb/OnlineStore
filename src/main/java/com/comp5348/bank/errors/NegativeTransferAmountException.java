package com.comp5348.bank.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.BAD_REQUEST,
        reason = "Transfer amount must be positive.")
public class NegativeTransferAmountException extends RuntimeException {
}
