package com.homework.currency_calculator.dto

import java.math.BigDecimal

data class CurrencyPairRequest (
    val currencyFrom: String,
    val currencyTo: String,
    var fee: BigDecimal
)