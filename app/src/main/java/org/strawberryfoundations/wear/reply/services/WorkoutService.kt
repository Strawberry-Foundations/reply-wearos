package org.strawberryfoundations.wear.reply.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.strawberryfoundations.wear.reply.MainActivity
import org.strawberryfoundations.wear.reply.R
import org.strawberryfoundations.wear.reply.room.AppDatabase
import org.strawberryfoundations.wear.reply.room.entities.Exercise
import org.strawberryfoundations.wear.reply.room.entities.ExerciseGroup
import org.strawberryfoundations.wear.reply.room.entities.SessionStatus
import org.strawberryfoundations.wear.reply.room.entities.WorkoutSession
import org.strawberryfoundations.wear.reply.room.entities.getExerciseGroupEmoji

class WorkoutService : Service() {
    private val binder = WorkoutBinder()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var currentSessionId: Long? = null
    private val dao by lazy { AppDatabase.getInstance(applicationContext).workoutSessionDao() }
    private val exerciseDao by lazy { AppDatabase.getInstance(applicationContext).trainingDao() }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "workout_service_channel"

        fun startService(context: Context, session: WorkoutSession) {
            val intent = Intent(context, WorkoutService::class.java).apply {
                putExtra("SESSION_ID", session.id)
                putExtra("EXERCISE_NAME", "Workout")
            }
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, WorkoutService::class.java))
        }

        fun updateSession(context: Context, session: WorkoutSession) {
            val intent = Intent(context, WorkoutService::class.java).apply {
                action = "UPDATE_SESSION"
                putExtra("SESSION_ID", session.id)
                putExtra("EXERCISE_ID", session.exerciseId)
                putExtra("STATUS", session.status.name)
                putExtra("STARTED_AT", session.startedAt)
                putExtra("ELAPSED_SECONDS", session.elapsedSeconds)
                putExtra("SETS_COMPLETED", session.setsCompleted)
                putExtra("CURRENT_WEIGHT", session.currentWeight)
            }
            context.startService(intent)
        }
    }

    inner class WorkoutBinder : Binder() {
        fun getService(): WorkoutService = this@WorkoutService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "UPDATE_SESSION" -> {
                val session = intent.toWorkoutSession()
                if (session != null) {
                    updateOngoingActivity(session)
                }
            }
            else -> {
                val sessionId = intent?.getLongExtra("SESSION_ID", -1L) ?: -1L
                if (sessionId != -1L) {
                    currentSessionId = sessionId
                    val session = runBlocking {
                        dao.getSessionByIdOnce(sessionId)
                    }
                    val exercise = session?.let { s ->
                        runBlocking {
                            exerciseDao.getById(s.exerciseId)
                        }
                    }
                    startForeground(NOTIFICATION_ID, createNotification(sessionId, session, exercise))
                }
            }
        }
        return START_STICKY
    }

    private fun updateOngoingActivity(session: WorkoutSession) {
        scope.launch {
            val exercise = exerciseDao.getById(session.exerciseId)
            val notification = createNotification(session.id, session, exercise)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun Intent.toWorkoutSession(): WorkoutSession? {
        val sessionId = getLongExtra("SESSION_ID", -1L)
        val exerciseId = getLongExtra("EXERCISE_ID", -1L)
        val statusName = getStringExtra("STATUS") ?: return null
        val startedAt = getLongExtra("STARTED_AT", -1L)
        val elapsedSeconds = getLongExtra("ELAPSED_SECONDS", 0L)
        val setsCompleted = getIntExtra("SETS_COMPLETED", 0)
        val currentWeight = getDoubleExtra("CURRENT_WEIGHT", 0.0)

        if (sessionId == -1L || exerciseId == -1L || startedAt == -1L) return null

        return WorkoutSession(
            id = sessionId,
            exerciseId = exerciseId,
            startedAt = startedAt,
            status = SessionStatus.valueOf(statusName),
            currentWeight = currentWeight,
            setsCompleted = setsCompleted,
            elapsedSeconds = elapsedSeconds,
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun createNotification(sessionId: Long, session: WorkoutSession? = null, exercise: Exercise? = null): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("SESSION_ID", sessionId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val startTime = session?.startedAt ?: System.currentTimeMillis()
        val groupEmoji = exercise?.let { getExerciseGroupEmoji(it.group) } ?: ""
        val isPaused = session?.status == SessionStatus.PAUSED
        val elapsedSeconds = when {
            session == null -> 0L
            isPaused -> session.elapsedSeconds
            else -> ((System.currentTimeMillis() - session.startedAt) / 1000).coerceAtLeast(0)
        }

        val drawable = when (exercise?.group) {
            ExerciseGroup.UPPER_BODY -> R.drawable.muscles
            ExerciseGroup.LEGS -> R.drawable.leg
            ExerciseGroup.CARDIO -> R.drawable.cardio
            else -> R.drawable.other
        }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.workout_in_progress))
            .setContentText("$groupEmoji ${session?.setsCompleted ?: 0} ${getString(R.string.sets)}")
            .setSmallIcon(drawable)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setWhen(startTime)

        if (!isPaused) {
            notificationBuilder.setUsesChronometer(true)
        }

        if (isPaused) {
            notificationBuilder.setContentText(
                "$groupEmoji ${session?.setsCompleted ?: 0} ${getString(R.string.sets)} • ${formatDuration(elapsedSeconds)}"
            )
        }

        val elapsedRealtimeAtStart = startTime - (System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime())

        if (!isPaused) {
            val ongoingActivityStatus = Status.Builder()
                .addTemplate("#time#")
                .addPart("time", Status.StopwatchPart(elapsedRealtimeAtStart))
                .build()

            val ongoingActivity = OngoingActivity.Builder(
                this,
                NOTIFICATION_ID,
                notificationBuilder
            )
                .setAnimatedIcon(drawable)
                .setStaticIcon(drawable)
                .setStatus(ongoingActivityStatus)
                .setTouchIntent(pendingIntent)
                .build()

            ongoingActivity.apply(this)
        }

        return notificationBuilder.build()
    }

    private fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            "%d:%02d:%02d".format(hours, minutes, secs)
        } else {
            "%d:%02d".format(minutes, secs)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.workout_notifications),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.workout_service_desc)
            setShowBadge(false)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
