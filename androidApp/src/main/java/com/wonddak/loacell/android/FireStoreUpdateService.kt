package com.wonddak.loacell.android

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

        return START_NOT_STICKY
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
                        value.documents.forEach {
                            val user = it.id
                            val representativeCharacter = it.data!!["representativeCharacter"] as String
                            db.addUser(roomId, user, representativeCharacter)
                        }
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }
        }
    }

}