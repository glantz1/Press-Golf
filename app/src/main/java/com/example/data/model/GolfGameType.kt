package com.example.data.model

enum class GameCategory(val displayName: String) {
    ROTATION("Rotating Partner Games"),
    TEAM("Team & Pairs Games"),
    MULTI_PLAYER("Side Bets & Group Games"),
    POINTS("Points & Quota"),
    TRADITIONAL("Traditional Tournament Formats")
}

data class GolfGameDefinition(
    val id: String,
    val name: String,
    val shortName: String,
    val category: GameCategory,
    val minPlayers: Int,
    val maxPlayers: Int,
    val tagline: String,
    val description: String,
    val rotationRules: String = "",
    val scoringRules: String,
    val dotsAndPayouts: String,
    val handicapUsage: String
)

object GolfGamesCatalog {
    val ALL_GAMES = listOf(
        GolfGameDefinition(
            id = "LOW_BALL_LOW_TOTAL",
            name = "Low Ball / Low Total (2v2 Full Match)",
            shortName = "Low Ball / Total",
            category = GameCategory.TEAM,
            minPlayers = 4,
            maxPlayers = 4,
            tagline = "Fixed 2 vs 2 for all 18 holes. 1 point for low ball, 1 point for low total!",
            description = "The definitive 2-man team match format. Two fixed partners (Player 1 & 2) battle against (Player 3 & 4) for the entire 18-hole round. On every hole, two points are at stake: 1 point for the team with the lowest individual score (Low Ball), and 1 point for the team with the lowest combined team score (Low Total). Ties halve or carry over.",
            rotationRules = "Fixed 2 vs 2 partnership for all 18 holes:\n• Team 1: Player A + Player B\n• Team 2: Player C + Player D",
            scoringRules = "On each hole (playable Gross or Net):\n1. Low Ball (1 pt): The team whose player has the lowest score on the hole wins 1 point.\n2. Low Total (1 pt): The team with the lower combined score (P1+P2 vs P3+P4) wins 1 point.\n3. Halved holes: Ties can either halve (0.5 pt each) or wash. Team with the most points across 18 holes wins the match!",
            dotsAndPayouts = "Points tallied across Front 9, Back 9, and Total 18 holes. Each point is worth the set unit bet (e.g. $1 or $2 per point).",
            handicapUsage = "Supports both Net (WHS handicap strokes off the hole stroke index rank) and Gross (scratch strokes)."
        ),
        GolfGameDefinition(
            id = "SIXES",
            name = "6's (Round Robin / Hollywood)",
            shortName = "6's",
            category = GameCategory.ROTATION,
            minPlayers = 4,
            maxPlayers = 4,
            tagline = "Partners rotate every 6 holes; dots travel with individual players!",
            description = "The ultimate 4-ball betting game. Over 18 holes, you team up with each golfer in your group for exactly 6 holes. Points (dots) are awarded on every hole for Low Ball and Low Total. Best of all, dots stay with individual players to determine the overall champion!",
            rotationRules = "• Holes 1–6: Team A (Player 1 + Player 2) vs Team B (Player 3 + Player 4)\n• Holes 7–12: Team A (Player 1 + Player 3) vs Team B (Player 2 + Player 4)\n• Holes 13–18: Team A (Player 1 + Player 4) vs Team B (Player 2 + Player 3)",
            scoringRules = "On each hole:\n1. Low Ball Dot: Team with the lowest individual score among all 4 players wins 1 dot per partner.\n2. Low Total Dot: Team with the lowest combined team score wins 1 dot per partner.\n3. Ties either push or split. At the end of 18 holes, the player with the most accumulated dots wins the pot!",
            dotsAndPayouts = "Dots accumulate per player across all 18 holes. Each dot has a set dollar value (e.g. $1 or $2 per dot). Final settlements compare each player's dots vs other players.",
            handicapUsage = "Supports Gross (raw strokes) or Net (strokes off the low ball or course handicap stroke index)."
        ),
        GolfGameDefinition(
            id = "SKINS",
            name = "Skins (Gross or Net)",
            shortName = "Skins",
            category = GameCategory.MULTI_PLAYER,
            minPlayers = 2,
            maxPlayers = 8,
            tagline = "Lowest unique score wins the hole; ties carry over!",
            description = "Each hole is worth one 'skin'. A player wins the skin outright by having the lowest score on that hole with no ties. If two or more players tie for lowest score, the skin carries over to increase the pot on the next hole!",
            rotationRules = "Individual play or 2-player teams against the field.",
            scoringRules = "• Outright lowest score on hole takes the skin (and any accumulated carryovers).\n• Any tie for low score results in a push, adding the skin to the subsequent hole.\n• On hole 18, a tie can either trigger sudden death or split evenly.",
            dotsAndPayouts = "Holes carried over build massive excitement. A 4-hole carryover skin can flip the whole match!",
            handicapUsage = "In Net Skins, players receive handicap strokes based on the course scorecard's hole stroke index rank."
        ),
        GolfGameDefinition(
            id = "NASSAU",
            name = "Nassau (with Auto-Press)",
            shortName = "Nassau",
            category = GameCategory.TRADITIONAL,
            minPlayers = 2,
            maxPlayers = 4,
            tagline = "Three simultaneous matches: Front 9, Back 9, and Full 18.",
            description = "Golf's most storied wager, created at Nassau Country Club in Long Island in 1900. Three separate bets: Front 9 match, Back 9 match, and the overall 18-hole match.",
            rotationRules = "Singles (1v1) or fixed 2v2 Four-Ball.",
            scoringRules = "• Front 9 Winner: Best match play record on holes 1–9.\n• Back 9 Winner: Best record on holes 10–18 (score resets on hole 10!).\n• Overall 18: Best record across all 18 holes.\n• Auto-Press: When a team falls 2-down, a new subsidiary match is automatically pressed for the remaining holes in that nine.",
            dotsAndPayouts = "Standard $2, $5, or $10 bets per bet segment, plus optional press payouts.",
            handicapUsage = "Typically played off the low handicap in the group (low handicap plays as scratch, others get the difference)."
        ),
        GolfGameDefinition(
            id = "WOLF",
            name = "Wolf",
            shortName = "Wolf",
            category = GameCategory.ROTATION,
            minPlayers = 4,
            maxPlayers = 4,
            tagline = "Rotating Wolf chooses a partner or goes Lone Wolf for triple points!",
            description = "A fast-paced psychological group game. The tee honor rotates each hole: 1st, 2nd, 3rd tee off, and the designated 'Wolf' tees off last. The Wolf can select a partner immediately after seeing their shot, or choose to go 'Lone Wolf' against the other three!",
            rotationRules = "The Wolf rotation passes 1-2-3-4 every hole. On holes 17 and 18, players in 3rd/4th place typically get Wolf honors.",
            scoringRules = "• Wolf + Partner vs 2 Hunters: Lowest ball of pair wins. 2 points each if Wolf team wins, 3 points each if Hunters win.\n• Lone Wolf: Wolf plays 1 vs 3. If Lone Wolf wins low ball, Wolf earns 4 points (or triple). If Hunters win, Hunters get 1 point each.\n• Blind Wolf: Declared before anyone hits drive for 4x stakes!",
            dotsAndPayouts = "Points tallied at the conclusion of 18 holes.",
            handicapUsage = "Net scores equalize high and low handicappers."
        ),
        GolfGameDefinition(
            id = "VEGAS",
            name = "Vegas (Team Numbers)",
            shortName = "Vegas",
            category = GameCategory.TEAM,
            minPlayers = 4,
            maxPlayers = 4,
            tagline = "Concatenate team scores; birdies flip opponent numbers!",
            description = "A high-drama 2v2 team game. Instead of adding scores together, a team's two scores are combined by putting the lower score first. E.g., if Player A shoots 4 and Player B shoots 5, the team score is 45!",
            rotationRules = "Fixed 2v2 teams throughout the 18 holes.",
            scoringRules = "• Normal: Low digit goes first. Team 1 (4 & 5 = 45) vs Team 2 (5 & 6 = 56). Team 1 wins 11 points (56 - 45).\n• Birdie Flip: If a team makes birdie (or eagle), the losing team's scores are flipped so the HIGHER score goes first! (e.g. 4 and 6 becomes 64!).",
            dotsAndPayouts = "Difference in points between pairs paid per point (often 10¢ to $1 per point).",
            handicapUsage = "Gross or Net. On high handicap holes, strokes create huge point swings."
        ),
        GolfGameDefinition(
            id = "BINGO_BANGO_BONGO",
            name = "Bingo Bango Bongo",
            shortName = "Bingo Bango",
            category = GameCategory.MULTI_PLAYER,
            minPlayers = 2,
            maxPlayers = 4,
            tagline = "Strict order of play rewards skills on every hole regardless of score!",
            description = "Three points available on every single hole. Honors and distance away from the pin dictate order of play strictly.",
            rotationRules = "Group play. Furthest from the hole plays first at all times.",
            scoringRules = "• Bingo: 1st point awarded to the first golfer to land their ball on the green.\n• Bango: 2nd point awarded to the player whose ball is closest to the pin once all golf balls are on the green.\n• Bongo: 3rd point awarded to the player who holes out first in the cup.",
            dotsAndPayouts = "54 total points across 18 holes (3 per hole). Payout by net points earned.",
            handicapUsage = "Naturally self-handicapping because shorter hitters hit into the green first, having first crack at Bingo!"
        ),
        GolfGameDefinition(
            id = "STABLEFORD",
            name = "Stableford & Modified",
            shortName = "Stableford",
            category = GameCategory.POINTS,
            minPlayers = 1,
            maxPlayers = 8,
            tagline = "Points for pars, birdies, and eagles; bad holes don't ruin your round!",
            description = "The classic scoring system invented by Dr. Frank Stableford at Wallasey in 1931. Rather than counting strokes, golfers score positive points for good scores on holes.",
            rotationRules = "Individual competition.",
            scoringRules = "• Double Eagle / Albatross: +8 pts\n• Eagle: +5 pts\n• Birdie: +2 pts (Modified: +3 pts)\n• Par: 0 pts (Modified: +1 pt)\n• Bogey: -1 pt (Standard: 1 pt)\n• Double Bogey or worse: -3 pts (Standard: 0 pts)",
            dotsAndPayouts = "Player with highest total positive points wins.",
            handicapUsage = "Net Stableford calculates points based on Net score relative to hole par."
        ),
        GolfGameDefinition(
            id = "QUOTA",
            name = "Chicago / Quota",
            shortName = "Quota",
            category = GameCategory.POINTS,
            minPlayers = 2,
            maxPlayers = 16,
            tagline = "Beat your custom point quota based on your handicap index!",
            description = "Each golfer begins with a targeted point quota calculated as (36 minus Course Handicap). During the round, points are earned for Bogeys (1), Pars (2), Birdies (4), Eagles (8). Highest points over your quota wins!",
            rotationRules = "Individual or team quota.",
            scoringRules = "• Point values: Bogey = 1, Par = 2, Birdie = 4, Eagle = 8, Double Eagle = 16.\n• Your Goal: Score higher than your assigned target quota.",
            dotsAndPayouts = "Player with the largest positive differential (+5, +3, etc.) takes the pot.",
            handicapUsage = "Handicap directly sets the starting point quota."
        ),
        GolfGameDefinition(
            id = "FOUR_BALL",
            name = "Four-Ball (2v2 Best Ball)",
            shortName = "Four-Ball",
            category = GameCategory.TEAM,
            minPlayers = 4,
            maxPlayers = 4,
            tagline = "Ryder Cup style: Two 2-man teams take the best score on each hole.",
            description = "The premier tournament format. Each player plays their own ball throughout. The best score of the two partners counts as the team's score for the hole.",
            rotationRules = "Fixed 2v2 teams.",
            scoringRules = "Match play (1-up, 2-up, halved) or stroke play aggregate.",
            dotsAndPayouts = "Overall match victory, or point per hole won.",
            handicapUsage = "Players stroke off the lowest player's handicap."
        ),
        GolfGameDefinition(
            id = "CHA_CHA_CHA",
            name = "ChaChaCha",
            shortName = "ChaChaCha",
            category = GameCategory.TEAM,
            minPlayers = 4,
            maxPlayers = 4,
            tagline = "1 best ball on Par 3s, 2 on Par 4s, 3 on Par 5s!",
            description = "A dynamic 4-player team game where pressure builds based on hole par. Par 3s require 1 low ball, Par 4s require 2 low balls, and Par 5s require 3 low balls!",
            rotationRules = "4-player squad working together or competing in teams.",
            scoringRules = "Sum of required low balls across all 18 holes.",
            dotsAndPayouts = "Team competition against other foursomes or team quota.",
            handicapUsage = "Net scores utilized to determine the qualifying low balls."
        ),
        GolfGameDefinition(
            id = "SCRAMBLE",
            name = "Scramble & Shamble",
            shortName = "Scramble",
            category = GameCategory.TEAM,
            minPlayers = 2,
            maxPlayers = 4,
            tagline = "Everyone hits, choose the best shot, and play from there!",
            description = "Scramble: All players hit each shot, select the best ball, and all hit from that spot until holed. Shamble: Everyone drives, select the best drive, and each player plays their own ball from there into the cup.",
            rotationRules = "Team play.",
            scoringRules = "Team total gross or team net handicap (typically 20% of team A + 15% team B).",
            dotsAndPayouts = "Low team score wins.",
            handicapUsage = "USGA recommended team handicap allowance formulas."
        ),
        GolfGameDefinition(
            id = "MATCH_PLAY",
            name = "Match Play",
            shortName = "Match Play",
            category = GameCategory.TRADITIONAL,
            minPlayers = 2,
            maxPlayers = 4,
            tagline = "Hole-by-hole rivalry: Win holes, not just stroke totals.",
            description = "The oldest format of golf. You play to win individual holes rather than aggregate total strokes. Halved holes mean no blood.",
            rotationRules = "1v1 or 2v2.",
            scoringRules = "Lowest score wins the hole (1 up). Dormie occurs when a player leads by the same number of holes remaining.",
            dotsAndPayouts = "Winner of match.",
            handicapUsage = "Net strokes given on designated handicap ranked holes."
        ),
        GolfGameDefinition(
            id = "STROKE_PLAY",
            name = "Individual Stroke Play (Medal)",
            shortName = "Stroke Play",
            category = GameCategory.TRADITIONAL,
            minPlayers = 1,
            maxPlayers = 8,
            tagline = "Standard tournament golf: Total gross and net score across 18 holes.",
            description = "Every shot counts. Count all strokes and penalties across 18 holes for full gross and net leaderboard standings.",
            rotationRules = "Individual stroke play.",
            scoringRules = "Lowest 18-hole total wins.",
            dotsAndPayouts = "Low gross, low net, flight winners.",
            handicapUsage = "Net score = Gross score - Course Handicap."
        )
    )

    fun getGameById(id: String): GolfGameDefinition {
        return ALL_GAMES.find { it.id == id } ?: ALL_GAMES.first()
    }
}
