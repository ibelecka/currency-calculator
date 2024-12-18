package com.homework.currency_calculator.dto

import java.math.BigDecimal

data class CurrencyPairECBRequest (
    val currencyFrom: String,
    val currencyTo: String,
    val rate: BigDecimal
)