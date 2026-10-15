package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Course
import com.example.data.model.Hole
import com.example.data.model.HoleScore
import com.example.data.model.Player
import com.example.data.model.Round
import com.example.data.model.RoundPlayer
import com.example.data.model.Tee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Player::class,
        Course::class,
        Tee::class,
        Hole::class,
        Round::class,
        RoundPlayer::class,
        HoleScore::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GolfDatabase : RoomDatabase() {

    abstract fun golfDao(): GolfDao

    companion object {
        @Volatile
        private var INSTANCE: GolfDatabase? = null

        fun getInstance(context: Context): GolfDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GolfDatabase::class.java,
                    "golf_database.db"
                )
                    .addCallback(GolfDatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class GolfDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                populateDatabase(getInstance(context).golfDao())
            }
        }

        private suspend fun populateDatabase(dao: GolfDao) {
            // Seed Players
            dao.insertPlayers(CourseSeedData.INITIAL_PLAYERS)

            // Seed Courses, Tees, and Holes
            val bundles = CourseSeedData.getInitialCourses()
            bundles.forEach { bundle ->
                dao.insertCourse(bundle.course)
                dao.insertTees(bundle.tees)
                dao.insertHoles(bundle.holes)
            }
        }
    }
}
