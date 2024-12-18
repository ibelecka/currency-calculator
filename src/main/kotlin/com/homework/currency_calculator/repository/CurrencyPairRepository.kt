package com.homework.currency_calculator.repository

import com.homework.currency_calculator.entity.CurrencyPairEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface  CurrencyPairRepository: JpaRepository<CurrencyPairEntity, Long> {

    fun findByCurrencyFromAndCurrencyTo(currencyFrom: String, currencyTo: String): CurrencyPairEntity?

}