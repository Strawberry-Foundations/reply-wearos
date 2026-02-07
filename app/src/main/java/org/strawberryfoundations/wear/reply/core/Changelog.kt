package org.strawberryfoundations.wear.reply.core

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

object Changelog {
    val entries = listOf(
        ChangelogEntry(
            version = "2.0.2",
            date = "Feb 7, 2026",
            changes = listOf(
                "Added session count to device sync page",
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
