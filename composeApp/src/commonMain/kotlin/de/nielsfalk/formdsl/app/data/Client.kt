package de.nielsfalk.formdsl.app.data

import io.ktor.client.HttpClient

expect fun createClient(): HttpClient

expect fun localhost():String