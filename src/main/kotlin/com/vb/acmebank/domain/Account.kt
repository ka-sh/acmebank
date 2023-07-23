package com.vb.acmebank.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal

//TODO:Refactor Account Entity to use none Data classes
@Entity
@Table(name = "accounts")
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false, nullable = false)
    var id: Long,
    @Column(unique = true)
    val number: String,
    val balance: Long,
    val currency: String,
    @OneToMany(mappedBy = "from")
    val outgoingTransactions:List<Transaction> = emptyList(),
    @OneToMany(mappedBy = "to")
    val incomingTransactions:List<Transaction> = emptyList()
) {
    companion object {
        private const val SCALE = 2
    }

    fun deposit(amount: BigDecimal): Account {
        require(amount > BigDecimal.ZERO) { "Invalid deposit amount. Must be greater than zero" }
        require(amount.scale() <= SCALE) { "Invalid deposit amount. Must have at most 2 decimal places" }
        val unscaledAmount = amount
            .setScale(SCALE)
            .unscaledValue()
            .longValueExact()
        return this.copy(balance = this.balance + unscaledAmount)
    }

    fun withdraw(amount: BigDecimal): Account {
        require(amount > BigDecimal.ZERO) { "Invalid withdraw amount. Must be greater than zero" }
        require(amount.scale() <= SCALE) { "Invalid withdraw amount. Must have at most 2 decimal places" }
        val unscaledAmount = amount.setScale(SCALE).unscaledValue().longValueExact()
        require(this.balance >= unscaledAmount) { "Insufficient balance" }
        return this.copy(balance = this.balance - unscaledAmount)
    }
}