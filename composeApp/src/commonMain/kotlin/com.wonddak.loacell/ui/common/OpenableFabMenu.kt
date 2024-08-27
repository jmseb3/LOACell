package com.wonddak.loacell.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.ModalStatus
import org.jetbrains.compose.resources.DrawableResource

sealed class FABInfo(
    open val img: DrawableResource,
    open val action: () -> Unit,
) {
    data class Default(
        override val img: DrawableResource,
        override val action: () -> Unit,
    ) : FABInfo(img, action)

    data class Label(
        override val img: DrawableResource,
        val title: String,
        override val action: () -> Unit,
    ) : FABInfo(img, action)
}


@Composable
fun OpenableFabMenu(
    modalStatus: ModalStatus,
    menuItem: List<FABInfo>,
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        menuItem.forEach { item ->
            FabMenuItem(modalStatus.status, item)
        }
        FloatingActionButton(
            onClick = { modalStatus.toggle() },
            shape = FloatingActionButtonDefaults.largeShape
        ) {
            Icon(
                if (modalStatus.status) {
                    Icons.Filled.Clear
                } else {
                    Icons.Filled.Add
                }, null
            )
        }
    }
}