package com.example.engine

import com.example.data.model.HoleScore
import com.example.data.model.RoundPlayer

data class SixesPairing(
    val team1PlayerIndices: List<Int>,
    val team2PlayerIndices: List<Int>,
    val legDescription: String
)

data class SixesHoleResult(
    val holeNumber: Int,
    val lowBallWinnerTeam: Int?, // 1, 2, or null (tie)
    val lowTotalWinnerTeam: Int?, // 1, 2, or null (tie)
    val playerDotsAwarded: Map<String, Int>, // playerId -> count of dots won on this hole
    val team1LowScore: Int,
    val team2LowScore: Int,
    val team1Total: Int,
    val team2Total: Int
)

data class LowBallLowTotalHoleResult(
    val holeNumber: Int,
    val lowBallWinnerTeam: Int?, // 1, 2, or null (tie)
    val lowTotalWinnerTeam: Int?, // 1, 2, or null (tie)
    val team1LowScore: Int,
    val team2LowScore: Int,
    val team1Total: Int,
    val team2Total: Int,
    val team1PointsWon: Int,
    val team2PointsWon: Int
)

data class LowBallLowTotalMatchSummary(
    val team1PlayerNames: List<String>,
    val team2PlayerNames: List<String>,
    val team1TotalPoints: Int,
    val team2TotalPoints: Int,
    val team1Front9Points: Int,
    val team2Front9Points: Int,
    val team1Back9Points: Int,
    val team2Back9Points: Int,
    val holeResults: List<LowBallLowTotalHoleResult>
)

data class PlayerDotStanding(
    val playerId: String,
    val playerName: String,
    val playerSlot: Int,
    val sixesDots: Int,
    val junkDots: Int,
    val totalDots: Int,
    val grossTotal: Int,
    val netTotal: Int
)

data class SkinsHoleResult(
    val holeNumber: Int,
    val winnerPlayerId: String?,
    val winningScore: Int?,
    val skinsValue: Int, // includes carryovers
    val isTied: Boolean
)

data class NassauStanding(
    val team1Name: String,
    val team2Name: String,
    val front9Status: String, // e.g., "Team 1 2-Up", "All Square"
    val back9Status: String,
    val overall18Status: String
)

object GolfGameEngine {

    /**
     * Determines 6's pairings for any hole 1..18.
     * 4 players: 0=Player A, 1=Player B, 2=Player C, 3=Player D.
     */
    fun getSixesPairingForHole(holeNumber: Int): SixesPairing {
        return when (holeNumber) {
            in 1..6 -> SixesPairing(
                team1PlayerIndices = listOf(0, 1),
                team2PlayerIndices = listOf(2, 3),
                legDescription = "Leg 1 (Holes 1–6): Team A&B vs Team C&D"
            )
            in 7..12 -> SixesPairing(
                team1PlayerIndices = listOf(0, 2),
                team2PlayerIndices = listOf(1, 3),
                legDescription = "Leg 2 (Holes 7–12): Team A&C vs Team B&D"
            )
            else -> SixesPairing(
                team1PlayerIndices = listOf(0, 3),
                team2PlayerIndices = listOf(1, 2),
                legDescription = "Leg 3 (Holes 13–18): Team A&D vs Team B&C"
            )
        }
    }

