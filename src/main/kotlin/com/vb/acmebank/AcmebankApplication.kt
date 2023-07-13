package com.vb.acmebank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AcmebankApplication

fun main(args: Array<String>) {
    runApplication<AcmebankApplication>(*args)
}
