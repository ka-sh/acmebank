package com.vb.acmebank.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "transactions")
class Transaction(
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    val id: Long? = null,
    @ManyToOne
    val from: Account,
    @ManyToOne
    val to: Account,
    val amount: Long,
    val currency: String,
    @Column(unique = true)
    val reference: UUID,
    @Enumerated(EnumType.STRING)
    val status: TransactionStatus
)