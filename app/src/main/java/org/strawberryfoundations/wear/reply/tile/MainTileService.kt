package org.strawberryfoundations.wear.reply.tile

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.expression.DynamicBuilders.DynamicFloat
import androidx.wear.protolayout.expression.PlatformEventSources
import androidx.wear.protolayout.material3.CardDefaults.filledTonalCardColors
import androidx.wear.protolayout.material3.CircularProgressIndicatorDefaults
import androidx.wear.protolayout.material3.GraphicDataCardDefaults.constructGraphic
import androidx.wear.protolayout.material3.PrimaryLayoutMargins
import androidx.wear.protolayout.material3.Typography
import androidx.wear.protolayout.material3.circularProgressIndicator
import androidx.wear.protolayout.material3.graphicDataCard
import androidx.wear.protolayout.material3.icon
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textEdgeButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.tooling.preview.TilePreviewData
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import org.strawberryfoundations.wear.reply.R
import java.util.Locale

private const val RESOURCES_VERSION = "1"
private const val ICON_ID = "ic_workout"

data class WorkoutState(
    val exerciseName: String,
    val completedSets: Int,
    val totalSets: Int
)

@OptIn(ExperimentalHorologistApi::class)
class MainTileService : SuspendingTileService() {

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources = ReplyTileRenderer.resources(this)

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val state = WorkoutState("Leg press", 3, 5)

        return TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTileTimeline(
                TimelineBuilders.Timeline.Builder().addTimelineEntry(
                    TimelineBuilders.TimelineEntry.Builder().setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(ReplyTileRenderer.layout(this, requestParams.deviceConfiguration, state))
                            .build()
                    ).build()
                ).build()
            ).build()
    }
}

object ReplyTileRenderer {

    fun layout(
        context: Context,
        deviceParameters: DeviceParameters,
        data: WorkoutState
    ): LayoutElement = materialScope(context, deviceParameters) {

        val progress = if (data.totalSets > 0) data.completedSets.toFloat() / data.totalSets else 0f
        val statusText = "${data.completedSets} / ${data.totalSets}"

        primaryLayout(
            titleSlot = { text("Reply".layoutString) },
            margins = PrimaryLayoutMargins.MIN_PRIMARY_LAYOUT_MARGIN,
            mainSlot = {
                graphicDataCard(
                    onClick = launchAppClickable(context),
                    height = expand(),
                    colors = filledTonalCardColors(),
                    title = {
                        text(
                            statusText.layoutString,
                            typography = if (deviceParameters.screenWidthDp > 200)
                                Typography.DISPLAY_LARGE else Typography.DISPLAY_SMALL
                        )
                    },
                    content = {
                        text(
                            data.exerciseName.layoutString,
                            typography = Typography.TITLE_SMALL
                        )
                    },
                    horizontalAlignment = LayoutElementBuilders.HORIZONTAL_ALIGN_END,
                    graphic = {
                        constructGraphic(
                            mainContent = {
                                circularProgressIndicator(
                                    staticProgress = progress,
                                    dynamicProgress = DynamicFloat.onCondition(
                                        PlatformEventSources.isLayoutVisible()
                                    )
                                        .use(progress)
                                        .elseUse(0f)
                                        .animate(CircularProgressIndicatorDefaults.recommendedAnimationSpec),
                                    startAngleDegrees = 200f,
                                    endAngleDegrees = 520f
                                )
                            },
                            iconContent = {
                                icon(ICON_ID)
                            }
                        )
                    }
                )
            },
            bottomSlot = {
                textEdgeButton(onClick = launchAppClickable(context)) {
                    text("Start".layoutString)
                }
            }
        )
    }

    fun resources(context: Context): ResourceBuilders.Resources {
        return ResourceBuilders.Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            // Hier verknüpfst du die ID mit deinem Hantel-Icon
            .addIdToImageMapping(
                ICON_ID,
                ResourceBuilders.ImageResource.Builder()
                    .setAndroidResourceByResId(
                        ResourceBuilders.AndroidImageResourceByResId.Builder()
                            .setResourceId(R.drawable.ic_launcher) // Prüfe diesen Namen!
                            .build()
                    ).build()
            ).build()
    }

    private fun launchAppClickable(context: Context): ModifiersBuilders.Clickable {
        return ModifiersBuilders.Clickable.Builder()
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName(context.packageName)
                            .setClassName("org.strawberryfoundations.wear.reply.MainActivity")
                            .build()
                    ).build()
            ).build()
    }
}