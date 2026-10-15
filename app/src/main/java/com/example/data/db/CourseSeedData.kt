package com.example.data.db

import com.example.data.model.Course
import com.example.data.model.Hole
import com.example.data.model.Player
import com.example.data.model.Tee

object CourseSeedData {

    val INITIAL_PLAYERS = listOf(
        Player(
            id = "player_1",
            name = "Gary Lantz",
            handicapIndex = 8.4,
            email = "gary.lantz@jefferson.kyschools.us",
            avatarColorHex = "#0F4D33"
        ),
        Player(
            id = "player_2",
            name = "Tiger Woods",
            handicapIndex = 0.0,
            avatarColorHex = "#D32F2F"
        ),
        Player(
            id = "player_3",
            name = "Phil Mickelson",
            handicapIndex = 3.2,
            avatarColorHex = "#1E88E5"
        ),
        Player(
            id = "player_4",
            name = "Rory McIlroy",
            handicapIndex = 1.1,
            avatarColorHex = "#FFA000"
        ),
        Player(
            id = "player_5",
            name = "Jordan Spieth",
            handicapIndex = 4.5,
            avatarColorHex = "#7B1FA2"
        ),
        Player(
            id = "player_6",
            name = "Weekend Warrior",
            handicapIndex = 18.2,
            avatarColorHex = "#0288D1"
        )
    )

    data class CourseBundle(
        val course: Course,
        val tees: List<Tee>,
        val holes: List<Hole>
    )

