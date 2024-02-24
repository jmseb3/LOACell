package com.wonddak.loacell.android.ui.room.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.Character
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.room.common.DropDownCharacterNameView


@Composable
fun UserInfoCharacter(
    character : Character,
    representativeCharacter :String
) {
    val doBold = representativeCharacter == character.name
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        DropDownCharacterNameView(
            name = character.name,
            fontWeight =  if (doBold) FontWeight.Bold else FontWeight.Normal
        )
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
    }
}
@Composable
fun UserInfoCharacters(
    userInfo: UserInfo,
    characterList : List<Character>
) {
    val representativeCharacter = userInfo.representativeCharacter
    LazyColumn {
        itemsIndexed(characterList) { index,character ->
            UserInfoCharacter(character,representativeCharacter)
            if (index != characterList.size -1) {
                Divider()
            }
        }
    }
}
