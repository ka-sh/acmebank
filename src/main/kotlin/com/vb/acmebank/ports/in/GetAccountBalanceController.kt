package com.vb.acmebank.ports.`in`

import com.vb.acmebank.application.AccountManager
import com.vb.acmebank.ports.`in`.responses.BalanceResponse
import jakarta.validation.constraints.Pattern
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class GetAccountBalanceController(
    val accountManager: AccountManager
) {
    @GetMapping("/v1/accounts/{accountNumber}/balance")
    fun getBalance(
        @PathVariable
        @Pattern(regexp = "[0-9]{8}", message = "Account number must be 8 digits")
        accountNumber: String
    ): ResponseEntity<BalanceResponse> {
            val balanceResponse = accountManager.getBalance(accountNumber)
            return ResponseEntity.ok(balanceResponse)
    }
}