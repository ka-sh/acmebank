package com.vb.acmebank.ports.`in`

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vb.acmebank.ports.`in`.requests.TransferRequest
import com.vb.acmebank.ports.`in`.responses.BalanceResponse
import com.vb.acmebank.ports.`in`.responses.TransferResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@Sql("/db/init_test_db.sql")
@Transactional
@Rollback
@AutoConfigureMockMvc
class PostTransferFundsControllerTest(
    @Autowired
    val mockMvc: MockMvc
) {
    @Test
    fun `Fails to  transfer funds from one account to another if reference is missing`() {
        val request = TransferRequest(
            from = "12345678",
            to = "88888888",
            amount = BigDecimal(100.0),
            currency = "HKD",
            reference = null
        )
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/accounts/transfer")
                .contentType("application/json")
                .content(jacksonObjectMapper().writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.reference").value("Reference is required"))
    }

    @Test
    fun `Duplicate transfer requests with the same reference will result in the same response`() {
        val reference = UUID.randomUUID().toString()
        val request = TransferRequest(
            from = "12345678",
            to = "88888888",
            amount = BigDecimal(100.0),
            currency = "HKD",
            reference = reference
        )
        val expectedBalance =BalanceResponse(accountNumber = request.from!!, balance = BigDecimal(999900.0), currency =  "HKD")
         executeTransfer(request)
            .assertSuccessfulTransferStatus()
        fetchBalance(request.from!!)
            .assertFetchedBalanceEqual(
                expectedBalance
            )
        //Execute a duplicate transfer request with the same reference
        executeTransfer(request)
            .assertSuccessfulTransferStatus()
        fetchBalance(request.from!!)
            .assertFetchedBalanceEqual(expectedBalance.copy(balance = BigDecimal(999900.0)))
    }
    @Test
    fun `Successfully execute multiple transfer requests with different references`(){
        val getRequestWithDifferentReference = fun() = TransferRequest(
            from = "12345678",
            to = "88888888",
            amount = BigDecimal(100.0),
            currency = "HKD",
            reference = UUID.randomUUID().toString()
        )
        executeTransfer(getRequestWithDifferentReference())
            .assertSuccessfulTransferStatus()
        fetchBalance("12345678")
            .assertFetchedBalanceEqual(BalanceResponse(accountNumber = "12345678", balance = BigDecimal(999900.0), currency =  "HKD"))
        fetchBalance("88888888")
            .assertFetchedBalanceEqual(BalanceResponse(accountNumber = "88888888", balance = BigDecimal(1000100.0), currency =  "HKD"))
        executeTransfer(getRequestWithDifferentReference())
            .assertSuccessfulTransferStatus()
        fetchBalance("12345678")
            .assertFetchedBalanceEqual(BalanceResponse(accountNumber = "12345678", balance = BigDecimal(999800.0), currency =  "HKD"))
        fetchBalance("88888888")
            .assertFetchedBalanceEqual(BalanceResponse(accountNumber = "88888888", balance = BigDecimal(1000200.0), currency =  "HKD"))

    }

    private fun executeTransfer(transferRequest: TransferRequest): ResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/accounts/transfer")
                .contentType("application/json")
                .content(jacksonObjectMapper().writeValueAsString(transferRequest))
        )
    private fun fetchBalance(accountNumber: String): ResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/accounts/$accountNumber/balance")
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk())

    private fun ResultActions.assertFetchedBalanceEqual(expectedBalance: BalanceResponse) = this.andExpect {
        val balanceResponse =
            jacksonObjectMapper().readValue(it.response.contentAsString, BalanceResponse::class.java)
        assertEquals(expectedBalance.accountNumber, balanceResponse.accountNumber)
        assertEquals(expectedBalance.currency, balanceResponse.currency)
        assertEquals(expectedBalance.balance.longValueExact(), balanceResponse.balance.longValueExact())
    }
    private fun ResultActions.assertSuccessfulTransferStatus() = this.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect {
            val transferResponse =
                jacksonObjectMapper().readValue(it.response.contentAsString, TransferResponse::class.java)
            assertEquals("Success", transferResponse.status)
            assertEquals("Transfer completed", transferResponse.message)
        }



}