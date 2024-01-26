package de.nielsfalk.formdsl.forms

import de.nielsfalk.formdsl.dsl.form
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime

const val noodleId = "65ac53ce21c4be2b23f14ed6"

// noodle is something like doodle.com
fun noodle() =
    form {
        id = noodleId
        title = "a noodle survey"
        section {
            textInput {
                description = "Please enter your name"
                placehoder = "Name"
            }
            label("a date")
            selectMulti {
                description = "please select a date"
                option("foo")
                option("bar")
            }

            selectOne {
                description = "please select a One"
                option("foo")
                option("bar")
            }

            selectOne {
                description = "Select a localDateTime"
                option("2020-08-30T18:43".toLocalDateTime())
                option("2020-08-31T17:43".toLocalDateTime())
            }

            selectMulti {
                description = "Select a localDate"
                option("2020-08-30".toLocalDate())
                option("2020-08-31".toLocalDate())
            }

            booleanInput()
            booleanInput { description = "aBoolean Description" }
        }
    }