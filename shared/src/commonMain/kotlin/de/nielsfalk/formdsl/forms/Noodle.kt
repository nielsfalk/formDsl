package de.nielsfalk.formdsl.forms

import de.nielsfalk.formdsl.dsl.form

const val noodleId = "65ac53ce21c4be2b23f14ed6"

// noodle is something like doodle.com
fun noodle() =
    form {
        id = noodleId
        title = "a noodle survey"
        section{
            label("select a date")
            selectMulti{
                option("foo","foo")
                option("bar","bar")
            }
        }
    }
