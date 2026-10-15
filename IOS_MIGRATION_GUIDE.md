# iOS Compatibility & Migration Guide for Golf Games

This document outlines how to make and deploy this Golf Games application on **iOS (iPhone & iPad)**.

Because this project was built using modern **Jetpack Compose** and **Clean Architecture (MVVM)**, you have two primary paths to run it on iOS:

---

## Path 1: Compose Multiplatform (Recommended — Share 95% of Code)

Google and JetBrains officially support **Compose Multiplatform for iOS**, which compiles the exact Jetpack Compose UI and Kotlin code directly to native iOS binaries using Kotlin/Native and Skia/Metal rendering.

### Architecture Overview
```
┌────────────────────────────────────────────────────────┐
│                   Shared Kotlin Code                   │
│  - GolfGameEngine (6's, Low Ball/Low Total, Skins)     │
│  - HandicapCalculator (USGA/WHS formulas)              │
│  - Data Models (Course, Hole, Player, HoleScore)       │
│  - Compose UI (Scorecard, Games Catalog, Leaderboards) │
└───────────────────────────┬────────────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            ▼                               ▼
    Android App (.apk)               iOS App (.ipa)
 (Jetpack Compose / JVM)       (Compose Multiplatform / Xcode)
```

### Steps to Convert:
1. **Export Project from AI Studio**:
   - In AI Studio, click the project menu (top right) and choose **Download ZIP** or **Push to GitHub**.
2. **Open in Android Studio / Fleet**:
   - Apply the JetBrains Compose plugin in `build.gradle.kts`:
     ```kotlin
     plugins {
         kotlin("multiplatform") version "2.0.0"
         id("org.jetbrains.compose") version "1.6.11"
     }
     ```
3. **Configure the iOS Target**:
   ```kotlin
   kotlin {
       androidTarget()
       listOf(
           iosX64(),
           iosArm64(),
           iosSimulatorArm64()
       ).forEach { iosTarget ->
           iosTarget.binaries.framework {
               baseName = "GolfGamesKit"
               isStatic = true
           }
       }
   }
   ```
4. **Launch Xcode**:
   - Open the generated `iosApp/iosApp.xcworkspace` in Xcode.
   - Run on iOS Simulator or your physical iPhone.

---

## Path 2: Native Swift & SwiftUI Translation

If you prefer a 100% pure Apple native app in **Swift & SwiftUI**, the architecture translates 1:1. Below is the ready-to-use Swift implementation of the core handicap and 2v2 / 6's game engine:

### 1. `HandicapCalculator.swift`
```swift
import Foundation

struct HandicapCalculator {
    /// USGA / WHS Course Handicap Formula
    static func calculateCourseHandicap(
        handicapIndex: Double,
        slopeRating: Int,
        courseRating: Double,
        parTotal: Int
    ) -> Int {
        let standardSlope = 113.0
        let base = (handicapIndex * (Double(slopeRating) / standardSlope)) + (courseRating - Double(parTotal))
        return Int(base.rounded())
    }

    /// Stroke allocation per hole based on hole handicap rank (1..18)
    static func getStrokesForHole(courseHandicap: Int, holeHandicapRank: Int) -> Int {
        if courseHandicap == 0 { return 0 }
        if courseHandicap > 0 {
            let baseStrokes = courseHandicap / 18
            let remainder = courseHandicap % 18
            let extra = (holeHandicapRank <= remainder) ? 1 : 0
            return baseStrokes + extra
        } else {
            let absH = abs(courseHandicap)
            let threshold = 18 - absH + 1
            return holeHandicapRank >= threshold ? -1 : 0
        }
    }

    /// Net score calculation
    static func calculateNetScore(grossScore: Int, strokes: Int) -> Int {
        guard grossScore > 0 else { return 0 }
        return max(1, grossScore - strokes)
    }
}
```

### 2. `GolfGameEngine.swift` (6's & Low Ball / Low Total)
```swift
import Foundation

struct LowBallLowTotalResult {
    let holeNumber: Int
    let lowBallWinnerTeam: Int?  // 1, 2, or nil (halved)
    let lowTotalWinnerTeam: Int? // 1, 2, or nil (halved)
    let team1Points: Int
    let team2Points: Int
}

struct GolfGameEngine {
    /// 6's Partner Rotations
    static func getSixesPairing(holeNumber: Int) -> (team1: [Int], team2: [Int]) {
        switch holeNumber {
        case 1...6:
            return ([0, 1], [2, 3]) // A&B vs C&D
        case 7...12:
            return ([0, 2], [1, 3]) // A&C vs B&D
        default:
            return ([0, 3], [1, 2]) // A&D vs B&C
        }
    }

    /// Low Ball / Low Total (Fixed 2v2 for 18 Holes)
    static func evaluateLowBallLowTotal(
        team1Scores: [Int],
        team2Scores: [Int]
    ) -> (lowBallTeam: Int?, lowTotalTeam: Int?, t1Pts: Int, t2Pts: Int) {
        guard let t1Low = team1Scores.min(),
              let t2Low = team2Scores.min() else {
            return (nil, nil, 0, 0)
        }
        let t1Total = team1Scores.reduce(0, +)
        let t2Total = team2Scores.reduce(0, +)

        let lowBallWinner: Int? = (t1Low < t2Low) ? 1 : ((t2Low < t1Low) ? 2 : nil)
        let lowTotalWinner: Int? = (t1Total < t2Total) ? 1 : ((t2Total < t1Total) ? 2 : nil)

        var t1Pts = 0
        var t2Pts = 0
        if lowBallWinner == 1 { t1Pts += 1 } else if lowBallWinner == 2 { t2Pts += 1 }
        if lowTotalWinner == 1 { t1Pts += 1 } else if lowTotalWinner == 2 { t2Pts += 1 }

        return (lowBallWinner, lowTotalWinner, t1Pts, t2Pts)
    }
}
```

### 3. SwiftUI vs. Jetpack Compose UI Mapping
| Compose (This App) | SwiftUI (iOS Equivalent) |
|---|---|
| `Column` | `VStack` |
| `Row` | `HStack` |
| `LazyColumn` | `List` / `LazyVStack` |
| `Card` | `RoundedRectangle` / `.cardStyle` |
| `FilterChip` | `Picker` / `.buttonStyle(.borderedProminent)` |
| `StateFlow` | `@Published` / `ObservableObject` |
| `collectAsState()` | `@ObservedObject` / `@State` |
| `ModalBottomSheet` | `.sheet(isPresented: ...)` |

---

## Summary Recommendation
1. To test and use the app immediately, run it directly in your browser emulator or install the generated Android APK.
2. When you want to publish to the Apple App Store, choose **Compose Multiplatform** to reuse the exact code you see here without rewriting your scorecards and game rules!
