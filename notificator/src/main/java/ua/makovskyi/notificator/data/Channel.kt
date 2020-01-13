package ua.makovskyi.notificator.data

import android.app.NotificationManager.*

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

    object MIN : Importance(PRIORITY_MIN, IMPORTANCE_MIN)
    object LOW : Importance(PRIORITY_LOW, IMPORTANCE_LOW)
    object HIGHT : Importance(PRIORITY_HIGH, IMPORTANCE_HIGH)
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
class ChannelInfo private constructor(
    internal val channelId: String,
    internal val channelName: String,
    internal val channelDescription: String?
) {

    @ChannelMarker
    class Builder(
        private var channelId: String = "CHANNEL_GENERAL",
        private var channelName: String = "GENERAL CHANNEL",
        private var channelDescription: String? = null
    ) {

        fun channelId(init: () -> String) {
            channelId = init()
        }

        fun channelName(init: () -> String) {
            channelName = init()
        }

        fun channelDescription(init: () -> String) {
            channelDescription = init()
        }

        internal fun build(init: Builder.() -> Unit): ChannelInfo {
            init()
            return build()
        }

        internal fun build(): ChannelInfo = ChannelInfo(channelId, channelName, channelDescription)
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
class GroupingParams private constructor(
    internal val groupId: String?,
    internal val groupName: String?,
    internal val groupDescription: String?
) {

    @ChannelMarker
    class Builder(
        private var groupId: String? = null,
        private var groupName: String? = null,
        private var groupDescription: String? = null
    ) {

        fun groupId(init: () -> String) {
            groupId = init()
        }

        fun groupName(init: () -> String) {
            groupName = init()
        }

        fun groupDescription(init: () -> String) {
            groupDescription = init()
        }

        internal fun build(init: Builder.() -> Unit): GroupingParams {
            init()
            return build()
        }

        internal fun build(): GroupingParams =
            GroupingParams(groupId, groupName, groupDescription)
    }
}

fun channelGroupingParams(init: GroupingParams.Builder.() -> Unit): GroupingParams = GroupingParams.Builder().build(init)

/**
 * Notification Channel settings.
 *
 * If SDK does not support Notification Channels - channelInfo and groupingParams will be ignored.
 *
 * @param importance - notification importance.
 * @param channelInfo - notification channel info parameters.
 * @param groupingParams - notification channel grouping parameters.
 */
class Channel private constructor(
    internal val importance: Importance,
    internal val channelInfo: ChannelInfo,
    internal val groupingParams: GroupingParams?
) {

    @ChannelMarker
    @NotificationMarker
    class Builder(
        private var importance: Importance = Importance.DEFAULT,
        private var channelInfo: ChannelInfo = ChannelInfo.Builder().build(),
        private var groupingParams: GroupingParams? = null
    ) {

        fun importance(init: () -> Importance?) {
            importance = init() ?: return
        }

        fun channelInfo(init: ChannelInfo.Builder.() -> Unit) {
            channelInfo = ChannelInfo.Builder().build(init)
        }

        fun groupingParams(init: GroupingParams.Builder.() -> Unit) {
            groupingParams = GroupingParams.Builder().build(init)
        }

        internal fun build(init: Builder.() -> Unit): Channel {
            init()
            return build()
        }

        internal fun build(): Channel = Channel(importance, channelInfo, groupingParams)
    }
}

fun notificationChannel(init: Channel.Builder.() -> Unit): Channel = Channel.Builder().build(init)