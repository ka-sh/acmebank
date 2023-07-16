package com.vb.acmebank.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AccountTest{
    @Test
    fun `Successfully withdraw integer amount`(){
        val account = Account(1, "12345678", 10000, "HKD")
        val updated = account.withdraw(BigDecimal("99"))
        assertEquals(100, updated.balance)
    }
    @Test
    fun `Successfully withdraw fractional amount`(){
        val account = Account(1, "12345678", 10000, "HKD")
        val updated = account.withdraw(BigDecimal("10.09"))
        assertEquals(8991, updated.balance)
    }

    @Test
    fun `Fails on withdrawing negative amount`(){
        val account  = Account(1, "12345678", 10000, "HKD")
        assertThrows(IllegalArgumentException::class.java){
            account.withdraw(BigDecimal(-100))
        }
    }

    @Test
    fun `Fails on withdrawing amount lager than existing balance`(){
        val account = Account(1, "12345678", 10000, "HKD")
        assertThrows(IllegalArgumentException::class.java){
            account.withdraw(BigDecimal(1001))
        }
    }

    @Test
    fun `Fails on withdrawing amount with more than 2 decimal places`(){
        val account = Account(1, "12345678", 10000, "HKD")
        assertThrows(IllegalArgumentException::class.java){
            account.withdraw(BigDecimal("10.001"))
        }
    }

    @Test
    fun `Successfully deposit integer amount`(){
        val account = Account(1, "12345678", 10000, "HKD")
        val updated = account.deposit(BigDecimal(100))
        assertEquals(20000, updated.balance)
    }
}