    fun getInitialCourses(): List<CourseBundle> {
        val list = mutableListOf<CourseBundle>()

        // 1. Pebble Beach Golf Links
        val pebbleCourse = Course(
            id = "course_pebble_beach",
            name = "Pebble Beach Golf Links",
            city = "Pebble Beach",
            state = "CA",
            country = "USA",
            holesCount = 18,
            courseType = "Resort / Championship",
            architect = "Jack Neville & Douglas Grant",
            website = "https://www.pebblebeach.com"
        )
        val pebbleTeeBlue = Tee(
            id = "tee_pebble_blue",
            courseId = pebbleCourse.id,
            name = "Blue (Championship)",
            colorHex = "#1565C0",
            courseRating = 75.5,
            slopeRating = 145,
            parTotal = 72,
            yardageTotal = 7075
        )
        val pebbleTeeGold = Tee(
            id = "tee_pebble_gold",
            courseId = pebbleCourse.id,
            name = "Gold (Member)",
            colorHex = "#D4AF37",
            courseRating = 73.1,
            slopeRating = 138,
            parTotal = 72,
            yardageTotal = 6450
        )
        val pebbleTeeWhite = Tee(
            id = "tee_pebble_white",
            courseId = pebbleCourse.id,
            name = "White (Resort)",
            colorHex = "#FFFFFF",
            courseRating = 71.3,
            slopeRating = 134,
            parTotal = 72,
            yardageTotal = 6010
        )
        // Pebble Beach hole pars and handicap indices
        // Par: [4, 5, 4, 4, 3, 5, 3, 4, 4,  4, 4, 3, 4, 5, 4, 4, 3, 5] = 72
        // Handicap ranks: 1..18
        val pebblePars = listOf(4, 5, 4, 4, 3, 5, 3, 4, 4,  4, 4, 3, 4, 5, 4, 4, 3, 5)
        val pebbleHdcp = listOf(8, 14, 12, 16, 10, 2, 18, 6, 4,  7, 5, 17, 9, 3, 13, 11, 15, 1)
        val pebbleYards = listOf(380, 516, 404, 331, 195, 523, 106, 428, 505,  495, 390, 202, 445, 580, 397, 403, 208, 543)

        val pebbleHoles = pebblePars.mapIndexed { idx, par ->
            Hole(
                id = "hole_pebble_${idx + 1}",
                courseId = pebbleCourse.id,
                teeId = pebbleTeeBlue.id,
                holeNumber = idx + 1,
                par = par,
                handicapRank = pebbleHdcp[idx],
                yardage = pebbleYards[idx]
            )
        }
        list.add(CourseBundle(pebbleCourse, listOf(pebbleTeeBlue, pebbleTeeGold, pebbleTeeWhite), pebbleHoles))

        // 2. Augusta National Golf Club
        val augustaCourse = Course(
            id = "course_augusta",
            name = "Augusta National Golf Club",
            city = "Augusta",
            state = "GA",
            country = "USA",
            holesCount = 18,
            courseType = "Championship / Private",
            architect = "Bobby Jones & Alister MacKenzie",
            website = "https://www.masters.com"
        )
        val augustaTeeMasters = Tee(
            id = "tee_augusta_masters",
            courseId = augustaCourse.id,
            name = "Masters Tees",
            colorHex = "#0F4D33",
            courseRating = 78.1,
            slopeRating = 152,
            parTotal = 72,
            yardageTotal = 7545
        )
        val augustaTeeMembers = Tee(
            id = "tee_augusta_members",
            courseId = augustaCourse.id,
            name = "Members Tees",
            colorHex = "#D4AF37",
            courseRating = 74.0,
            slopeRating = 140,
            parTotal = 72,
            yardageTotal = 6365
        )
        val augustaPars = listOf(4, 5, 4, 3, 4, 3, 4, 5, 4,  4, 4, 3, 5, 4, 5, 3, 4, 4)
        val augustaHdcp = listOf(7, 13, 15, 5, 1, 9, 11, 17, 3,  2, 6, 16, 12, 14, 8, 18, 10, 4)
        val augustaYards = listOf(445, 585, 350, 240, 495, 180, 450, 570, 460,  495, 520, 155, 545, 440, 550, 170, 440, 465)
        val augustaHoles = augustaPars.mapIndexed { idx, par ->
            Hole(
                id = "hole_augusta_${idx + 1}",
                courseId = augustaCourse.id,
                teeId = augustaTeeMasters.id,
                holeNumber = idx + 1,
                par = par,
                handicapRank = augustaHdcp[idx],
                yardage = augustaYards[idx]
            )
        }
        list.add(CourseBundle(augustaCourse, listOf(augustaTeeMasters, augustaTeeMembers), augustaHoles))

        // 3. Bethpage State Park - Black Course
        val bethpageCourse = Course(
            id = "course_bethpage_black",
            name = "Bethpage State Park (Black)",
            city = "Farmingdale",
            state = "NY",
            country = "USA",
            holesCount = 18,
            courseType = "Public Championship",
            architect = "A.W. Tillinghast",
            website = "https://parks.ny.gov/golf/bethpage"
        )
        val bethpageTeeBlack = Tee(
            id = "tee_bethpage_black",
            courseId = bethpageCourse.id,
            name = "Black (Championship)",
            colorHex = "#212121",
            courseRating = 77.5,
            slopeRating = 155,
            parTotal = 71,
            yardageTotal = 7468
        )
        val bethpagePars = listOf(4, 4, 3, 5, 4, 4, 4, 3, 4,  4, 4, 4, 5, 3, 4, 4, 3, 4)
        val bethpageHdcp = listOf(11, 7, 17, 1, 3, 9, 5, 15, 13,  6, 4, 10, 2, 18, 8, 12, 16, 14)
        val bethpageYards = listOf(430, 389, 230, 517, 478, 408, 553, 210, 460,  502, 435, 501, 608, 161, 478, 490, 207, 411)
        val bethpageHoles = bethpagePars.mapIndexed { idx, par ->
            Hole(
                id = "hole_bethpage_${idx + 1}",
                courseId = bethpageCourse.id,
                teeId = bethpageTeeBlack.id,
                holeNumber = idx + 1,
                par = par,
                handicapRank = bethpageHdcp[idx],
                yardage = bethpageYards[idx]
            )
        }
        list.add(CourseBundle(bethpageCourse, listOf(bethpageTeeBlack), bethpageHoles))

        // 4. Pinehurst Resort - No. 2
        val pinehurstCourse = Course(
            id = "course_pinehurst_2",
            name = "Pinehurst Resort - No. 2",
            city = "Pinehurst",
            state = "NC",
            country = "USA",
            holesCount = 18,
            courseType = "Resort / Championship",
            architect = "Donald Ross",
            website = "https://www.pinehurst.com"
        )
        val pinehurstTeeChampionship = Tee(
            id = "tee_pinehurst_champ",
            courseId = pinehurstCourse.id,
            name = "US Open Championship",
            colorHex = "#D32F2F",
            courseRating = 76.5,
            slopeRating = 142,
            parTotal = 70,
            yardageTotal = 7588
        )
        val pinehurstPars = listOf(4, 4, 4, 4, 5, 3, 4, 4, 3,  5, 4, 4, 4, 4, 3, 5, 3, 4)
        val pinehurstHdcp = listOf(7, 3, 11, 5, 1, 15, 9, 13, 17,  2, 6, 12, 8, 10, 16, 4, 18, 14)
        val pinehurstYards = listOf(398, 501, 387, 529, 576, 222, 424, 485, 184,  617, 483, 451, 382, 473, 202, 528, 205, 451)
        val pinehurstHoles = pinehurstPars.mapIndexed { idx, par ->
            Hole(
                id = "hole_pinehurst_${idx + 1}",
                courseId = pinehurstCourse.id,
                teeId = pinehurstTeeChampionship.id,
                holeNumber = idx + 1,
                par = par,
                handicapRank = pinehurstHdcp[idx],
                yardage = pinehurstYards[idx]
            )
        }
        list.add(CourseBundle(pinehurstCourse, listOf(pinehurstTeeChampionship), pinehurstHoles))

        // 5. TPC Sawgrass - The Players Stadium
        val sawgrassCourse = Course(
            id = "course_tpc_sawgrass",
            name = "TPC Sawgrass (Stadium Course)",
            city = "Ponte Vedra Beach",
            state = "FL",
            country = "USA",
            holesCount = 18,
            courseType = "Championship Resort",
            architect = "Pete Dye",
            website = "https://tpc.com/sawgrass"
        )
        val sawgrassTeeGold = Tee(
            id = "tee_sawgrass_gold",
            courseId = sawgrassCourse.id,
            name = "Players / Gold Tees",
            colorHex = "#D4AF37",
            courseRating = 76.4,
            slopeRating = 155,
            parTotal = 72,
            yardageTotal = 7245
        )
        val sawgrassPars = listOf(4, 5, 3, 4, 4, 4, 4, 3, 5,  4, 5, 4, 3, 4, 4, 5, 3, 4)
        val sawgrassHdcp = listOf(11, 9, 15, 7, 1, 13, 5, 17, 3,  12, 10, 16, 14, 8, 2, 6, 18, 4)
        val sawgrassYards = listOf(423, 532, 177, 384, 471, 393, 442, 237, 583,  424, 558, 358, 181, 481, 449, 523, 137, 462)
        val sawgrassHoles = sawgrassPars.mapIndexed { idx, par ->
            Hole(
                id = "hole_sawgrass_${idx + 1}",
                courseId = sawgrassCourse.id,
                teeId = sawgrassTeeGold.id,
                holeNumber = idx + 1,
                par = par,
                handicapRank = sawgrassHdcp[idx],
                yardage = sawgrassYards[idx]
            )
        }
        list.add(CourseBundle(sawgrassCourse, listOf(sawgrassTeeGold), sawgrassHoles))

        // 6. Valhalla Golf Club (Louisville, KY - Gary's home state!)
        val valhallaCourse = Course(
            id = "course_valhalla",
            name = "Valhalla Golf Club",
            city = "Louisville",
            state = "KY",
            country = "USA",
            holesCount = 18,
            courseType = "PGA Championship Private",
            architect = "Jack Nicklaus",
            website = "https://www.valhallagolfclub.com"
        )
        val valhallaTeeBlack = Tee(
            id = "tee_valhalla_black",
            courseId = valhallaCourse.id,
            name = "Championship Black",
            colorHex = "#212121",
            courseRating = 77.6,
            slopeRating = 148,
            parTotal = 71,
            yardageTotal = 7608
        )
        val valhallaPars = listOf(4, 4, 3, 4, 4, 4, 5, 3, 4,  5, 3, 4, 4, 3, 4, 4, 4, 5)
        val valhallaHdcp = listOf(9, 7, 15, 3, 5, 1, 17, 13, 11,  12, 16, 8, 6, 18, 14, 2, 4, 10)
        val valhallaYards = listOf(450, 500, 215, 372, 460, 495, 597, 190, 415,  590, 210, 470, 350, 220, 435, 505, 472, 570)
        val valhallaHoles = valhallaPars.mapIndexed { idx, par ->
            Hole(
                id = "hole_valhalla_${idx + 1}",
                courseId = valhallaCourse.id,
                teeId = valhallaTeeBlack.id,
                holeNumber = idx + 1,
                par = par,
                handicapRank = valhallaHdcp[idx],
                yardage = valhallaYards[idx]
            )
        }
        list.add(CourseBundle(valhallaCourse, listOf(valhallaTeeBlack), valhallaHoles))

        // 7. Whistling Straits - The Straits
        val straitsCourse = Course(
            id = "course_whistling_straits",
            name = "Whistling Straits (Straits Course)",
            city = "Kohler",
            state = "WI",
            country = "USA",
            holesCount = 18,
            courseType = "Resort Championship",
            architect = "Pete Dye",
            website = "https://www.destinationkohler.com"
        )
        val straitsTeeBlack = Tee(
            id = "tee_straits_black",
            courseId = straitsCourse.id,
            name = "Black / Straits",
            colorHex = "#212121",
            courseRating = 77.2,
            slopeRating = 152,
            parTotal = 72,
            yardageTotal = 7790
        )
        val straitsPars = listOf(4, 5, 3, 4, 5, 4, 3, 4, 4,  4, 5, 3, 4, 4, 4, 5, 3, 4)
        val straitsHdcp = listOf(11, 7, 17, 3, 5, 9, 15, 1, 13,  8, 4, 18, 12, 14, 6, 2, 16, 10)
        val straitsYards = listOf(408, 593, 181, 489, 603, 355, 221, 507, 446,  391, 645, 143, 404, 397, 518, 569, 223, 500)
        val straitsHoles = straitsPars.mapIndexed { idx, par ->
            Hole(
                id = "hole_straits_${idx + 1}",
                courseId = straitsCourse.id,
                teeId = straitsTeeBlack.id,
                holeNumber = idx + 1,
                par = par,
                handicapRank = straitsHdcp[idx],
                yardage = straitsYards[idx]
            )
        }
        list.add(CourseBundle(straitsCourse, listOf(straitsTeeBlack), straitsHoles))

        return list
    }
}
