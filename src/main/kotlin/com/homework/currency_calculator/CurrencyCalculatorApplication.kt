package com.homework.currency_calculator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan
class CurrencyCalculatorApplication

fun main(args: Array<String>) {
	runApplication<CurrencyCalculatorApplication>(*args)
}
