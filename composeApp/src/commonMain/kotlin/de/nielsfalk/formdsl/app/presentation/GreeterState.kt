package de.nielsfalk.formdsl.app.presentation

import Greeting

data class GreeterState(
    val showContent: Boolean = false,
    val greeting :String = Greeting().greet()
)