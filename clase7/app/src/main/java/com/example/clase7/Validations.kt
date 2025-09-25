package com.example.clase7

import android.util.Patterns

fun validateEmail (value: String): Pair<Boolean, String> {
    return when {
        value.isEmpty() -> Pair(false, "El email es requerido")
        !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> Pair(false, "El email es invalido")
        else -> Pair(true, "")
    }
}

fun validatePassword(value: String): Pair<Boolean, String> {
    return when {
        value.isEmpty() -> Pair(false, "El password es requerido")
        value.length < 6 -> Pair(false, "El password debe tener al menos 6 caracteres")
        else -> Pair(true, "")
    }
}