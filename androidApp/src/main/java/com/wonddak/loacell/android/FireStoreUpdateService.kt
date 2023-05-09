package com.wonddak.loacell.android

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.loacell.android.util.NotificationUtil
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.DriverFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class FireStoreUpdateService : Service() {
    companion object {
        private const val TAG = "FireStoreService"

        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"


        private var _nowRoomId = MutableStateFlow("")
        fun setRoomId(id: String) {
            _nowRoomId.value = id
        }

        val nowRoomId: StateFlow<String> get() = _nowRoomId
    }

    private lateinit var db: AppDataBase

    override fun onCreate() {
        super.onCreate()
        db = AppDataBase(DriverFactory(this))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        NotificationUtil.makeNotificationChannel(this)
        val builder = NotificationCompat.Builder(this, NotificationUtil.channelId).apply {
            setContentTitle("LoaCell")
            setContentText("데이터 동기화 중입니다.")
            setChannelId(NotificationUtil.channelId)
            setSmallIcon(com.wonddak.sharedresources.R.drawable.add)
        }.build()
        startForeground(10, builder)

        if (intent?.action != null && intent.action.equals(
                ACTION_STOP, ignoreCase = true
            )
        ) {
            stopSelf()
        }
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                observeUserNames()
            }
            launch {

            }
        }

        return START_STICKY
    }

    suspend fun observeUserNames() {
        nowRoomId.collect { roomId ->
            if (roomId.isEmpty()) {
                return@collect
            }
            Firebase.firestore.collection("rooms")
                .document(roomId)
                .collection("users")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        Log.i(TAG, "Listen Users")
                        // 현재 방에 있는 유저 목록 가져옴
                        val userList =
                            db.getUsersByRoomIdValue(roomId).map { it.name }.toMutableSet()

                        // 이름 조회..
                        value.documents.forEach {
                            val userName = it.id
                            val representativeCharacter =
                                it.data!!["representativeCharacter"] as String
                            //이미 값이 있는 경우
                            if (userName in userList) {
                                //업데이트
                                db.updateUserRepresentativeCharacter(
                                    userName,
                                    roomId,
                                    representativeCharacter
                                )
                                userList.remove(userName)
                            } else {
                                //없는 경우 추가
                                db.addUser(userName, roomId, representativeCharacter)
                            }
                            observeUserCharacters(roomId,userName)
                        }
                        // 동작이 끝난후 남아있다면
                        userList.forEach { name ->
                            db.deleteUserName(name, roomId)
                        }
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }
        }
    }

    fun observeUserCharacters(nowRoomId:String, userName:String) {
        Firebase.firestore.collection("rooms")
            .document(nowRoomId)
            .collection("users")
            .document(userName)
            .collection("characters")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    Log.i(TAG, "Listen $userName characters")
                    val characterList = db.getCharactersValue(userName,nowRoomId).map { it.name }.toMutableSet()
                    value.documents.forEach {
                        val characterName  = it.id
                        Log.d(TAG,characterName)
                        val className = it.data!!["className"] as String
                        val level = it.data!!["level"] as String
                        val server = it.data!!["server"] as String
                        //이미 값이 있는 경우
                        if (characterName in characterList) {
                            //업데이트
                            db.updateUserCharacterInfo(
                                characterName,
                                nowRoomId,
                                server,
                                className,
                                level
                            )
                            characterList.remove(characterName)
                        } else {
                            //없는 경우 추가
                            db.addCharacter(
                                characterName,
                                userName,
                                nowRoomId,
                                server,
                                className,
                                level
                            )
                        }
                        // 동작이 끝난후 남아있다면
                        characterList.forEach { name ->
                            db.deleteCharacter(name, nowRoomId)
                        }
                    }
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

}