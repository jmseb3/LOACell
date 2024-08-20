//package com.wonddak.loacell.ext
//
//import com.wonddak.loacell.database.model.RaidType
//import com.wonddak.loacell.RaidInfo
//import com.wonddak.loacell.util.TimeHelper
//import loacell.composeapp.generated.resources.Res
//import loacell.composeapp.generated.resources.raid_abrelshud
//import loacell.composeapp.generated.resources.raid_behemoth
//import loacell.composeapp.generated.resources.raid_echidna
//import loacell.composeapp.generated.resources.raid_egir
//import loacell.composeapp.generated.resources.raid_illiakan
//import loacell.composeapp.generated.resources.raid_ivory_tower
//import loacell.composeapp.generated.resources.raid_kamen
//import loacell.composeapp.generated.resources.raid_kayangel
//import loacell.composeapp.generated.resources.raid_kouku
//import loacell.composeapp.generated.resources.raid_valtan
//import loacell.composeapp.generated.resources.raid_vykas
//import org.jetbrains.compose.resources.DrawableResource
//
//fun RaidInfo.getMinLevel(): Int {
//    return this.type.getMinLevel(this.Difficulty, this.endGateNumber.toInt())
//}
//
//fun RaidInfo.getRaidText(): String {
//    return "${this.type.toKorString()} - ${this.Difficulty.toKorString()}"
//}
//
//fun RaidInfo.getMaxParty(): Int {
//    return this.type.getMaxParty()
//}
//
//fun RaidInfo.makeGateText(): String {
//    return when (this.type) {
//        RaidType.ETC -> {
//            "관문 정보 없음"
//        }
//
//        else -> {
//            "1 ~ ${this.type.getMaxGate(this.Difficulty)} 관문"
//        }
//    }
//}
//
//fun RaidInfo.getImg(): DrawableResource? {
//    return when (this.type) {
//        RaidType.VALTAN -> {
//            Res.drawable.raid_valtan
//        }
//
//        RaidType.VYKAS -> {
//            Res.drawable.raid_vykas
//        }
//
//        RaidType.KOUKU -> {
//            Res.drawable.raid_kouku
//        }
//
//        RaidType.ABRELSHUD -> {
//            Res.drawable.raid_abrelshud
//        }
//
//        RaidType.ILLIAKAN -> {
//            Res.drawable.raid_illiakan
//        }
//
//        RaidType.KAMEN -> {
//            Res.drawable.raid_kamen
//        }
//
//        RaidType.KAYANGEL -> {
//            Res.drawable.raid_kayangel
//        }
//
//        RaidType.IVORYTOWER -> {
//            Res.drawable.raid_ivory_tower
//        }
//
//        RaidType.ECHIDNA -> {
//            Res.drawable.raid_echidna
//        }
//
//        RaidType.BETHEMOTH -> {
//            Res.drawable.raid_behemoth
//        }
//        RaidType.EGIR -> {
//            Res.drawable.raid_egir
//        }
//        else -> {
//            null
//        }
//    }
//}
//
//fun RaidInfo.getDayText(): String =
//    "${this.day.text} ${TimeHelper.makeTimeText(this.hour.toInt(), this.minute.toInt())}"
//
///**
// * 모든 파티 리스트를 가져온다.
// */
//fun RaidInfo.getAllPartyList(): List<String> {
//    val maxParty = this.getMaxParty()
//
//    return when (maxParty) {
//        1 -> {
//            this.party1characterList
//        }
//
//        2 -> {
//            arrayListOf(
//                this.party1characterList,
//                this.party2characterList,
//            ).flatten()
//        }
//
//        4 -> {
//            arrayListOf(
//                this.party1characterList,
//                this.party2characterList,
//                this.party3characterList,
//                this.party4characterList
//            ).flatten()
//        }
//
//        else -> {
//            emptyList()
//        }
//    }
//}
//
///**
// * partyIndex 번호로 partyList를 가져온다.
// */
//fun RaidInfo.getPartyByIndex(index: Int): List<String> {
//    return when (index) {
//        0 -> {
//            this.party1characterList
//        }
//
//        1 -> {
//            this.party2characterList
//        }
//
//        2 -> {
//            this.party3characterList
//        }
//
//        3 -> {
//            this.party4characterList
//        }
//
//        else -> {
//            throw IllegalArgumentException(
//                """
//                    잘못된 index 0~3
//                """.trimIndent()
//            )
//        }
//    }
//}
//
///**
// * Party에 들어간 이름목록 집합을 가져온다.
// */
//fun RaidInfo.getPartNameList(): Set<String> {
//    return getAllPartyList()
//        .toMutableSet()
//        .also {
//            it.remove("")
//        }.toSet()
//}