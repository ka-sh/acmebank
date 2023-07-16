package com.vb.acmebank.ports.out

import com.vb.acmebank.domain.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountJPARepository : JpaRepository<Account, Long> {
    fun findByNumber(accountNumber: String): Account?
}