package com.wonddak.loacell.ui.main

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaCellTopAppBar(
    title: String,
    actionContent: (@Composable RowScope.() -> Unit) = {},
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(title)
        },
        actions = actionContent,
        navigationIcon = {
            onBack?.let {
                IconButton(
                    onClick = {
                        it.invoke()
                    },
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        }
    )
}