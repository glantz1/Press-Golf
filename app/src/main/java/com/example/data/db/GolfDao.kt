package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Course
import com.example.data.model.Hole
import com.example.data.model.HoleScore
import com.example.data.model.Player
import com.example.data.model.Round
import com.example.data.model.RoundPlayer
import com.example.data.model.Tee
import kotlinx.coroutines.flow.Flow

@Dao
interface GolfDao {

    // --- PLAYERS ---
    @Query("SELECT * FROM players ORDER BY name ASC")
    fun getAllPlayersFlow(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: String): Player?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<Player>)

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    // --- COURSES & TEES & HOLES ---
    @Query("SELECT * FROM courses ORDER BY name ASC")
    fun getAllCoursesFlow(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE name LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%' OR state LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCoursesFlow(query: String): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: String): Course?

    @Query("SELECT * FROM tees WHERE courseId = :courseId ORDER BY yardageTotal DESC")
    fun getTeesForCourseFlow(courseId: String): Flow<List<Tee>>

    @Query("SELECT * FROM tees WHERE id = :teeId")
    suspend fun getTeeById(teeId: String): Tee?

    @Query("SELECT * FROM holes WHERE teeId = :teeId ORDER BY holeNumber ASC")
    fun getHolesForTeeFlow(teeId: String): Flow<List<Hole>>

    @Query("SELECT * FROM holes WHERE teeId = :teeId ORDER BY holeNumber ASC")
    suspend fun getHolesForTeeSync(teeId: String): List<Hole>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTees(tees: List<Tee>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoles(holes: List<Hole>)

    // --- ROUNDS ---
    @Query("SELECT * FROM rounds ORDER BY dateMillis DESC")
    fun getAllRoundsFlow(): Flow<List<Round>>

    @Query("SELECT * FROM rounds WHERE id = :roundId")
    suspend fun getRoundById(roundId: String): Round?

    @Query("SELECT * FROM rounds WHERE id = :roundId")
    fun getRoundFlow(roundId: String): Flow<Round?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: Round)

    @Update
    suspend fun updateRound(round: Round)

    @Query("DELETE FROM rounds WHERE id = :roundId")
    suspend fun deleteRound(roundId: String)

    // --- ROUND PLAYERS ---
    @Query("SELECT * FROM round_players WHERE roundId = :roundId ORDER BY playerSlot ASC")
    fun getRoundPlayersFlow(roundId: String): Flow<List<RoundPlayer>>

    @Query("SELECT * FROM round_players WHERE roundId = :roundId ORDER BY playerSlot ASC")
    suspend fun getRoundPlayersSync(roundId: String): List<RoundPlayer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoundPlayers(roundPlayers: List<RoundPlayer>)

    // --- HOLE SCORES ---
    @Query("SELECT * FROM hole_scores WHERE roundId = :roundId ORDER BY holeNumber ASC")
    fun getHoleScoresForRoundFlow(roundId: String): Flow<List<HoleScore>>

    @Query("SELECT * FROM hole_scores WHERE roundId = :roundId ORDER BY holeNumber ASC")
    suspend fun getHoleScoresForRoundSync(roundId: String): List<HoleScore>

    @Query("SELECT * FROM hole_scores WHERE roundId = :roundId AND playerId = :playerId AND holeNumber = :holeNumber LIMIT 1")
    suspend fun getHoleScore(roundId: String, playerId: String, holeNumber: Int): HoleScore?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHoleScore(holeScore: HoleScore)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHoleScores(holeScores: List<HoleScore>)
}
