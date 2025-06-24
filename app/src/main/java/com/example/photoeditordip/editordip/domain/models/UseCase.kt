package com.example.photoeditordip.editordip.domain.models

interface UseCase <IN, OUT> {
    suspend operator fun invoke(param: IN): Result<OUT>
}