package com.vb.acmebank.ports.`in`

import com.vb.acmebank.application.exceptions.AccountNotFoundException
import com.vb.acmebank.ports.`in`.responses.TransferResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest


@RestControllerAdvice
class CustomControllerAdvice {

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<Map<String, String>> {
        val body = mutableMapOf("error" to (ex.message ?: "Invalid Input"))
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        webRequest: WebRequest
    ): ResponseEntity<Map<String, String>> {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.fieldErrors.forEach {
            errors[it.field] = it.defaultMessage ?: ""
        }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFoundException(ex: AccountNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity(mapOf("error" to (ex.message ?: "Invalid account")), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<TransferResponse> {
        return ResponseEntity(
            TransferResponse(status = "Failed", message = ex.message ?: "Transfer operation failed"),
            HttpStatus.BAD_REQUEST
        )
    }

}