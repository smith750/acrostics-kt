package com.necessarysense.signwords.models

data class AcrosticStep(
    val composingWord: String,
    val currentLetter: Int,
    val contributingLetter: Int,
)
