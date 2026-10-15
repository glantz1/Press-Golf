package com.example.data.model

data class DotDefinition(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val shortBadge: String,
    val description: String,
    val defaultDots: Int = 1,
    val isGameDot: Boolean = false // true for 6's Low Ball/Low Total, false for Side Junk
)

object DotCatalog {
    val ALL_DOTS = listOf(
        DotDefinition(
            id = "LOW_BALL",
            name = "Low Ball",
            iconEmoji = "🎯",
            shortBadge = "LB",
            description = "Awarded to the player/team with the lowest individual score on the hole.",
            defaultDots = 1,
            isGameDot = true
        ),
        DotDefinition(
            id = "LOW_TOTAL",
            name = "Low Total",
            iconEmoji = "🤝",
            shortBadge = "LT",
            description = "Awarded to the 2-man team with the lower combined team score on the hole.",
            defaultDots = 1,
            isGameDot = true
        ),
        DotDefinition(
            id = "GREENIE",
            name = "Greenie",
            iconEmoji = "⛳",
            shortBadge = "GRN",
            description = "Closest to the pin on a Par 3 in regulation, making Par or better.",
            defaultDots = 1
        ),
        DotDefinition(
            id = "SANDIE",
            name = "Sandie",
            iconEmoji = "🏖️",
            shortBadge = "SND",
            description = "Up and down from a greenside bunker to save Par or better.",
            defaultDots = 1
        ),
        DotDefinition(
            id = "BARKIE",
            name = "Barkie",
            iconEmoji = "🌲",
            shortBadge = "BRK",
            description = "Ball strikes a tree on the hole and player still makes Par or better.",
            defaultDots = 1
        ),
        DotDefinition(
            id = "POLIE",
            name = "Polie",
            iconEmoji = "📏",
            shortBadge = "POL",
            description = "Made putt longer than the length of the flagstick (~7-8 feet).",
            defaultDots = 1
        ),
        DotDefinition(
            id = "FERRET",
            name = "Ferret",
            iconEmoji = "🦡",
            shortBadge = "FER",
            description = "Holed out from off the putting surface (chip-in or hole-out).",
            defaultDots = 1
        ),
        DotDefinition(
            id = "ARNIE",
            name = "Arnie",
            iconEmoji = "👑",
            shortBadge = "ARN",
            description = "Made par on a Par 4 or 5 without ever hitting the fairway (in the spirit of Arnold Palmer).",
            defaultDots = 1
        ),
        DotDefinition(
            id = "BIRDIE",
            name = "Birdie Dot",
            iconEmoji = "🦅",
            shortBadge = "BRD",
            description = "1 under par on any hole.",
            defaultDots = 1
        ),
        DotDefinition(
            id = "EAGLE",
            name = "Eagle Dot",
            iconEmoji = "⚡",
            shortBadge = "EGL",
            description = "2 under par on any hole.",
            defaultDots = 2
        ),
        DotDefinition(
            id = "SNAKE",
            name = "Snake",
            iconEmoji = "🐍",
            shortBadge = "SNK",
            description = "Penalty dot! Any 3-putt takes the Snake. Whoever holds it at round's end pays out.",
            defaultDots = -1
        )
    )

    fun getDotById(id: String): DotDefinition? = ALL_DOTS.find { it.id == id }
}
