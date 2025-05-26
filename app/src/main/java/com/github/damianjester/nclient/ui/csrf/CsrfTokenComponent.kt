package com.github.damianjester.nclient.ui.csrf

import android.util.Log
import com.arkivanov.decompose.ComponentContext

interface CsrfTokenComponent {
    fun onCsrfTokenFound(token: String)
}

class DefaultCsrfTokenComponent(
    componentContext: ComponentContext,
) : CsrfTokenComponent, ComponentContext by componentContext {
    override fun onCsrfTokenFound(token: String) {
        Log.wtf("token", "CSRF token found: $token")
    }
}
