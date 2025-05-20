package com.github.damianjester.nclient

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.github.damianjester.nclient.settings.Global
import com.github.damianjester.nclient.theme.NClientTheme
import org.koin.core.component.KoinComponent

class NewMainActivity : ComponentActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        val rootComponent = DefaultRootComponent(
            componentContext = defaultComponentContext(),
        )

        setContent {
            NClientTheme(
                darkTheme = Global.getTheme() == Global.ThemeScheme.DARK
            ) {
                Surface {
                    RootContent(rootComponent, Modifier.fillMaxSize())
                }
            }
        }
    }
}
