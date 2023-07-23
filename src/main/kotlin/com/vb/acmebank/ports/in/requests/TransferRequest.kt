package com.vb.acmebank.ports.`in`.requests

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.math.BigDecimal

data class TransferRequest(
    @field:Pattern(regexp = "[0-9]{8}", message = "Account number must be 8 digits")
    @field:NotBlank(message = "From account number is required")
    val from: String?,
    @field:NotBlank(message = "To account number is required")
    @field:Pattern(regexp = "[0-9]{8}", message = "Account number must be 8 digits")
    val to: String?,
    @field:NotBlank(message = "Currency is required")
    @field:Pattern(regexp = "HKD", message = "Only Hong Kong Dollar is supported")
    val currency: String?,
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @field:Digits(integer = 10, fraction = 2, message = "Amount cannot have more than 2 decimal places")
    val amount: BigDecimal?,

    @field:NotBlank(message = "Reference is required")
    @field:Pattern(regexp = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[89ab][0-9a-f]{3}-[0-9a-f]{12}", message = "Reference must be a valid UUID")
    val reference: String?
)