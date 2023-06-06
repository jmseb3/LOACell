package com.wonddak.loacell.store

import cocoapods.FirebaseFirestore.*
import com.wonddak.database.AppDataBase
import platform.Foundation.NSError

actual object RoomHelper {
    private fun getRoomsRef(): FIRCollectionReference =
        FIRFirestore.firestore().collectionWithPath("rooms")

    private fun getRoomRef(id: String): FIRDocumentReference = getRoomsRef().documentWithPath(id)

    actual fun syncInfo(
        userId: String,
        db: AppDataBase,
        failAction: (error: String) -> Unit,
        successAction: () -> Unit
    ) {
        getRoomsRef()
            .queryWhereFilter(
                FIRFilter.orFilterWithFilters(
                    listOf(
                        FIRFilter.filterWhereField("owner", isEqualTo = userId),
                        FIRFilter.filterWhereField("anonymousUser", arrayContains = userId),
                        FIRFilter.filterWhereField("editableUser", arrayContains = userId),
                        FIRFilter.filterWhereField("enterUser", arrayContains = userId)
                    )
                )
            )
            .getDocumentsWithCompletion { firQuerySnapshot, nsError ->
                firQuerySnapshot?.let {
                    if (nsError != null) {
                        println("JWH : Error getting documents: $nsError")
                        failAction(nsError.localizedDescription)
                    } else {
                        firQuerySnapshot.documents.forEach { document ->
                            if (document is FIRQueryDocumentSnapshot) {
                                val id = document.documentID
                                val data = document.data()

                                val fbRoomInfo = FBRoomInfo(
                                    data["title"] as String,
                                    data["description"] as String,
                                    data["owner"] as String,
                                    data["enterPassword"] as String,
                                    data["anonymousUser"] as List<String>,
                                    data["editableUser"] as List<String>,
                                    data["enterUser"] as List<String>,
                                )
                                println("JWH : $id => $fbRoomInfo")
                                db.roomInfoQueriesHelper.addRoomInfo(
                                    title = fbRoomInfo.title,
                                    description = fbRoomInfo.description,
                                    uniqueId = id,
                                    owner = fbRoomInfo.owner
                                )
                            }
                        }
                        successAction()
                    }
                }
            }
    }

    actual fun checkExist(
        roomId: String,
        successAction: (password: String) -> Unit,
        failAction: () -> Unit
    ) {
        getRoomRef(roomId)
            .getDocumentWithCompletion { firDocumentSnapshot, nsError ->
                if (nsError != null) {
                    failAction()
                } else {
                    if (firDocumentSnapshot?.exists == true) {
                        successAction(firDocumentSnapshot.data()!!["enterPassword"] as String)
                    } else {
                        failAction()
                    }
                }
            }
    }

    actual fun makeInfo(
        title: String,
        description: String,
        password: String,
        owner: String,
        successAction: (id: String) -> Unit
    ) {
        val data = FBRoomInfo(
            title = title,
            description = description,
            enterPassword = password,
            owner = owner
        ).toMap()
        val ref = getRoomsRef().documentWithAutoID()

        ref.setData(data) { err ->
            if (err == null) {
                successAction(ref.documentID)
            }
        }
    }
    actual fun updateUser(
        roomId: String,
        userId: String,
        isAnonymous: Boolean,
        successAction: () -> Unit,
        failAction: (e: String?) -> Unit
    ) {
        val field = if (isAnonymous) "anonymousUser" else "enterUser"
        val value = FIRFieldValue.fieldValueForArrayUnion(listOf(userId))

        getRoomRef(roomId).updateData(
            mapOf(field to value)
        ) { err ->
            if (err == null) {
                successAction()
            } else {
                failAction(err.localizedDescription)
            }

        }
    }

}