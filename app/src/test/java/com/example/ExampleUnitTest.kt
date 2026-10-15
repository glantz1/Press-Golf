package com.example

import com.example.engine.GolfGameEngine
import com.example.engine.HandicapCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testWHSHandicapCalculation() {
        // Course Handicap = Round(Handicap Index * (Slope / 113) + (Rating - Par))
        // 12.0 * (135 / 113.0) + (72.8 - 72.0) = 14.336 + 0.8 = 15.136 -> 15
        val ch = HandicapCalculator.calculateCourseHandicap(
            handicapIndex = 12.0,
            slopeRating = 135,
            courseRating = 72.8,
            parTotal = 72
        )
        assertEquals(15, ch)
    }

    @Test
    fun testStrokeAllocationPerHole() {
        // A player with 15 course handicap gets 1 stroke on hole ranks 1..15, and 0 on 16..18
        assertEquals(1, HandicapCalculator.getStrokesForHole(15, 1))
        assertEquals(1, HandicapCalculator.getStrokesForHole(15, 15))
        assertEquals(0, HandicapCalculator.getStrokesForHole(15, 16))
        assertEquals(0, HandicapCalculator.getStrokesForHole(15, 18))

        // A player with 20 course handicap gets 2 strokes on ranks 1..2, and 1 stroke on ranks 3..18
        assertEquals(2, HandicapCalculator.getStrokesForHole(20, 1))
        assertEquals(2, HandicapCalculator.getStrokesForHole(20, 2))
        assertEquals(1, HandicapCalculator.getStrokesForHole(20, 3))
    }

    @Test
    fun testSixesPairingsRotation() {
        val leg1 = GolfGameEngine.getSixesPairingForHole(3)
        assertEquals(listOf(0, 1), leg1.team1PlayerIndices) // A & B
        assertEquals(listOf(2, 3), leg1.team2PlayerIndices) // C & D

        val leg2 = GolfGameEngine.getSixesPairingForHole(8)
        assertEquals(listOf(0, 2), leg2.team1PlayerIndices) // A & C
        assertEquals(listOf(1, 3), leg2.team2PlayerIndices) // B & D

        val leg3 = GolfGameEngine.getSixesPairingForHole(15)
        assertEquals(listOf(0, 3), leg3.team1PlayerIndices) // A & D
        assertEquals(listOf(1, 2), leg3.team2PlayerIndices) // B & C
    }

    @Test
    fun testLowBallLowTotalNetAndGross() {
        val p1 = com.example.data.model.RoundPlayer("1", "round1", "p1", "Player A", 0.0, 0, 0)
        val p2 = com.example.data.model.RoundPlayer("2", "round1", "p2", "Player B", 10.0, 10, 1)
        val p3 = com.example.data.model.RoundPlayer("3", "round1", "p3", "Player C", 0.0, 0, 2)
        val p4 = com.example.data.model.RoundPlayer("4", "round1", "p4", "Player D", 0.0, 0, 3)

        // Hole 1:
        // P1: Gross 4, Net 4
        // P2: Gross 5, Net 4 (1 stroke received)
        // P3: Gross 4, Net 4
        // P4: Gross 6, Net 6
        val s1 = com.example.data.model.HoleScore("s1", "round1", "p1", 1, grossScore = 4, netScore = 4, strokesAllocated = 0)
        val s2 = com.example.data.model.HoleScore("s2", "round1", "p2", 1, grossScore = 5, netScore = 4, strokesAllocated = 1)
        val s3 = com.example.data.model.HoleScore("s3", "round1", "p3", 1, grossScore = 4, netScore = 4, strokesAllocated = 0)
        val s4 = com.example.data.model.HoleScore("s4", "round1", "p4", 1, grossScore = 6, netScore = 6, strokesAllocated = 0)

        val scores = mapOf("p1" to s1, "p2" to s2, "p3" to s3, "p4" to s4)

        // Evaluate GROSS:
        // Team 1: Gross 4, 5 -> Low 4, Total 9
        // Team 2: Gross 4, 6 -> Low 4, Total 10
        // Low Ball: 4 vs 4 -> Tie (null)
        // Low Total: 9 vs 10 -> Team 1 wins!
        val grossRes = GolfGameEngine.evaluateLowBallLowTotalHole(1, listOf(p1, p2, p3, p4), scores, useNet = false)
        assertNotNull(grossRes)
        assertEquals(null, grossRes?.lowBallWinnerTeam) // Halved
        assertEquals(1, grossRes?.lowTotalWinnerTeam)   // Team 1 won total
        assertEquals(1, grossRes?.team1PointsWon)
        assertEquals(0, grossRes?.team2PointsWon)

        // Evaluate NET:
        // Team 1: Net 4, 4 -> Low 4, Total 8
        // Team 2: Net 4, 6 -> Low 4, Total 10
        // Low Ball: 4 vs 4 -> Tie
        // Low Total: 8 vs 10 -> Team 1 wins
        val netRes = GolfGameEngine.evaluateLowBallLowTotalHole(1, listOf(p1, p2, p3, p4), scores, useNet = true)
        assertNotNull(netRes)
        assertEquals(1, netRes?.lowTotalWinnerTeam)
        assertEquals(8, netRes?.team1Total)
        assertEquals(10, netRes?.team2Total)
    }
}
