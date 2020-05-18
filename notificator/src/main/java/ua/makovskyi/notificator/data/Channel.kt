package ua.makovskyi.notificator.data

import android.app.NotificationManager.*

import androidx.annotation.RestrictTo
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*

import ua.makovskyi.notificator.dsl.ChannelMarker
import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

/**
 * Notification importance parameter.
 *
 * Compatible with all SDK levels.
 *
 * @param priority - for SDK < 24 (Oreo)
 * @param importance - for SDK >= 24 (Oreo)
 */
sealed class Importance(val priority: Int, val importance: Int) {

    object NONE : Importance(PRIORITY_MIN, IMPORTANCE_NONE)
    object MIN : Importance(PRIORITY_MIN, IMPORTANCE_MIN)
    object LOW : Importance(PRIORITY_LOW, IMPORTANCE_LOW)
    object HIGH : Importance(PRIORITY_HIGH, IMPORTANCE_HIGH)
    object MAXIMAL : Importance(PRIORITY_MAX, IMPORTANCE_MAX)
    object DEFAULT : Importance(PRIORITY_DEFAULT, IMPORTANCE_DEFAULT)
}

/**
 * Notification channel info parameters.
 *
 * Will be ignored if SDK level is less than 24 (Oreo).
 *
 * @param channelId - notification channel ID.
 * @param channelName - notification channel name (human readable, will be displayed in applications manager).
 * @param channelDescription - notification channel description (human readable, will be displayed in applications manager).
 */
data class ChannelInfo(
    val channelId: String,
    val channelName: String,
    val channelDescription: String?
) {

    @ChannelMarker
    class Builder(
        private var channelId: String = "CHANNEL_GENERAL",
        private var channelName: String = "GENERAL CHANNEL",
        private var channelDescription: String? = null
    ) {

        constructor(info: ChannelInfo) : this(
            info.channelId,
            info.channelName,
            info.channelDescription)

        fun channelId(init: () -> String) {
            channelId = init()
        }

        fun channelName(init: () -> String) {
            channelName = init()
        }

        fun channelDescription(init: () -> String?) {
            channelDescription = init()
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): ChannelInfo = ChannelInfo(channelId, channelName, channelDescription)

        internal fun build(init: Builder.() -> Unit): ChannelInfo {
            init()
            return build()
        }
    }
}

/**
 * Notification channel group parameters.
 *
 * Will be ignored if SDK level is less than 24 (Oreo).
 *
 * @param groupId - id of the group. Must be unique per package. The value may be truncated if it is too long.
 * @param groupName - human readable name of the group. The recommended maximum length is 40 characters (1).
 * @param groupDescription - human readable description of this group. The recommended maximum length is 300 characters (1).
 *
 * (1) - the value may be truncated if it is too long.
 */
data class GroupingParams(
    val groupId: String?,
    val groupName: String?,
    val groupDescription: String?
) {

    @ChannelMarker
    class Builder(
        private var groupId: String? = null,
        private var groupName: String? = null,
        private var groupDescription: String? = null
    ) {

        constructor(params: GroupingParams) : this(
            params.groupId,
            params.groupName,
            params.groupDescription)

        fun groupId(init: () -> String?) {
            groupId = init()
        }

        fun groupName(init: () -> String?) {
            groupName = init()
        }

        fun groupDescription(init: () -> String?) {
            groupDescription = init()
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): GroupingParams = GroupingParams(groupId, groupName, groupDescription)

        internal fun build(init: Builder.() -> Unit): GroupingParams {
            init()
            return build()
        }
    }
}

/**
 * Notification Channel settings.
 *
 * If SDK does not support Notification Channels - channelInfo and groupingParams will be ignored.
 *
 * @param visibility - visibility on locked screen.
 * @param importance - notification importance.
 * @param channelInfo - notification channel info parameters.
 * @param groupingParams - notification channel grouping parameters.
 */
data class Channel(
    val visibility: Int,
    val importance: Importance,
    val channelInfo: ChannelInfo,
    val groupingParams: GroupingParams?
) {

    @ChannelMarker
    @NotificationMarker
    class Builder(
        @NotificationVisibility
        private var visibility: Int = VISIBILITY_PUBLIC,
        private var importance: Importance = Importance.DEFAULT,
        private var channelInfo: ChannelInfo = ChannelInfo.Builder().build(),
        private var groupingParams: GroupingParams? = null
    ) {

        constructor(channel: Channel) : this(
            channel.visibility,
            channel.importance,
            channel.channelInfo,
            channel.groupingParams)

        fun visibility(init: () -> Int) {
            visibility = init()
        }

        fun importance(init: () -> Importance?) {
            importance = init() ?: return
        }

        fun channelInfo(builder: ChannelInfo.Builder) {
            channelInfo = builder.build()
        }

        fun channelInfo(init: ChannelInfo.Builder.() -> Unit) {
            channelInfo = ChannelInfo.Builder().build(init)
        }

        fun channelInfo(info: ChannelInfo, init: ChannelInfo.Builder.() -> Unit) {
            channelInfo = ChannelInfo.Builder(info).build(init)
        }

        fun groupingParams(builder: GroupingParams.Builder) {
            groupingParams = builder.build()
        }

        fun groupingParams(init: GroupingParams.Builder.() -> Unit) {
            groupingParams = GroupingParams.Builder().build(init)
        }

        fun groupingParams(params: GroupingParams, init: GroupingParams.Builder.() -> Unit) {
            groupingParams = GroupingParams.Builder(params).build(init)
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): Channel = Channel(visibility, importance, channelInfo, groupingParams)

        internal fun build(init: Builder.() -> Unit): Channel {
            init()
            return build()
        }
    }
}

fun notificationChannel(init: Channel.Builder.() -> Unit): Channel = Channel.Builder().build(init)