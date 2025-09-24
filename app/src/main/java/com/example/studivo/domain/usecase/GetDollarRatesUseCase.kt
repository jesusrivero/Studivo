package com.example.studivo.domain.usecase

import com.example.studivo.domain.model.DollarRates
import com.example.studivo.domain.repository.DollarRepository
import javax.inject.Inject


class GetDollarRatesUseCase  @Inject constructor(
    private val repository: DollarRepository
) {
    suspend operator fun invoke(): DollarRates {
        return repository.getDollarRates()
    }
}