package com.wonddak.loacell.android.ui.room.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.Character

@Composable
fun UserInfoCharacter(
    character : Character
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Text(text = character.name)
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val modifier = Modifier.fillMaxWidth(0.5f)
            Text(
                text = character.className,
                modifier = modifier
            )
            Text(
                text = character.level,
                modifier = modifier
            )
        }
        Divider()
    }
}
@Composable
fun UserInfoCharacters(
    characterNameList :List<String>,
    db :AppDataBase,
) {
    val characterList by db.characterInfoQueriesHelper.getCharacterValueFlow(characterNameList).collectAsState(
        initial = emptyList()
    )
    LazyColumn {
        items(characterList) { character ->
            UserInfoCharacter(character)
        }
    }
}
