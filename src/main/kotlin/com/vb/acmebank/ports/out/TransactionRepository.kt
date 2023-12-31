package com.vb.acmebank.ports.out

import com.vb.acmebank.domain.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TransactionRepository:JpaRepository<Transaction,Long>{
    fun findByReference(reference: UUID): Transaction?
}