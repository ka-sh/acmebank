package com.vb.acmebank.ports.`in`

import com.vb.acmebank.application.AccountManager
import com.vb.acmebank.ports.`in`.requests.TransferRequest
import com.vb.acmebank.ports.`in`.responses.TransferResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalArgumentException


@RestController
class PostTransferFundsController(
    val accountManager: AccountManager
) {
    @PostMapping("/v1/accounts/transfer")
    fun transferFunds(
        @RequestBody
        @Valid
        request: TransferRequest
    ): ResponseEntity<TransferResponse> {
        accountManager.transfer(
            from = request.from!!,
            to = request.to!!,
            amount = request.amount!!,
            currency = request.currency!!
        )
        return ResponseEntity.ok(TransferResponse("Success", "Transfer completed"))
    }
}
