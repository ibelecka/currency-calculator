package com.homework.currency_calculator.entity

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.experimental.Accessors
import java.math.BigDecimal

@Entity(name = "currency-pair")
@Accessors(chain = true)
class CurrencyPairEntity (
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(updatable = false, nullable = false)
    var currencyFrom: String,

    @Column(updatable = false, nullable = false)
    var currencyTo: String,

    @Column
    var fee: BigDecimal?,

    @Column(nullable = false)
    var rate: BigDecimal


)