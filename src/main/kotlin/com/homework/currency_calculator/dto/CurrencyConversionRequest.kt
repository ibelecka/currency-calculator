package com.homework.currency_calculator.dto

import java.math.BigDecimal

data class CurrencyConversionRequest (
    val currencyFrom: String,
    val currencyTo: String,
    var amount: BigDecimal
)