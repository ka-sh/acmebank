package com.vb.acmebank.application

import com.vb.acmebank.ports.`in`.responses.BalanceResponse
import java.math.BigDecimal
import java.util.UUID

interface AccountManager {
    fun transfer(from: String, to: String, amount: BigDecimal, currency: String,reference:UUID)
    fun getBalance(accountNumber: String): BalanceResponse
}