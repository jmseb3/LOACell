package com.wonddak.sharedresources.store

import com.wonddak.loacell.SharedRes
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc

object CommonString {

    object Login {
        fun getInfo1(): StringDesc = StringDesc.Resource(SharedRes.strings.login_info_1)
        fun getInfo2(): StringDesc = StringDesc.Resource(SharedRes.strings.login_info_2)
        fun getAnonymous(): StringDesc = StringDesc.Resource(SharedRes.strings.login_anonymous)
        fun getProgress(): StringDesc = StringDesc.Resource(SharedRes.strings.login_progress)
    }
}