    /**
     * Evaluates 6's results for a specific hole.
     */
    fun evaluateSixesHole(
        holeNumber: Int,
        roundPlayers: List<RoundPlayer>,
        scoresByPlayerId: Map<String, HoleScore>,
        useNet: Boolean
    ): SixesHoleResult? {
        if (roundPlayers.size < 4) return null
        val pairing = getSixesPairingForHole(holeNumber)

        val t1Players = pairing.team1PlayerIndices.mapNotNull { idx -> roundPlayers.getOrNull(idx) }
        val t2Players = pairing.team2PlayerIndices.mapNotNull { idx -> roundPlayers.getOrNull(idx) }

        if (t1Players.size < 2 || t2Players.size < 2) return null

        val getScore = { p: RoundPlayer ->
            val s = scoresByPlayerId[p.playerId]
            if (s != null && s.isCompleted) {
                if (useNet) s.netScore else s.grossScore
            } else null
        }

        val t1Scores = t1Players.mapNotNull { getScore(it) }
        val t2Scores = t2Players.mapNotNull { getScore(it) }

        // If not all 4 players completed this hole yet
        if (t1Scores.size < 2 || t2Scores.size < 2) return null

        val t1Low = t1Scores.minOrNull() ?: return null
        val t2Low = t2Scores.minOrNull() ?: return null
        val t1Total = t1Scores.sum()
        val t2Total = t2Scores.sum()

        val lowBallWinner = when {
            t1Low < t2Low -> 1
            t2Low < t1Low -> 2
            else -> null
        }

        val lowTotalWinner = when {
            t1Total < t2Total -> 1
            t2Total < t1Total -> 2
            else -> null
        }

        val dotsMap = mutableMapOf<String, Int>()
        roundPlayers.forEach { dotsMap[it.playerId] = 0 }

        if (lowBallWinner == 1) {
            t1Players.forEach { dotsMap[it.playerId] = (dotsMap[it.playerId] ?: 0) + 1 }
        } else if (lowBallWinner == 2) {
            t2Players.forEach { dotsMap[it.playerId] = (dotsMap[it.playerId] ?: 0) + 1 }
        }

        if (lowTotalWinner == 1) {
            t1Players.forEach { dotsMap[it.playerId] = (dotsMap[it.playerId] ?: 0) + 1 }
        } else if (lowTotalWinner == 2) {
            t2Players.forEach { dotsMap[it.playerId] = (dotsMap[it.playerId] ?: 0) + 1 }
        }

        return SixesHoleResult(
            holeNumber = holeNumber,
            lowBallWinnerTeam = lowBallWinner,
            lowTotalWinnerTeam = lowTotalWinner,
            playerDotsAwarded = dotsMap,
            team1LowScore = t1Low,
            team2LowScore = t2Low,
            team1Total = t1Total,
            team2Total = t2Total
        )
    }

    /**
     * Evaluates Low Ball / Low Total for a fixed 2 vs 2 match across the whole 18 holes.
     * Team 1 = Players 0 & 1, Team 2 = Players 2 & 3.
     */
    fun evaluateLowBallLowTotalHole(
        holeNumber: Int,
        roundPlayers: List<RoundPlayer>,
        scoresByPlayerId: Map<String, HoleScore>,
        useNet: Boolean
    ): LowBallLowTotalHoleResult? {
        if (roundPlayers.size < 4) return null
        val t1Players = listOf(roundPlayers[0], roundPlayers[1])
        val t2Players = listOf(roundPlayers[2], roundPlayers[3])

        val getScore = { p: RoundPlayer ->
            val s = scoresByPlayerId[p.playerId]
            if (s != null && s.isCompleted) {
                if (useNet) s.netScore else s.grossScore
            } else null
        }

        val t1Scores = t1Players.mapNotNull { getScore(it) }
        val t2Scores = t2Players.mapNotNull { getScore(it) }

        if (t1Scores.size < 2 || t2Scores.size < 2) return null

        val t1Low = t1Scores.minOrNull() ?: return null
        val t2Low = t2Scores.minOrNull() ?: return null
        val t1Total = t1Scores.sum()
        val t2Total = t2Scores.sum()

        val lowBallWinner = when {
            t1Low < t2Low -> 1
            t2Low < t1Low -> 2
            else -> null
        }

        val lowTotalWinner = when {
            t1Total < t2Total -> 1
            t2Total < t1Total -> 2
            else -> null
        }

        var t1Pts = 0
        var t2Pts = 0
        if (lowBallWinner == 1) t1Pts += 1 else if (lowBallWinner == 2) t2Pts += 1
        if (lowTotalWinner == 1) t1Pts += 1 else if (lowTotalWinner == 2) t2Pts += 1

        return LowBallLowTotalHoleResult(
            holeNumber = holeNumber,
            lowBallWinnerTeam = lowBallWinner,
            lowTotalWinnerTeam = lowTotalWinner,
            team1LowScore = t1Low,
            team2LowScore = t2Low,
            team1Total = t1Total,
            team2Total = t2Total,
            team1PointsWon = t1Pts,
            team2PointsWon = t2Pts
        )
    }

