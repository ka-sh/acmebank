package com.vb.acmebank.application

import com.vb.acmebank.application.exceptions.AccountNotFoundException
import com.vb.acmebank.domain.Account
import com.vb.acmebank.ports.out.AccountJPARepository
import com.vb.acmebank.ports.out.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.util.UUID

class BasicAccountManagerTest {

    @Test
    fun `Successfully Transfer amount between two existing accounts`() {
        val fromAccountNumber = "12345678"
        val toAccountNumber = "88888888"
        val initialBalance = BigDecimal("100").setScale(2).unscaledValue().longValueExact()
        val accountRepository = mockk<AccountJPARepository>()
        val transactionRepository = mockk<TransactionRepository>()
        /**
         * Mockking
         */
        every { accountRepository.findByNumber(fromAccountNumber) } returns Account(
            1,
            fromAccountNumber,
            initialBalance,
            "HKD"
        )
        every { accountRepository.findByNumber(toAccountNumber) } returns Account(
            2,
            toAccountNumber,
            initialBalance,
            "HKD"
        )
        every { accountRepository.save(any()) } answers { firstArg() }
        every { transactionRepository.findByReference(any()) } returns null
        every { transactionRepository.save(any()) } answers { firstArg() }

        val accountManager = BasicAccountManager(accountRepository, transactionRepository)
        accountManager.transfer(fromAccountNumber, toAccountNumber, BigDecimal("100"), "HKD", UUID.randomUUID())

        verify { accountRepository.findByNumber(fromAccountNumber) }
        verify { accountRepository.findByNumber(toAccountNumber) }
        verify(exactly = 2) { accountRepository.save(any()) }
        verify { accountRepository.save(match { it.number == fromAccountNumber && it.balance == 0L }) }
        verify { accountRepository.save(match { it.number == toAccountNumber && it.balance == 20000L }) }
    }

    @Test
    fun `Fails to transfer if source and destination accounts are the same`() {
        val fromAccountNumber = "12345678"
        val toAccountNumber = "12345678"
        val accountRepository = mockk<AccountJPARepository>()
        val transactionRepository = mockk<TransactionRepository>()
        val accountManager = BasicAccountManager(accountRepository,transactionRepository)
        assertThrows(IllegalArgumentException::class.java) {
            accountManager.transfer(fromAccountNumber, toAccountNumber, BigDecimal("100"), "HKD", UUID.randomUUID())
        }
    }

    @Test
    fun `Successfully fetch balance of an existing account`() {
        val accountNumber = "88888888"
        val accountRepository = mockk<AccountJPARepository>()
        val transactionRepository = mockk<TransactionRepository>()

        every { accountRepository.findByNumber(accountNumber) } returns Account(
            1,
            accountNumber,
            BigDecimal("100.01").unscaledValue().longValueExact(),
            "HKD"
        )
        every { transactionRepository.findByReference(any()) } returns null
        every { transactionRepository.save(any()) } answers { firstArg() }
        val accountManager = BasicAccountManager(accountRepository,transactionRepository)
        val balanceResponse = accountManager.getBalance(accountNumber)
        assertNotNull(balanceResponse)
        assertEquals("88888888", balanceResponse.accountNumber)
        assertEquals(BigDecimal("100.01"), balanceResponse.balance)
        assertEquals("HKD", balanceResponse.currency)
        verify { accountRepository.findByNumber(accountNumber) }
    }

    @Test
    fun `Fail to fetch balance of a non-existing account`() {
        val accountNumber = "12345678"
        val accountRepository = mockk<AccountJPARepository>()
        val transactionRepository = mockk<TransactionRepository>()

        every { accountRepository.findByNumber(accountNumber) } returns null
        every { transactionRepository.findByReference(any()) } returns null
        every { transactionRepository.save(any()) } answers { firstArg() }
        val accountManager = BasicAccountManager(accountRepository,transactionRepository)
        assertThrows(AccountNotFoundException::class.java) {
            accountManager.getBalance(accountNumber)
        }
        verify { accountRepository.findByNumber(accountNumber) }
    }
}