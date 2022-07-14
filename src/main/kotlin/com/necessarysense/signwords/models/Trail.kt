package com.necessarysense.signwords.models

import kotlinx.collections.immutable.PersistentList

data class Trail<S>(val currentStep: S, val previousSteps: PersistentList<S>)
