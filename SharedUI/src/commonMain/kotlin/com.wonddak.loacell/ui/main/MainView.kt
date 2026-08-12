/* Hallmark · pre-emit critique: P5 H5 E5 S5 R5 V4
 * genre: modern-minimal · tone: utilitarian · anchor hue: existing blue
 * macrostructure: Catalogue · contrast: pass (40–41) · honest: pass (46)
 */
package com.wonddak.loacell.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.wonddak.loacell.Const
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.SetTwiceClose
import com.wonddak.loacell.di.LocalLostArkApi
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.network.LostArkResult
import com.wonddak.loacell.network.lostark.model.EventInfo
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.ui.common.FABInfo
import com.wonddak.loacell.ui.common.OpenableFabMenu
import com.wonddak.loacell.ui.modal.sheet.RoomSheet
import com.wonddak.loacell.ui.rememberWebLauncher
import com.wonddak.loacell.theme.LoaCellRadius
import com.wonddak.loacell.theme.LoaCellSpace
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import kotlinx.coroutines.launch
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.room_enter
import loacell.sharedui.generated.resources.room_make

private const val HomeRoomPreviewCount = 3

private sealed interface EventSectionState {
    data object Loading : EventSectionState
    data object Empty : EventSectionState
    data class Content(val events: List<EventInfo>) : EventSectionState
    data object Error : EventSectionState
}

@Composable
fun MainView(
	navController : NavHostController,
	authViewModel : AuthViewModel,
	raidViewModel : RaidViewModel,
) {
	val roomList by raidViewModel.roomList.collectAsState()
	val lostArkApi = LocalLostArkApi.current
	var eventState by remember { mutableStateOf<EventSectionState>(EventSectionState.Loading) }
	var showAllRooms by remember { mutableStateOf(false) }
	val webLauncher = rememberWebLauncher()
	val scope = rememberCoroutineScope()

	suspend fun loadEvents() {
		eventState = EventSectionState.Loading
		eventState = when (val result = lostArkApi.getEvents()) {
			is LostArkResult.Success -> result.data
				.takeIf { it.isNotEmpty() }
				?.let(EventSectionState::Content)
				?: EventSectionState.Empty
			is LostArkResult.Fail,
			is LostArkResult.FailOnlyMsg -> EventSectionState.Error
		}
	}

	LaunchedEffect(Unit) {
		loadEvents()
	}

	LaunchedEffect(authViewModel.user) {
		//메인에서 유저 정보에 변동이 생긴 경우
		if (authViewModel.user == null) {
			//로그아웃 된경우
			//다시 로그인으로 보낸다.
			navController.navigate(Const.NAV_LOGIN) {
				popUpTo(Const.NAV_MAIN) {
					inclusive = true
					saveState = true
				}
			}
		} else {
			//로그인 된경우
			//다시 로그인으로 보낸다.
			raidViewModel.startObserveRoom(authViewModel.user!!.uid)
		}
		//별개로 raidData는 메인에 오면 계속 탐색할 필요가 없다.
		raidViewModel.stopObserveRaidInfo()
	}
	val fabStatus = rememberModalStatus()
	SetTwiceClose()
	SetBackAction(fabStatus.status) {
		fabStatus.hide()
	}
	val roomAddSheet = rememberModalStatus()
	Scaffold(
		topBar = {
			LoaCellTopAppBar(
				"LoaCell",
				actionContent = {
					IconButton(
						onClick = {
							navController.navigate(Const.NAV_SETTING) {
								launchSingleTop = true
							}
						},
					) {
						Icon(Icons.Filled.Settings, contentDescription = "설정")
					}
				}
			)
		},
		floatingActionButton = {
			OpenableFabMenu(
				fabStatus,
				arrayListOf(
					FABInfo.Label(Res.drawable.room_enter, "입장") {
						navController.navigate(Const.NAV_ROOM_ENTER_MAIN) {
							launchSingleTop = true
						}
					}
				).also { list ->
					authViewModel.user?.let {
						if (!it.isAnonymous) {
							list.add(FABInfo.Label(Res.drawable.room_make, "만들기") {
								roomAddSheet.show()
							})
						}
					}
				}
			)
		}
	) { innerPadding ->
		LazyColumn(
			modifier = Modifier.fillMaxSize().padding(innerPadding),
			contentPadding = PaddingValues(vertical = LoaCellSpace.md),
			verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(LoaCellSpace.xs),
		) {
			item {
				HomeActionEntry(
					onClick = {
						navController.navigate(Const.NAV_ROOM_ENTER_MAIN) {
							launchSingleTop = true
						}
					},
				)
			}
			item { HomeSectionTitle("내 파티 (${roomList.size})") }
			if (roomList.isEmpty()) {
				item {
					EmptyRoomEntry(
						onClick = {
							navController.navigate(Const.NAV_ROOM_ENTER_MAIN) {
								launchSingleTop = true
							}
						},
					)
				}
			} else {
				items(if (showAllRooms) roomList else roomList.take(HomeRoomPreviewCount)) { roomInfo ->
					Card(
						onClick = {
							scope.launch {
								raidViewModel.setRoomId(roomInfo, authViewModel.user?.uid)
								navController.navigate(Const.NAV_ROOM) {
									launchSingleTop = true
								}
							}
						},
						modifier = Modifier.fillMaxWidth().padding(horizontal = LoaCellSpace.md),
						shape = RoundedCornerShape(LoaCellRadius.card),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
						),
					) {
						RoomInfoRow(roomInfo)
					}
				}
				if (roomList.size > HomeRoomPreviewCount) {
					item {
						RoomListToggle(
							showAllRooms = showAllRooms,
							roomCount = roomList.size,
							onClick = { showAllRooms = !showAllRooms },
						)
					}
				}
			}
			item { HomeSectionTitle("진행 중인 이벤트") }
			item {
				EventSection(
					state = eventState,
					onEventClick = { webLauncher.launchWeb(it.link) },
					onRetry = { scope.launch { loadEvents() } },
				)
			}
		}
		RoomSheet(
			roomAddSheet,
			null
		) { title, description, password ->
			raidViewModel.createRoom(title, description, password, authViewModel.user!!.uid) {
				roomAddSheet.hide()
			}
		}
	}
}

