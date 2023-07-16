package com.vb.acmebank.application

import com.vb.acmebank.application.exceptions.AccountNotFoundException
import com.vb.acmebank.domain.Account
import com.vb.acmebank.ports.out.AccountJPARepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class BasicAccountManagerTest {
    /**
     * 12345678, and 88888888
     */

    @Test
    fun `Successfully fetch balance of an existing account`() {
        val accountNumber = "88888888"
        val accountRepository = mockk<AccountJPARepository>()
        every { accountRepository.findByNumber(accountNumber) } returns Account(
            1,
            accountNumber,
            BigDecimal("100.01").unscaledValue().longValueExact(),
            "HKD"
        )
        val accountManager = BasicAccountManager(accountRepository)
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
        every { accountRepository.findByNumber(accountNumber) } returns null
        val accountManager = BasicAccountManager(accountRepository)
        assertThrows(AccountNotFoundException::class.java) {
            accountManager.getBalance(accountNumber)
        }
        verify { accountRepository.findByNumber(accountNumber) }
    }
}