package com.example.engine

import kotlin.math.roundToInt

object HandicapCalculator {

    /**
     * Calculates the USGA / WHS Course Handicap for a player on a specific tee.
     * Formula: Course Handicap = Round(Handicap Index * (Slope / 113) + (Course Rating - Par))
     */
    fun calculateCourseHandicap(
        handicapIndex: Double,
        slopeRating: Int,
        courseRating: Double,
        parTotal: Int,
        allowancePercentage: Double = 1.0 // 1.0 = 100%
    ): Int {
        val standardSlope = 113.0
        val baseHandicap = (handicapIndex * (slopeRating / standardSlope)) + (courseRating - parTotal)
        val allowanceHandicap = baseHandicap * allowancePercentage
        return allowanceHandicap.roundToInt()
    }

    /**
     * Determines how many handicap strokes a player receives on a specific hole.
     * Hole handicap rank is 1 (hardest hole on course) to 18 (easiest).
     */
    fun getStrokesForHole(courseHandicap: Int, holeHandicapRank: Int): Int {
        if (courseHandicap == 0) return 0

        if (courseHandicap > 0) {
            val baseStrokes = courseHandicap / 18
            val remainder = courseHandicap % 18
            val extraStroke = if (holeHandicapRank <= remainder) 1 else 0
            return baseStrokes + extraStroke
        } else {
            // Plus handicap (giving strokes back on easiest holes)
            val absHandicap = kotlin.math.abs(courseHandicap)
            // Starts giving strokes on easiest holes (ranks 18, 17, 16...)
            val easiestThreshold = 18 - absHandicap + 1
            return if (holeHandicapRank >= easiestThreshold) -1 else 0
        }
    }

    /**
     * Calculates the net score on a hole given gross score and strokes received.
     */
    fun calculateNetScore(grossScore: Int, strokesAllocated: Int): Int {
        if (grossScore <= 0) return 0
        return (grossScore - strokesAllocated).coerceAtLeast(1)
    }

    /**
     * Calculates the WHS Score Differential for a completed 18-hole round.
     * Formula: (113 / Slope) * (Adjusted Gross Score - Rating)
     */
    fun calculateScoreDifferential(
        grossScoreTotal: Int,
        courseRating: Double,
        slopeRating: Int
    ): Double {
        if (slopeRating == 0) return 0.0
        val differential = (113.0 / slopeRating) * (grossScoreTotal.toDouble() - courseRating)
        return (differential * 10.0).roundToInt() / 10.0
    }
}
