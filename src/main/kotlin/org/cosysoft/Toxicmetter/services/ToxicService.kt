package org.cosysoft.Toxicmetter.services

import org.json.JSONObject
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Service
class ToxicService {

    val toxicCollection: MutableMap<Date, String> = mutableMapOf()

    var currentToxicLevel: Number = 0

    fun increase(deviceId: String) {
        val toxicCalculationDateTimeStart = getTimeSecondsAgo(secondsAmountForToxicCalculation)
        val isAlreadyVoted = toxicCollection.filter { it.key > toxicCalculationDateTimeStart }.values.contains(deviceId)

        if (isAlreadyVoted) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN);
        } else {
            toxicCollection.put(Date(), deviceId)
            val previousToxicLevel = currentToxicLevel;
            currentToxicLevel = calculateToxicLevel()
            updateBotStatus(currentToxicLevel, previousToxicLevel)
        }
    }

    private fun updateBotStatus(toxicLevel: Number, previousToxicLevel: Number) {
        if (abs(toxicLevel.toInt() - previousToxicLevel.toInt()) < 1) return;
        val updateUrl = "http://172.105.91.137:8081/toxic-level-updated";

        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val toxicLevelJsonObject = JSONObject()
        toxicLevelJsonObject.put("level", toxicLevel)

        val request: HttpEntity<String> = HttpEntity<String>(toxicLevelJsonObject.toString(), headers)
            restTemplate.postForObject(
                updateUrl, request,
                String::class.java
            )
    }

    fun reset() {
        toxicCollection.clear()
    }

    fun getToxicLevel(): Number {
        return currentToxicLevel
    }
    fun calculateToxicLevel(): Number {
        val toxicCalculationDateTimeStart = getTimeSecondsAgo(secondsAmountForToxicCalculation)
        var toxicAverageLevel = toxicCollection.filter { it.key > toxicCalculationDateTimeStart }.size / averageToxicCoefficient
        if (toxicAverageLevel > 4) toxicAverageLevel = 4.0;
        return toxicAverageLevel
            .roundToInt()
    }

    private val secondsAmountForToxicCalculation = 1800

    private val averageToxicCoefficient = 8.75

    private fun getTimeSecondsAgo(secondsAgo: Int): Date {
        return Date(System.currentTimeMillis() - secondsAgo*1000)
    }
}
