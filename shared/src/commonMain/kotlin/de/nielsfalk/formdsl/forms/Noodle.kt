package de.nielsfalk.formdsl.forms

import de.nielsfalk.formdsl.dsl.form
import kotlinx.datetime.toLocalDateTime

const val noodleId = "65ac53ce21c4be2b23f14ed6"

// noodle is something like doodle.com
fun noodle() =
    form {
        id = noodleId
        title = "a Noodle survey"
        label("Noodle is something like Doodle")
        section {
            id= "noodleSection"
            textInput {
                id = "name"
                description = "Please enter your name"
                placehoder = "Name"
            }

            selectMulti {
                description = "Do you have time on"
                option("2024-08-30T18:43".toLocalDateTime())
                option("2024-08-31T18:43".toLocalDateTime())
            }
        }
    }