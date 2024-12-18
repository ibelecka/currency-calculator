package com.homework.currency_calculator.controller

import com.homework.currency_calculator.dto.CurrencyConversionRequest
import com.homework.currency_calculator.service.CurrencyPairService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/public/")
class CurrencyConvertController(private val currencyPairService: CurrencyPairService) {

    @GetMapping("convert")
    fun getConversion(
        @RequestParam currencyFrom: String,
        @RequestParam currencyTo: String,
        @RequestParam amount: String
    ): ResponseEntity<Any> {
        val currencyConversionRequest = CurrencyConversionRequest(currencyFrom, currencyTo, BigDecimal(amount))
        return currencyPairService.convert(currencyConversionRequest)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No currency pair found with these parameters")
    }
}