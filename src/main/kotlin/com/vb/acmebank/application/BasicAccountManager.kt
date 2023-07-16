package com.vb.acmebank.application

import com.vb.acmebank.application.exceptions.AccountNotFoundException
import com.vb.acmebank.ports.`in`.responses.BalanceResponse
import com.vb.acmebank.ports.out.AccountJPARepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class BasicAccountManager(
    private val accountRepository: AccountJPARepository
) : AccountManager {
    @Transactional
    override fun transfer(from: String, to: String, amount: BigDecimal, currency: String) {
        require(from!=to) { "Cannot transfer to the same account" }
        val fromAccount = accountRepository.findByNumber(from) ?: throw AccountNotFoundException("Account ends with ${from.substring(from.length - 4)} not found")
        val toAccount = accountRepository.findByNumber(to) ?: throw AccountNotFoundException("Account ends with ${to.substring(to.length - 4)} not found")
        val updatedFromAccount = fromAccount.withdraw(amount)
        val updatedToAccount = toAccount.deposit(amount)
        accountRepository.save(updatedFromAccount)
        accountRepository.save(updatedToAccount)
    }

    override fun getBalance(accountNumber: String): BalanceResponse {
        val account = accountRepository.findByNumber(accountNumber) ?: throw AccountNotFoundException("Account ends with ${accountNumber.substring(accountNumber.length - 4)} not found")
        return BalanceResponse.fromAccount(account)
    }
}