    /**
     * Evaluates the full 18-hole Low Ball / Low Total match for 2v2 fixed teams.
     */
    fun evaluateLowBallLowTotalMatch(
        roundPlayers: List<RoundPlayer>,
        allScores: List<HoleScore>,
        useNet: Boolean
    ): LowBallLowTotalMatchSummary? {
        if (roundPlayers.size < 4) return null
        val t1Names = listOf(roundPlayers[0].playerName, roundPlayers[1].playerName)
        val t2Names = listOf(roundPlayers[2].playerName, roundPlayers[3].playerName)

        val scoresByHole = allScores.groupBy { it.holeNumber }
        val holeResults = mutableListOf<LowBallLowTotalHoleResult>()

        var t1Front = 0
        var t2Front = 0
        var t1Back = 0
        var t2Back = 0

        for (holeNum in 1..18) {
            val scoresOnHole = scoresByHole[holeNum]?.associateBy { it.playerId } ?: emptyMap()
            val result = evaluateLowBallLowTotalHole(holeNum, roundPlayers, scoresOnHole, useNet)
            if (result != null) {
                holeResults.add(result)
                if (holeNum in 1..9) {
                    t1Front += result.team1PointsWon
                    t2Front += result.team2PointsWon
                } else {
                    t1Back += result.team1PointsWon
                    t2Back += result.team2PointsWon
                }
            }
        }

        return LowBallLowTotalMatchSummary(
            team1PlayerNames = t1Names,
            team2PlayerNames = t2Names,
            team1TotalPoints = t1Front + t1Back,
            team2TotalPoints = t2Front + t2Back,
            team1Front9Points = t1Front,
            team2Front9Points = t2Front,
            team1Back9Points = t1Back,
            team2Back9Points = t2Back,
            holeResults = holeResults
        )
    }

    /**
     * Computes player dot standings and totals for the entire round.
     * Evaluates travelling 6's dots, 2v2 Low Ball/Total points, + individual junk dots.
     */
    fun computePlayerStandings(
        roundPlayers: List<RoundPlayer>,
        allScores: List<HoleScore>,
        useNet: Boolean,
        gameType: String = "SIXES"
    ): List<PlayerDotStanding> {
        val scoresByHole = allScores.groupBy { it.holeNumber }

        // Game-specific dots accumulator
        val gameDotsAccumulator = mutableMapOf<String, Int>()
        roundPlayers.forEach { gameDotsAccumulator[it.playerId] = 0 }

        if (roundPlayers.size == 4) {
            if (gameType == "LOW_BALL_LOW_TOTAL") {
                for (holeNum in 1..18) {
                    val holeScores = scoresByHole[holeNum]?.associateBy { it.playerId } ?: emptyMap()
                    val res = evaluateLowBallLowTotalHole(holeNum, roundPlayers, holeScores, useNet)
                    if (res != null) {
                        if (res.team1PointsWon > 0) {
                            listOf(roundPlayers[0].playerId, roundPlayers[1].playerId).forEach { pid ->
                                gameDotsAccumulator[pid] = (gameDotsAccumulator[pid] ?: 0) + res.team1PointsWon
                            }
                        }
                        if (res.team2PointsWon > 0) {
                            listOf(roundPlayers[2].playerId, roundPlayers[3].playerId).forEach { pid ->
                                gameDotsAccumulator[pid] = (gameDotsAccumulator[pid] ?: 0) + res.team2PointsWon
                            }
                        }
                    }
                }
            } else {
                // Default 6s rotating partner dots
                for (holeNum in 1..18) {
                    val holeScores = scoresByHole[holeNum]?.associateBy { it.playerId } ?: emptyMap()
                    val res = evaluateSixesHole(holeNum, roundPlayers, holeScores, useNet)
                    res?.playerDotsAwarded?.forEach { (pid, dots) ->
                        gameDotsAccumulator[pid] = (gameDotsAccumulator[pid] ?: 0) + dots
                    }
                }
            }
        }

        // Aggregate Junk/Trash dots & Gross/Net
        return roundPlayers.mapIndexed { idx, player ->
            val playerScores = allScores.filter { it.playerId == player.playerId && it.isCompleted }
            val grossTotal = playerScores.sumOf { it.grossScore }
            val netTotal = playerScores.sumOf { it.netScore }

            var junkDotsCount = 0
            playerScores.forEach { hs ->
                val dots = hs.getDotsList()
                dots.forEach { dotId ->
                    when (dotId) {
                        "GREENIE", "SANDIE", "BARKIE", "POLIE", "FERRET", "ARNIE", "BIRDIE" -> junkDotsCount += 1
                        "EAGLE" -> junkDotsCount += 2
                        "SNAKE" -> junkDotsCount -= 1
                    }
                }
            }

            val gameDots = gameDotsAccumulator[player.playerId] ?: 0
            PlayerDotStanding(
                playerId = player.playerId,
                playerName = player.playerName,
                playerSlot = idx,
                sixesDots = gameDots,
                junkDots = junkDotsCount,
                totalDots = gameDots + junkDotsCount,
                grossTotal = grossTotal,
                netTotal = netTotal
            )
        }.sortedByDescending { it.totalDots }
    }

