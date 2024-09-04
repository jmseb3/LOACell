package com.wonddak.loacell

object Const {
    //DEPTH1
    const val NAV_SPLASH = "nav_splash"
    const val NAV_LOGIN = "nav_login"
    const val NAV_MAIN = "nav_home"

    //DEPTH2
    const val NAV_ROOM = "nav_room"
    const val NAV_SETTING = "nav_setting"

    //DEPTH3
    const val NAV_RAID_DETAIL_ARG = "raidId"
    const val NAV_RAID_DETAIL_MAIN = "nav_raid_detail/"
    const val NAV_RAID_DETAIL = "$NAV_RAID_DETAIL_MAIN{$NAV_RAID_DETAIL_ARG}"
    const val NAV_RAID_ADD = "nav_raid_add"

    const val NAV_USER_DETAIL_ARG = "userName"
    const val NAV_USER_DETAIL_MAIN = "nav_user_detail/"
    const val NAV_USER_DETAIL = "$NAV_USER_DETAIL_MAIN{$NAV_USER_DETAIL_ARG}"
}