package com.wonddak.loacell.android.ui.raid.user

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.loacell.Character
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.database.AppDataBase

@Composable
fun UserInfoCharacters(
    db :AppDataBase,
    characterName: String
) {
    val character by db.getCharacter(characterName).collectAsState(initial = null)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Text(text = characterName)
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val modifier = Modifier.fillMaxWidth(0.5f)
            Text(
                text = character?.className ?:"error",
                modifier = modifier
            )
            Text(
                text = character?.level ?: "error",
                modifier = modifier
            )
        }
        Divider()
    }
}