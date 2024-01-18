package de.nielsfalk.formdsl.forms

import de.nielsfalk.formdsl.dsl.form

// noodle is something like doodle.com
fun noodle() =
    form {
        title = "a noodle survey"
        section{
            label("select a date")
            selectMulti{
                option("foo","foo")
                option("bar","bar")
            }
        }
    }

fun main() {
    println("noodle() = ${noodle()}")
}