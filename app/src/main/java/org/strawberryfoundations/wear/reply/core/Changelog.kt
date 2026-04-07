package org.strawberryfoundations.wear.reply.core

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

object Changelog {
    val entries = listOf(
        ChangelogEntry(
            version = "2.1.1",
            date = "Apr 7, 2026",
            changes = listOf(
                "Updated AGP to v9.1.0",
                "Dependency updates",
                "Added history lookup for workout sessions",
                "Added info description for synchronization dialog",
                "Gradle build config update to match Google's AGP 9.x version",
            )
        ),
        ChangelogEntry(
            version = "2.1.0",
            date = "Mar 13, 2026",
            changes = listOf(
                "New app logo",
                "Updated several dependencies",
            )
        ),
        ChangelogEntry(
            version = "2.0.2",
            date = "Feb 7, 2026",
            changes = listOf(
                "Added session count to device sync page",
                "Added confirmation dialog when stopping an active exercise",
                "Updated Active Exercise Screen with better user experience"
            )
        ),
        ChangelogEntry(
            version = "2.0.1",
            date = "Feb 4, 2026",
            changes = listOf(
                "Replaced static translations on device sync page",
                "Fixed text alignment in exercise statistics when having insufficient data"
            )
        ),
        ChangelogEntry(
            version = "2.0.0",
            date = "Feb 2, 2026",
            changes = listOf(
                "New exercise view with detailed info",
                "Added support for editing workouts on wearable devices",
                "Added active exercise tracking to track your progress",
                "Improved syncing with mobile app",
                "Redesigned user interface for better usability",
                "New branding",
                "Bug fixes and performance improvements",
            )
        ),
    )

    val latestVersion: String
        get() = entries.firstOrNull()?.version ?: "Unknown"

    val latestChanges: List<String>
        get() = entries.firstOrNull()?.changes ?: emptyList()
}
