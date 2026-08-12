/* Hallmark · pre-emit critique: P5 H5 E5 S5 R5 V4
 * component: participant cards · genre: modern-minimal · theme: existing Material 3 blue
 */
package com.wonddak.loacell.ui.raidRoom.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.theme.LoaCellRadius
import com.wonddak.loacell.theme.LoaCellSpace

@Composable
fun UserListView(
    userList: List<UserInfo>,
    navigation: (UserInfo) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(LoaCellSpace.sm),
        verticalArrangement = Arrangement.spacedBy(LoaCellSpace.sm),
    ) {
        items(userList, key = { it.name }) { userInfo ->
            val representative = userInfo.characterList.find {
                it.name == userInfo.representativeCharacter
            }
            Card(
                shape = RoundedCornerShape(LoaCellRadius.card),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 88.dp)
                    .clickable { navigation(userInfo) },
            ) {
                Row(
                    modifier = Modifier.padding(LoaCellSpace.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userInfo.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(LoaCellSpace.xs))
                        Text(
                            text = "대표 캐릭터",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = userInfo.representativeCharacter,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        representative?.let { character ->
                            Text(
                                text = "${character.className} · Lv. ${character.level}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(LoaCellRadius.image),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            text = "${userInfo.characterList.size}명",
                            modifier = Modifier.padding(
                                horizontal = LoaCellSpace.xs,
                                vertical = LoaCellSpace.xxs,
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
    }
}
