package com.vb.acmebank.ports.`in`.responses

import com.vb.acmebank.domain.Account
import java.math.BigDecimal

data class BalanceResponse(val accountNumber: String, val balance: BigDecimal, val currency: String) {
    companion object {
        fun fromAccount(account: Account): BalanceResponse {
            return BalanceResponse(account.number, BigDecimal.valueOf(account.balance, 2), account.currency)
        }
    }
}