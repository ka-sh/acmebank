package com.vb.acmebank.application

import com.vb.acmebank.application.exceptions.AccountNotFoundException
import com.vb.acmebank.domain.Account
import com.vb.acmebank.domain.Transaction
import com.vb.acmebank.domain.TransactionStatus
import com.vb.acmebank.ports.`in`.responses.BalanceResponse
import com.vb.acmebank.ports.out.AccountJPARepository
import com.vb.acmebank.ports.out.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class BasicAccountManager(
    private val accountRepository: AccountJPARepository,
    private val transactionRepository: TransactionRepository
) : AccountManager {
    @Transactional
    override fun transfer(from: String, to: String, amount: BigDecimal, currency: String, reference: UUID) {
        require(from != to) { "Cannot transfer to the same account" }
        if(isDuplicateRequest(reference)) return
        val fromAccount = getAccount(from)
        val toAccount = getAccount(to)
        val updatedFromAccount = fromAccount.withdraw(amount)
        val updatedToAccount = toAccount.deposit(amount)
        accountRepository.save(updatedFromAccount)
        accountRepository.save(updatedToAccount)
        appendTransactionHistory(fromAccount,toAccount,amount.setScale(2).longValueExact(),currency,reference,TransactionStatus.COMPLETED)
    }

    override fun getBalance(accountNumber: String): BalanceResponse {
        val account = accountRepository.findByNumber(accountNumber)
            ?: throw AccountNotFoundException("Account ends with ${accountNumber.substring(accountNumber.length - 4)} not found")
        return BalanceResponse.fromAccount(account)
    }

    private fun isDuplicateRequest(reference: UUID) = transactionRepository.findByReference(reference) != null
    private fun appendTransactionHistory(from:Account,to:Account,amount:Long,currency:String,reference:UUID,status:TransactionStatus){
        transactionRepository.save(Transaction(
            from=from,
            to=to,
            amount=amount,
            currency=currency,
            reference=reference,
            status=status
        ))
    }
    private fun getAccount(accountNumber: String): Account {
        return accountRepository.findByNumber(accountNumber)
            ?: throw AccountNotFoundException("Account ends with ${accountNumber.substring(accountNumber.length - 4)} not found")
    }
}