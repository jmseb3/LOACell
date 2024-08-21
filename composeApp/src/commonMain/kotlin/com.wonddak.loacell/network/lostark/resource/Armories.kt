package com.wonddak.loacell.network.lostark.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/armories/characters")
class Armories() {

    @Serializable
    @Resource("{characterName}")
    class Character(val parent: Armories = Armories(), val characterName: String) {

        @Serializable
        @Resource("profiles")
        class Profiles(val parent: Character)

        @Serializable
        @Resource("equipment")
        class Equipment(val parent: Character)
    }

}