package com.example.studivo.data.repository

import com.example.studivo.data.remote.DollarApiService
import com.example.studivo.data.remote.HexaRateApiService
import com.example.studivo.data.remote.local.datastore.DollarDataStore
import com.example.studivo.domain.model.DollarRates
import com.example.studivo.domain.repository.DollarRepository

class DollarRepositoryImpl(
	private val api: DollarApiService,
	private val hexaApi: HexaRateApiService,
	private val dataStore: DollarDataStore
) : DollarRepository {
	
	override suspend fun getDollarRates(): DollarRates {
		return try {
			// ✅ Intento principal con DolarAPI
			val ratesList = api.getDolarRates()
			val bcv = ratesList.find { it.fuente.lowercase() == "oficial" }?.promedio
				?: throw Exception("BCV no encontrado en DolarAPI")
			
			val nowSec = System.currentTimeMillis() / 1000L
			val rates = DollarRates(
				bcv = bcv,
				timestamp = nowSec
			)
			
			dataStore.saveRates(rates)
			rates
			
		} catch (e: Exception) {
			// ✅ Fallback: HexaRate
			try {
				val fallback = hexaApi.getUsdToVes()
				if (fallback.status_code != 200) throw Exception("HexaRate inválido")
				
				val nowSec = System.currentTimeMillis() / 1000L
				val rates = DollarRates(
					bcv = fallback.data.mid,
					timestamp = nowSec
				)
				
				dataStore.saveRates(rates)
				rates
			} catch (fallbackEx: Exception) {
				// ✅ Si ambas fallan, devuelve caché o propaga excepción
				dataStore.getRates() ?: throw fallbackEx
			}
		}
	}
}
