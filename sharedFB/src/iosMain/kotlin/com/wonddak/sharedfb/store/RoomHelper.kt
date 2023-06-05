package com.wonddak.sharedfb.store

import cocoapods.FirebaseFirestore.FIRFilter
import cocoapods.FirebaseFirestore.FIRFirestore
import cocoapods.FirebaseFirestore.FIRQueryDocumentSnapshot

actual object RoomHelper {
    actual fun syncInfo(
        userId: String,
        successPerDocAction: (id: String, roomInfo: FBRoomInfo) -> Unit,
        failAction: (error : String) -> Unit,
        successAction: () -> Unit
    ) {
        FIRFirestore.firestore().collectionGroupWithID(userId)
            .queryWhereFilter(
                FIRFilter.orFilterWithFilters(
                    listOf(
                        FIRFilter.filterWhereField("owner", userId),
                        FIRFilter.filterWhereField("anonymousUser", userId),
                        FIRFilter.filterWhereField("editableUser", userId),
                        FIRFilter.filterWhereField("enterUser", userId)
                    )
                )
            )
            .getDocumentsWithCompletion { firQuerySnapshot, nsError ->
                firQuerySnapshot?.let {
                   if (nsError != null) {
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
                               successPerDocAction(id,fbRoomInfo)
                           }
                       }
                       successAction()
                   }
                }
            }


    }

}