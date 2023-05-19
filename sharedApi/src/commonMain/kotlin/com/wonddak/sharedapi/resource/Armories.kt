package com.wonddak.sharedapi.resource

import io.ktor.resources.*
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