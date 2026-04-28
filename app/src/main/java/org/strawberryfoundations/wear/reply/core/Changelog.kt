package org.strawberryfoundations.wear.reply.core

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

object Changelog {
    val entries = listOf(
        ChangelogEntry(
            version = "2.1.2",
            date = "Apr 28, 2026",
            changes = listOf(
                "[BUG] Fix pause button in ActiveExerciseView not pausing the timer",
                "[UI] Adjusted EdgeButton size in ActiveExerciseView",
                "[PRJ] Updated AGP to v9.2.0",
                "[PRJ] Dependency updates",
            )
        ),
        ChangelogEntry(
            version = "2.1.1",
            date = "Apr 7, 2026",
            changes = listOf(
                "[NEW] Added history lookup for workout sessions",
                "[NEW] Added info description for synchronization dialog",
                "[PRJ] Updated AGP to v9.1.0",
                "[PRJ] Dependency updates",
                "[PRJ] Gradle build config update to match Google's AGP 9.x version",
            )
        ),
        ChangelogEntry(
            version = "2.1.0",
            date = "Mar 13, 2026",
            changes = listOf(
                "[NEW] New app logo",
                "[PRJ] Updated several dependencies",
            )
        ),
        ChangelogEntry(
            version = "2.0.2",
            date = "Feb 7, 2026",
            changes = listOf(
                "[NEW] Added session count to device sync page",
                "[NEW] Added confirmation dialog when stopping an active exercise",
                "[UX] Updated Active Exercise Screen with better user experience"
            )
        ),
        ChangelogEntry(
            version = "2.0.1",
            date = "Feb 4, 2026",
            changes = listOf(
                "[FIX] Replaced static translations on device sync page",
                "[FIX] Fixed text alignment in exercise statistics when having insufficient data"
            )
        ),
        ChangelogEntry(
            version = "2.0.0",
            date = "Feb 2, 2026",
            changes = listOf(
                "[NEW] New exercise view with detailed info",
                "[NEW] Added support for editing workouts on wearable devices",
                "[NEW] Added active exercise tracking to track your progress",
                "[UX] Improved syncing with mobile app",
                "[UI] Redesigned user interface for better usability",
                "[PRJ] New branding",
                "[BUG] Bug fixes and performance improvements",
            )
        ),
    )

    val latestVersion: String
        get() = entries.firstOrNull()?.version ?: "Unknown"

    val latestChanges: List<String>
        get() = entries.firstOrNull()?.changes ?: emptyList()
}
