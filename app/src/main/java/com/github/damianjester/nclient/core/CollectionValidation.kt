package com.github.damianjester.nclient.core

object CollectionValidation {
    fun validateName(name: String): Boolean = name.isNotBlank()
}
