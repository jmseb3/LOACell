package com.wonddak.database.model


enum class RaidType(val maxPerson: Int) {
    VALTAN(8),
    VYKAS(8),
    KOUKU(4),
    ABRELSHUD(8),
    ILLIAKAN(8),
    KAYANGEL(4),
    IVORYTOWER(4),
    ETC(8);

    fun toKorString(): String {
        return when (this) {
            VALTAN -> "발탄"
            VYKAS -> "비아키스"
            KOUKU -> "쿠크세이튼"
            ABRELSHUD -> "아브렐슈드"
            ILLIAKAN -> "일리아칸"
            KAYANGEL -> "카앙겔"
            IVORYTOWER -> "상아탑"
            ETC -> "기타"
        }
    }

    fun accessibleDifficulty(): List<Difficulty> {
        return when (this) {
            VALTAN, VYKAS, ABRELSHUD, ETC -> listOf(
                Difficulty.Normal,
                Difficulty.Hard,
                Difficulty.Hell
            )

            KOUKU -> listOf(
                Difficulty.Normal,
                Difficulty.Hell
            )

            ILLIAKAN, KAYANGEL, IVORYTOWER -> listOf(
                Difficulty.Normal,
                Difficulty.Hard
            )
        }
    }

    fun getMinLevel(difficulty: Difficulty, gateway: Int = 0): Int {
        return when (this) {
            VALTAN -> when (difficulty) {
                Difficulty.Normal -> 1415
                Difficulty.Hard -> 1445
                Difficulty.Hell -> 1445
            }

            VYKAS -> when (difficulty) {
                Difficulty.Normal -> 1430
                Difficulty.Hard -> 1460
                Difficulty.Hell -> 1460
            }

            KOUKU -> when (difficulty) {
                Difficulty.Normal -> 1475
                Difficulty.Hard -> 0
                Difficulty.Hell -> 1475
            }

            ABRELSHUD -> when (difficulty) {
                Difficulty.Normal -> when (gateway) {
                    1 -> 1490
                    2 -> 1500
                    3 -> 1520
                    else -> 0
                }

                Difficulty.Hard -> when (gateway) {
                    1 -> 1540
                    2 -> 1550
                    3 -> 1560
                    else -> 0
                }

                Difficulty.Hell -> 1560
            }

            ILLIAKAN -> when (difficulty) {
                Difficulty.Normal -> 1580
                Difficulty.Hard -> 1600
                Difficulty.Hell -> 0
            }

            KAYANGEL -> when (difficulty) {
                Difficulty.Normal -> 1540
                Difficulty.Hard -> 1580
                Difficulty.Hell -> 0
            }

            IVORYTOWER -> when (difficulty) {
                Difficulty.Normal -> 1600
                Difficulty.Hard -> 1620
                Difficulty.Hell -> 0
            }

            ETC -> 0
        }
    }

    fun getMaxGate(): Int {
        return when (this) {
            VALTAN -> 2
            VYKAS -> 3
            KOUKU -> 3
            ABRELSHUD -> 6
            ILLIAKAN -> 3
            KAYANGEL -> 4
            IVORYTOWER -> 4
            ETC -> 0
        }
    }

    fun getMaxParty(): Int {
        return when (this) {
            VALTAN, VYKAS, ABRELSHUD, ILLIAKAN, ETC -> 2
            KOUKU, KAYANGEL, IVORYTOWER -> 1
        }
    }
}