@Composable
private fun HomeActionEntry(
	onClick: () -> Unit,
) {
	Card(
		onClick = onClick,
		modifier = Modifier.fillMaxWidth().padding(horizontal = LoaCellSpace.md),
		shape = RoundedCornerShape(LoaCellRadius.card),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
	) {
		Row(
			modifier = Modifier.fillMaxWidth().padding(LoaCellSpace.md),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Surface(
				shape = RoundedCornerShape(LoaCellRadius.image),
				color = MaterialTheme.colorScheme.primary,
			) {
				Icon(
					imageVector = Icons.Filled.People,
					contentDescription = null,
					modifier = Modifier.padding(LoaCellSpace.sm).size(24.dp),
					tint = MaterialTheme.colorScheme.onPrimary,
				)
			}
			Column(modifier = Modifier.weight(1f).padding(start = LoaCellSpace.sm)) {
				Text("파티 찾기", style = MaterialTheme.typography.titleMedium)
				Text(
					text = "참여할 파티를 찾아 바로 시작하세요.",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer,
				)
			}
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowForward,
				contentDescription = "파티 찾기",
				tint = MaterialTheme.colorScheme.onPrimaryContainer,
			)
		}
	}
}

@Composable
private fun HomeSectionTitle(title: String) {
	Text(
		modifier = Modifier.padding(start = LoaCellSpace.md, top = LoaCellSpace.sm, bottom = LoaCellSpace.xxs),
		text = title,
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
private fun RoomListToggle(
	showAllRooms: Boolean,
	roomCount: Int,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth().padding(horizontal = LoaCellSpace.md),
		horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
	) {
		TextButton(onClick = onClick) {
			Text(if (showAllRooms) "파티 목록 접기" else "전체 ${roomCount}개 보기")
		}
	}
}

@Composable
private fun EmptyRoomEntry(
    onClick: () -> Unit,
) {
	Card(
		onClick = onClick,
		modifier = Modifier.fillMaxWidth().padding(horizontal = LoaCellSpace.md),
		shape = RoundedCornerShape(LoaCellRadius.card),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
	) {
		Text(
			modifier = Modifier.padding(LoaCellSpace.md),
			text = "참여 중인 파티가 없습니다. 위에서 파티를 찾아보세요.",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun EventSection(
	state: EventSectionState,
	onEventClick: (EventInfo) -> Unit,
	onRetry: () -> Unit,
) {
	when (state) {
		EventSectionState.Loading -> {
			Box(
				modifier = Modifier.fillMaxWidth().height(64.dp),
				contentAlignment = Alignment.Center,
			) {
				CircularProgressIndicator(modifier = Modifier.size(24.dp))
			}
		}
		EventSectionState.Empty -> EventStatus("현재 진행 중인 이벤트가 없습니다.")
		EventSectionState.Error -> EventStatus(
			text = "이벤트를 불러오지 못했습니다.",
			actionLabel = "다시 시도",
			onAction = onRetry,
		)
		is EventSectionState.Content -> {
			LazyRow(
				contentPadding = PaddingValues(horizontal = LoaCellSpace.md),
				horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(LoaCellSpace.sm),
			) {
				items(state.events) { event ->
					EventEntry(event = event, onClick = { onEventClick(event) })
				}
			}
		}
	}
}

@Composable
private fun EventStatus(
	text: String,
	actionLabel: String? = null,
	onAction: (() -> Unit)? = null,
) {
	Row(
		modifier = Modifier.fillMaxWidth().padding(horizontal = LoaCellSpace.md, vertical = LoaCellSpace.sm),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			modifier = Modifier.weight(1f),
			text = text,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		if (actionLabel != null && onAction != null) {
			TextButton(onClick = onAction) {
				Text(actionLabel)
			}
		}
	}
}

@Composable
private fun EventEntry(
	event: EventInfo,
	onClick: () -> Unit,
) {
	Card(
		onClick = onClick,
		modifier = Modifier.width(232.dp),
		shape = RoundedCornerShape(LoaCellRadius.card),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
	) {
		Column {
			SubcomposeAsyncImage(
				model = ImageRequest.Builder(LocalPlatformContext.current)
					.data(event.thumbnail)
					.crossfade(true)
					.build(),
				loading = {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(116.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            content = {},
                        )
				},
				error = {
					Box(modifier = Modifier.fillMaxWidth().height(116.dp), contentAlignment = Alignment.Center) {
						Icon(
							imageVector = Icons.Filled.Event,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.onSurfaceVariant,
						)
					}
				},
				contentDescription = null,
				contentScale = ContentScale.Crop,
				modifier = Modifier.fillMaxWidth().height(116.dp).clip(RoundedCornerShape(LoaCellRadius.card)),
			)
			Column(modifier = Modifier.padding(LoaCellSpace.sm)) {
				Text(
					text = event.title,
					style = MaterialTheme.typography.titleSmall,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis,
				)
				Spacer(Modifier.height(LoaCellSpace.xxs))
				Text(
					text = "${event.startDate.toEventDate()} ~ ${event.endDate.toEventDate()}",
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
		}
	}
}

private fun String.toEventDate(): String = take(10).replace('-', '.')

@Composable
private fun RoomInfoRow(
	room : RoomInfo,
) {
	Row(
		modifier = Modifier.fillMaxWidth().padding(LoaCellSpace.md),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = room.title,
				style = MaterialTheme.typography.titleMedium,
				overflow = TextOverflow.Ellipsis,
				maxLines = 1,
			)
			Text(
				modifier = Modifier.fillMaxWidth().padding(top = LoaCellSpace.xxs),
				text = room.description.ifBlank { "방 설명이 없습니다." },
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				overflow = TextOverflow.Ellipsis,
				maxLines = 2,
			)
		}
		Icon(
			imageVector = Icons.AutoMirrored.Filled.ArrowForward,
			contentDescription = "방 열기",
			tint = MaterialTheme.colorScheme.primary,
		)
	}
}