    /**
     * Evaluates Skins game with carryovers across 18 holes.
     */
    fun evaluateSkins(
        roundPlayers: List<RoundPlayer>,
        allScores: List<HoleScore>,
        useNet: Boolean
    ): List<SkinsHoleResult> {
        val scoresByHole = allScores.groupBy { it.holeNumber }
        val results = mutableListOf<SkinsHoleResult>()
        var carryover = 0

        for (hole in 1..18) {
            val holeScores = scoresByHole[hole] ?: emptyList()
            val completed = holeScores.filter { it.isCompleted }

            if (completed.size < roundPlayers.size || completed.isEmpty()) {
                results.add(
                    SkinsHoleResult(
                        holeNumber = hole,
                        winnerPlayerId = null,
                        winningScore = null,
                        skinsValue = 1 + carryover,
                        isTied = false
                    )
                )
                continue
            }

            val scoresWithPlayer = completed.map { hs ->
                val score = if (useNet) hs.netScore else hs.grossScore
                hs.playerId to score
            }

            val minScore = scoresWithPlayer.minOf { it.second }
            val lowScorers = scoresWithPlayer.filter { it.second == minScore }

            if (lowScorers.size == 1) {
                val winner = lowScorers.first().first
                val skinValue = 1 + carryover
                carryover = 0
                results.add(
                    SkinsHoleResult(
                        holeNumber = hole,
                        winnerPlayerId = winner,
                        winningScore = minScore,
                        skinsValue = skinValue,
                        isTied = false
                    )
                )
            } else {
                carryover += 1
                results.add(
                    SkinsHoleResult(
                        holeNumber = hole,
                        winnerPlayerId = null,
                        winningScore = minScore,
                        skinsValue = carryover,
                        isTied = true
                    )
                )
            }
        }
        return results
    }

    /**
     * Calculates Stableford points for a single hole score based on par.
     */
    fun calculateStablefordPoints(score: Int, par: Int, isModified: Boolean = false): Int {
        if (score <= 0) return 0
        val diff = score - par
        return if (!isModified) {
            when {
                diff <= -3 -> 5
                diff == -2 -> 4
                diff == -1 -> 3
                diff == 0 -> 2
                diff == 1 -> 1
                else -> 0
            }
        } else {
            when {
                diff <= -3 -> 8
                diff == -2 -> 5
                diff == -1 -> 2
                diff == 0 -> 0
                diff == 1 -> -1
                else -> -3
            }
        }
    }
}
