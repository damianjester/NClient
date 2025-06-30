package com.github.damianjester.nclient.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.damianjester.nclient.ui.drawer.DrawerMenuButton

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PlaceholderDrawerRootContent(
    modifier: Modifier = Modifier,
    onDrawerClick: () -> Unit,
    topAppBarTitle: String,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(topAppBarTitle)
                },
                navigationIcon = {
                    DrawerMenuButton(onDrawerClick)
                }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                "TODO",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
