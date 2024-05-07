package com.wonddak.database.model


enum class RaidType(val maxPerson: Int) {
    VALTAN(8),
    VYKAS(8),
    KOUKU(4),
    ABRELSHUD(8),
    ILLIAKAN(8),
    KAMEN(8),
    KAYANGEL(4),
    IVORYTOWER(4),
    ECHIDNA(8),
    BETHEMOTH(16),
    ETC(8);

    fun toKorString(): String {
        return when (this) {
            VALTAN -> "발탄"
            VYKAS -> "비아키스"
            KOUKU -> "쿠크세이튼"
            ABRELSHUD -> "아브렐슈드"
            ILLIAKAN -> "일리아칸"
            KAMEN -> "카멘"
            KAYANGEL -> "카앙겔"
            IVORYTOWER -> "상아탑"
            ECHIDNA -> "에키드나"
            BETHEMOTH -> "베히모스"
            ETC -> "기타"
        }
    }

    //접근 가능한 난이도 정보
    fun accessibleDifficulty(): List<Difficulty> {
        return when (this) {
            VALTAN -> listOf(
                Difficulty.Normal,
                Difficulty.Hard,
                Difficulty.Hell,
                Difficulty.ExtremeNormal,
                Difficulty.ExtremeHard
            )

            VYKAS, ABRELSHUD, ETC -> listOf(
                Difficulty.Normal,
                Difficulty.Hard,
                Difficulty.Hell
            )

            KOUKU -> listOf(
                Difficulty.Normal,
                Difficulty.Hell
            )

            ILLIAKAN, KAMEN, KAYANGEL, IVORYTOWER, ECHIDNA -> listOf(
                Difficulty.Normal,
                Difficulty.Hard
            )

            BETHEMOTH -> listOf(
                Difficulty.Normal
            )
        }
    }

    //입장 레벨
    fun getMinLevel(difficulty: Difficulty, gateway: Int = 0): Int {
        return when (this) {
            VALTAN -> when (difficulty) {
                Difficulty.Normal -> 1415
                Difficulty.Hard -> 1445
                Difficulty.Hell -> 1445
                Difficulty.ExtremeNormal -> 1580
                Difficulty.ExtremeHard -> 1620
            }

            VYKAS -> when (difficulty) {
                Difficulty.Normal -> 1430
                Difficulty.Hard -> 1460
                Difficulty.Hell -> 1460
                else -> 0
            }

            KOUKU -> when (difficulty) {
                Difficulty.Normal -> 1475
                Difficulty.Hell -> 1475
                else -> 0
            }

            ABRELSHUD -> when (difficulty) {
                Difficulty.Normal -> when (gateway) {
                    1, 2 -> 1490
                    3 -> 1500
                    4 -> 1520
                    else -> 0
                }

                Difficulty.Hard -> when (gateway) {
                    1, 2 -> 1540
                    3 -> 1550
                    4 -> 1560
                    else -> 0
                }

                Difficulty.Hell -> 1560
                else -> 0
            }

            ILLIAKAN -> when (difficulty) {
                Difficulty.Normal -> 1580
                Difficulty.Hard -> 1600
                else -> 0
            }

            KAMEN -> when (difficulty) {
                Difficulty.Normal -> 1610
                Difficulty.Hard -> 1630
                else -> 0
            }

            KAYANGEL -> when (difficulty) {
                Difficulty.Normal -> 1540
                Difficulty.Hard -> 1580
                else -> 0
            }

            IVORYTOWER -> when (difficulty) {
                Difficulty.Normal -> 1600
                Difficulty.Hard -> 1620
                else -> 0
            }

            ECHIDNA -> when (difficulty) {
                Difficulty.Normal -> 1620
                Difficulty.Hard -> 1630
                else -> 0
            }

            BETHEMOTH -> when (difficulty) {
                Difficulty.Normal -> 1640
                else -> 0
            }

            ETC -> 0
        }
    }

    //관문 수
    fun getMaxGate(
        difficulty: Difficulty
    ): Int {
        return when (this) {
            VALTAN, VYKAS, ECHIDNA, BETHEMOTH -> 2
            KOUKU, ILLIAKAN, KAYANGEL -> 3
            ABRELSHUD, IVORYTOWER -> 4
            KAMEN -> {
                when(difficulty) {
                    Difficulty.Normal ->3
                    else -> 4
                }
            }
            ETC -> 0
        }
    }

    //파티 수
    fun getMaxParty(): Int {
        return when (this) {
            VALTAN, VYKAS, ABRELSHUD, ILLIAKAN, KAMEN, ECHIDNA, ETC -> 2
            KOUKU, KAYANGEL, IVORYTOWER -> 1
            BETHEMOTH -> 4
        }
    }
}