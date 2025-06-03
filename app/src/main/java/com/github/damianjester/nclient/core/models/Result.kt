package com.github.damianjester.nclient.core.models

sealed interface Result<out T, out E> {
    data class Ok<T>(val value: T) : Result<T, Nothing>

    data class Err<E>(val cause: E) : Result<Nothing, E>
}
