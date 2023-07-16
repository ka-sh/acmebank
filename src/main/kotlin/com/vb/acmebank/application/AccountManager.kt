package com.vb.acmebank.application

import com.vb.acmebank.ports.`in`.responses.BalanceResponse
import java.math.BigDecimal

interface AccountManager {
    fun transfer(from: String, to: String, amount: BigDecimal, currency: String)
    fun getBalance(accountNumber: String): BalanceResponse
}