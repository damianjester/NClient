package com.github.damianjester.nclient.ui.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.About
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Bookmarks
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Downloads
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Favorites
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Galleries
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.History
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.RandomGallery
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Settings
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme
import kotlinx.coroutines.launch

@Composable
fun NClientModalNavigationDrawer(
    component: NClientDrawerComponent,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    content: @Composable () -> Unit,
) {
    val model by component.model.subscribeAsState()
    val scope = rememberCoroutineScope()

    val onNavigate: (NClientDrawerItem) -> Unit = remember {
        {
            scope.launch {
                drawerState.close()
                component.navigate(it)
            }
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(12.dp))
                    NClientDrawerHeader()
                    HorizontalDivider()

                    NClientDrawerItemSection(
                        items = firstSection,
                        onClick = onNavigate,
                        selectedItem = model.selected
                    )
                    HorizontalDivider()
                    NClientDrawerItemSection(
                        items = secondSection,
                        onClick = onNavigate,
                        selectedItem = model.selected
                    )
                }
            }
        },
        modifier = modifier,
        drawerState = drawerState,
        content = content
    )
}

private val firstSection = listOf(
    Galleries to false,
    Downloads to true,
    RandomGallery to false,
    Favorites to true,
    Bookmarks to true,
    History to false,
)

private val secondSection = listOf(
    Settings to true,
    About to true
)

@PreviewLightDark
@Composable
private fun NClientModalNavigationDrawerPreview() {
    NClientPreviewTheme {
        val component = object : NClientDrawerComponent {
            override val model: Value<NClientDrawerComponent.Model> = MutableValue(
                NClientDrawerComponent.Model(
                    selected = Galleries
                )
            )

            override fun navigate(item: NClientDrawerItem) = Unit
        }

        val drawerState = rememberDrawerState(DrawerValue.Open)

        NClientModalNavigationDrawer(
            component = component,
            drawerState = drawerState
        ) {
            Surface(Modifier.fillMaxSize()) {}
        }
    }
}
