package com.wonddak.loacell.sharedapi.lostark.armories


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class EquipmentItem(
    @SerialName("Grade")
    val grade: String,
    @SerialName("Icon")
    val icon: String,
    @SerialName("Name")
    val name: String,
    @SerialName("Tooltip")
    val tooltip: String,
    @SerialName("Type")
    val type: String,
) {
    private fun makeJsonString(): String {
        return tooltip.replace("/r/n", "").replace("\\\"", "\"")
    }

    private fun makeJsonObject(): JsonElement {
        return Json.parseToJsonElement(makeJsonString())
    }


    fun getQualityValue(): String {
        return makeJsonObject().jsonObject["Element_001"]!!.jsonObject["value"]!!.jsonObject["qualityValue"]!!.toString()
    }

    //무기,상의,하의,장갑,투구,어꺠의 아이템 레벨을 가져온다.
    fun getItemLevel(): String {
        if (type !in setOf("무기", "상의", "하의", "장갑", "투구", "어깨")) {
            return ""
        }
        return makeJsonObject().jsonObject["Element_001"]!!.jsonObject["value"]!!.jsonObject["leftStr2"]!!.toString()
            .replace("\"", "")
    }

    fun getItemSetOptionLevels(): String {
        if (type !in setOf("무기", "상의", "하의", "장갑", "투구", "어깨")) {
            return ""
        }
        //엘릭서 효과 없는 경우
        makeJsonObject().jsonObject["Element_008"]!!.let { part ->
            if (part.jsonObject["type"]!!.toString().replace("\"", "") == "ItemPartBox") {
                return part.jsonObject["value"]!!.jsonObject["Element_001"]!!.toString()
                    .replace("\"", "")
            }
        }
        //엘릭서 효과 있는 경우
        makeJsonObject().jsonObject["Element_009"]!!.let { part ->
            if (part.jsonObject["type"]!!.toString().replace("\"", "") == "ItemPartBox") {
                return part.jsonObject["value"]!!.jsonObject["Element_001"]!!.toString()
                    .replace("\"", "")
            }
        }
        return ""
    }

    fun getElixirOptionLevels(): List<String> {
        if (type !in setOf("상의", "하의", "장갑", "투구", "어깨")) {
            return emptyList()
        }
        //엘릭서 효과 찾기 있는 경우에만 들어감
        val resultList = mutableListOf<String>()
        makeJsonObject().jsonObject["Element_007"]!!.let { part ->
            if (part.jsonObject["type"]!!.toString().replace("\"", "") == "IndentStringGroup") {
                for (i in 0..1) {
                    resultList.add(
                        part.jsonObject["value"]!!.jsonObject["Element_000"]!!.jsonObject["contentStr"]!!.jsonObject["Element_00$i"]!!.jsonObject["contentStr"]!!.toString()
                            .replace("\"", "")
                    )
                }
            }
        }

        return resultList
    }


    // 목걸이 , 반지 , 귀걸이 의 스탯을 가져온다.
    fun getItemStatus(): List<String> {
        if (type !in setOf("목걸이", "반지", "귀걸이")) {
            return emptyList()
        }

        return makeJsonObject().jsonObject["Element_005"]!!.jsonObject["value"]!!.jsonObject["Element_001"]!!.toString()
            .replace("\"", "").split("<BR>")
    }

    // 목걸이 , 반지 , 귀걸이 ,어빌리티 스톤의 각인을 가져온다.
    fun getItemEngraves(): List<String> {
        if (type !in setOf("목걸이", "반지", "귀걸이", "어빌리티 스톤")) {
            return emptyList()
        }
        val item =
            makeJsonObject().jsonObject["Element_006"]!!.jsonObject["value"]!!.jsonObject["Element_000"]!!.jsonObject["contentStr"]!!
        val resultList = mutableListOf<String>()
        for (i in 0..2) {
            resultList.add(
                item.jsonObject["Element_00$i"]!!.jsonObject["contentStr"]!!.toString()
                    .replace("\"", "").replace("<BR>", "")
            )
        }
        return resultList
    }

    //팔찌의 옵션을 가져온다.
    fun getBraceletOptions(): List<String> {
        if (type !in setOf("팔찌")) {
            return emptyList()
        }
        val optionString =
            makeJsonObject().jsonObject["Element_004"]!!.jsonObject["value"]!!.jsonObject["Element_001"]!!.toString()
                .replace("\"", "")
        return optionString.split("<BR>")
    }

    //어빌리티 스톤 의 추가 체력수치를 가져온다.
    fun getItemHealth(): List<String> {
        if (type !in setOf("어빌리티 스톤")) {
            return emptyList()
        }
        val resultList = mutableListOf<String>()
        for (i in 4..5) {
            val item =
                makeJsonObject().jsonObject["Element_00$i"]!!.jsonObject["value"]!!.jsonObject["Element_001"]!!
            resultList.add(item.toString().replace("\"", ""))
        }
        return resultList
    }

}
