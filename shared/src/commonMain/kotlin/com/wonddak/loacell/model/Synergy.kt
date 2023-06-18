package com.wonddak.loacell.model


data class ClassInfo(
    val className: String,
    val gender: Gender,
    val synergy: List<Synergy> = emptyList()
)

data class Synergy(
    val name: SynergyName,
    val type: SynergyType,
)


enum class SynergyName(val kotString: String) {
    DamageIncreasedOnCriticalHits("치명타 시 피해 증가"),
    CriticalRate("치명타 확률 증가"),
    AttackDamage("공격력 증가"),
    DefenseReduction("방어력 감소"),
    FlatPercentDamage("받는 피해 증기"),
    HeadAndBackAttackDamage("백/헤드 데미지 증가"),
    BUFF("딜러 공격력 증가")
}

enum class SynergyType(val kotString: String) {
    Burst("순간"),
    Constant("상시")
}

enum class Gender(val kotString: String) {
    Male("남"),
    Female("여")
}

sealed class Class {

    object Warrior {
        val DESTROYER = ClassInfo("디스트로이어", Gender.Male)
        val GUNLANCER = ClassInfo("워로드", Gender.Male)
        val BERSERKER = ClassInfo("버서커", Gender.Male)
        val PALADIN = ClassInfo("홀라나이트", Gender.Male)
        val SLAYER = ClassInfo("슬레이어", Gender.Female)
    }

    object MartialArtist {
        val WARDANCER = ClassInfo("배틀마스터", Gender.Female)
        val SCRAPPER = ClassInfo("인파이터", Gender.Female)
        val SOULFIST = ClassInfo("기공사", Gender.Female)
        val GLAIVIER = ClassInfo("창술사", Gender.Female)
        val STRIKER = ClassInfo("스트라이커", Gender.Male)
    }

    object Gunner {
        val DEADEYE = ClassInfo("데빌헌터", Gender.Male)
        val ARTILLERIST = ClassInfo("블래스터", Gender.Male)
        val SHARPSHOOTER = ClassInfo("호크아이", Gender.Male)
        val MACHINIST = ClassInfo("스카우터", Gender.Male)
        val GUNSLINGER = ClassInfo("건슬링어", Gender.Female)
    }

    object Mage {
        val BARD = ClassInfo("바드", Gender.Female)
        val SUMMONER = ClassInfo("서머너", Gender.Female)
        val ARCANIST = ClassInfo("아르카나", Gender.Female)
        val SORCERESS = ClassInfo("소서리스", Gender.Female)
    }

    object Assassin {
        val SHADOWHUNTER = ClassInfo("데모닉", Gender.Female)
        val DEATHBLADE = ClassInfo("블레이드", Gender.Female)
        val REAPER = ClassInfo("리퍼", Gender.Female)
    }

    object Specialist {
        val ARTIST = ClassInfo("도화가", Gender.Female)
        val Aeromancer = ClassInfo("기상술사", Gender.Female)
    }
}