<div align="center">
    <h1>🏋️ Reply for WearOS</h1>
    <p>Gym workout tracking on your smartwatch.</p>
</div>

Reply for WearOS is a standalone Wear OS app focused on fast logging during workouts. It lets you manage exercises, track active sessions, and review progress directly on your watch.

## Features

- Exercise management (create, edit, delete)
- Active workout flow with set/reps/weight tracking
- Workout history and per-exercise statistics
- Volume and progress visualizations
- Wear OS Tile support for quick access
- Optional data sync using the Wearable Data Layer
- On-device persistence with Room + DataStore

## Requirements

- Android Studio (latest stable recommended)
- JDK 11
- Wear OS device or emulator
- Minimum SDK: 33
- Target SDK: 36

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Let Gradle sync complete.
4. Select a Wear OS run target (emulator or physical watch).
5. Run the `app` configuration.

## Build

Build debug APK:

```bash
./gradlew :app:assembleDebug
```

Build release APK:

```bash
./gradlew :app:assembleRelease
```

## Testing

Run unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

Run instrumentation tests (with device/emulator connected):

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Sync Notes

The app includes Data Layer sync services and message handlers. For end-to-end synchronization, a compatible counterpart on the paired phone/watch side is required.

Relevant paths used by this app include:

- `/db-sync`
- `/request-sync`
- `/sync/notify`

## License

This project is licensed under the terms of the license in `LICENSE`.
