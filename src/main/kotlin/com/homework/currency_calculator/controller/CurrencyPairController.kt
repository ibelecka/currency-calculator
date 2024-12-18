package com.homework.currency_calculator.controller

import com.homework.currency_calculator.dto.CurrencyPairRequest
import com.homework.currency_calculator.entity.CurrencyPairEntity
import com.homework.currency_calculator.service.CurrencyPairService
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/currency-pair")
class CurrencyPairController (private val currencyPairService: CurrencyPairService){

    @GetMapping("")
    fun getAllCurrencyPairs() : ResponseEntity<List<CurrencyPairEntity>> {
        return ResponseEntity.ok(currencyPairService.getAllCurrencyPairs())

    }

    @PostMapping("")
    fun createCurrencyPair(@RequestBody currencyPairRequest: CurrencyPairRequest): ResponseEntity<Any> {
        return try {
            if (!currencyPairService.isCurrencyPairExists(currencyPairRequest)) {
                val createdCurrencyPair = currencyPairService.createFromRequest(currencyPairRequest)
                    ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot find rate for currency pair")
                ResponseEntity.status(HttpStatus.CREATED).body(createdCurrencyPair)
            } else {
                ResponseEntity.status(HttpStatus.CONFLICT).body("Currency pair already exists")
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: ${e.message}")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }


    @PutMapping("")
    fun updateFeeCurrencyPair(@RequestBody currencyPairRequest: CurrencyPairRequest): ResponseEntity<Any> {
        val updatedCurrencyPair = currencyPairService.updateFeeCurrencyPair(currencyPairRequest)
        return updatedCurrencyPair?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).body("No currency pair found with these parameters")
    }


    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long): ResponseEntity<String> {
        return try {
            currencyPairService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Currency pair not found")
        }
    }


    @GetMapping("/refresh")
    fun refresh() : ResponseEntity<Any> {
        return try {
            currencyPairService.fetchDataFromECB()
            ResponseEntity.ok("Updated")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: ${e.message}")
        }
    }
}