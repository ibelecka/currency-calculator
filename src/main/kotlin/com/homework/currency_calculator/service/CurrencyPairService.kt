package com.homework.currency_calculator.service

import com.homework.currency_calculator.dto.CurrencyPairRequest
import com.homework.currency_calculator.dto.CurrencyPairECBRequest
import com.homework.currency_calculator.dto.CurrencyConversionRequest
import com.homework.currency_calculator.entity.CurrencyPairEntity
import com.homework.currency_calculator.repository.CurrencyPairRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import java.io.InputStream
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId

@Service
@Component
class CurrencyPairService (private val currencyPairRepository: CurrencyPairRepository,

                           @Value("\${currency-conversion.defaultFee}")
                           val defaultFee: BigDecimal,

                           @Value("\${currency-conversion.ecb-api}")
                           val ecbAPI: String
                           ) {
    private var currencyPairRatesMap = mutableMapOf<String, CurrencyPairECBRequest>()

    @PostConstruct
    fun initializeData() {
        fetchDataFromECB()
    }

    fun getCurrencyPairECB(key: String): CurrencyPairECBRequest? = currencyPairRatesMap[key]

    private fun getCurrencyPairRate(key: String): BigDecimal? {
        return getCurrencyPairECB(key)?.rate
    }


    fun getAllCurrencyPairs(): List<CurrencyPairEntity> {
        return currencyPairRepository.findAll()
    }

    fun fetchDataFromECB() {
        val today = LocalDate.now()
        val currentTime = LocalTime.now(ZoneId.of("CET"))

        val ecbDate = when {
            currentTime.isBefore(LocalTime.of(16, 0)) -> today.minusDays(1)  // Before 16:00, use yesterday
            today.dayOfWeek == DayOfWeek.SATURDAY -> today.minusDays(1)  // Saturday, use Friday
            today.dayOfWeek == DayOfWeek.SUNDAY -> today.minusDays(2)  // Sunday, use Friday
            else -> today  // Otherwise, use today's date
        }
        val ecbAPIString = "$ecbAPI${ecbDate.year}-${ecbDate.monthValue}-${ecbDate.dayOfMonth}"
        val client = HttpClient.newHttpClient()

        try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(ecbAPIString))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())

            if (response.statusCode() == 200) {
                response.body()?.let { processECBResponse(it) }
                    ?: throw Exception("Failed to process response body.")
            } else {
                throw Exception("Request failed with response code: ${response.statusCode()}")
            }
        } catch (e: Exception) {
            throw Exception("Error occurred while making the API request.", e)
        }
    }


    private fun processECBResponse(responseBody: InputStream) {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(responseBody)

        document.documentElement.normalize()
        val currencyPairECBRequests = mutableListOf<CurrencyPairECBRequest>()

        // Loop through each <generic:Series> element
        val seriesList = document.getElementsByTagName("generic:Series")
        for (i in 0 until seriesList.length) {
            val seriesElement = seriesList.item(i) as Element

            // Extract currencyFrom and currencyTo
            val currencyFrom = getValueFromElement(seriesElement, "generic:SeriesKey", "CURRENCY")
            val currencyTo = getValueFromElement(seriesElement, "generic:SeriesKey", "CURRENCY_DENOM")

            // Extract the exchange rate
            val rateValue = getValueFromElement(seriesElement, "generic:Obs", "ObsValue")
            val rate = BigDecimal(rateValue)
            currencyPairECBRequests.add(CurrencyPairECBRequest(currencyFrom, currencyTo, rate))
        }

        for (currencyPairECBRequest in currencyPairECBRequests) {
            val key = generateKey(currencyPairECBRequest.currencyFrom, currencyPairECBRequest.currencyTo)
            currencyPairRatesMap.put(key, currencyPairECBRequest)
        }
    }



    private fun getValueFromElement(element: Element, parentTag: String, attributeId: String): String {
        val parentNodeList = element.getElementsByTagName(parentTag)
        for (i in 0 until parentNodeList.length) {
            val parentNode = parentNodeList.item(i) as Element
            var nodeList = parentNode.getElementsByTagName("generic:Value")
            for (j in 0 until nodeList.length) {
                val node = nodeList.item(j) as Element
                if (node.getAttribute("id") == attributeId) {
                    return node.getAttribute("value")
                }
            }

            nodeList = parentNode.getElementsByTagName("generic:ObsValue")
            for (j in 0 until nodeList.length) {
                val node = nodeList.item(j) as Element
                return node.getAttribute("value")
            }
        }
        return ""
    }

    fun isCurrencyPairExists(currencyPairRequest: CurrencyPairRequest): Boolean {
        return currencyPairRepository.findByCurrencyFromAndCurrencyTo(currencyPairRequest.currencyFrom, currencyPairRequest.currencyTo) != null
    }


    fun createFromRequest(currencyPairRequest: CurrencyPairRequest): CurrencyPairEntity? {
        val key = generateKey(currencyPairRequest.currencyFrom, currencyPairRequest.currencyTo)
        val rate = getCurrencyPairRate(key)
        return rate?.let {
            currencyPairRepository.save(
                CurrencyPairEntity(
                    currencyFrom = currencyPairRequest.currencyFrom,
                    currencyTo = currencyPairRequest.currencyTo,
                    fee = currencyPairRequest.fee,
                    rate = it
                )
            )
        }
    }


    private fun generateKey(currencyFrom: String, currencyTo: String): String {
        return "currencyFrom$currencyFrom" + "currencyTo$currencyTo"
    }


    fun updateFeeCurrencyPair(currencyPairRequest: CurrencyPairRequest): CurrencyPairEntity? {
        return currencyPairRepository.findByCurrencyFromAndCurrencyTo(
            currencyPairRequest.currencyFrom, currencyPairRequest.currencyTo
        )?.apply {
            fee = currencyPairRequest.fee
            currencyPairRepository.save(this)
        }
    }


    fun saveOrUpdateCurrencyPairFromECB(currencyPairECBRequest: CurrencyPairECBRequest) {
        currencyPairRepository.findByCurrencyFromAndCurrencyTo(
            currencyPairECBRequest.currencyFrom, currencyPairECBRequest.currencyTo
        )?.apply {
            rate = currencyPairECBRequest.rate
        } ?: currencyPairRepository.save(
            CurrencyPairEntity(
                id = null,
                currencyFrom = currencyPairECBRequest.currencyFrom,
                currencyTo = currencyPairECBRequest.currencyTo,
                fee = null,
                rate = currencyPairECBRequest.rate
            )
        )
    }

    fun deleteById(id: Long) {
        currencyPairRepository.deleteById(id);
    }

    fun deleteAll() {
        currencyPairRepository.deleteAll()
    }

    fun convert(currencyConversionRequest: CurrencyConversionRequest): BigDecimal? {
        val currencyPair = currencyPairRepository
            .findByCurrencyFromAndCurrencyTo(currencyConversionRequest.currencyFrom, currencyConversionRequest.currencyTo)

        val (rate, fee) = currencyPair?.let {
            it.rate to it.fee
        } ?: run {
            val key = generateKey(currencyConversionRequest.currencyFrom, currencyConversionRequest.currencyTo)
            val ecbRate = getCurrencyPairECB(key)?.rate
            ecbRate to defaultFee
        }
        val amount = currencyConversionRequest.amount
        return if (rate == null || fee == null) null
        else (amount - amount.multiply(fee)) * rate
    }

}