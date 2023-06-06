package com.wonddak.loacell.store

import cocoapods.FirebaseFirestore.*

actual object RoomHelper {
    actual fun syncInfo(
        userId: String,
        successPerDocAction: (id: String, roomInfo: FBRoomInfo) -> Unit,
        failAction: (error : String) -> Unit,
        successAction: () -> Unit
    ) {
        FIRFirestore.firestore().collectionWithPath("rooms")
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
                       firQuerySnapshot.documents.forEach {document ->
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
                               successPerDocAction(id,fbRoomInfo)
                           }
                       }
                       successAction()
                   }
                }
            }


    }

}