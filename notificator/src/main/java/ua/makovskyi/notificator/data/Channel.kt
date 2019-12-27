package ua.makovskyi.notificator.data

import android.app.NotificationManager.*

import androidx.core.app.NotificationCompat.*

import ua.makovskyi.notificator.dsl.ChannelMarker
import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

sealed class Importance(val priority: Int, val importance: Int) {

    object MIN: Importance(PRIORITY_MIN, IMPORTANCE_MIN)
    object LOW: Importance(PRIORITY_LOW, IMPORTANCE_LOW)
    object HIGHT: Importance(PRIORITY_HIGH, IMPORTANCE_HIGH)
    object MAXIMAL: Importance(PRIORITY_MAX, IMPORTANCE_MAX)
    object DEFAULT: Importance(PRIORITY_DEFAULT, IMPORTANCE_DEFAULT)
}

class ChannelInfo private constructor(
    val id: String,
    val name: String,
    val description: String?
) {

    @ChannelMarker
    class Builder(
        var id: String = "CHANNEL_GENERAL",
        var name: String = "GENERAL CHANNEL",
        var description: String? = null
    ) {

        internal fun build(init: Builder.() -> Unit): ChannelInfo {
            init()
            return build()
        }

        internal fun build(): ChannelInfo = ChannelInfo(id, name, description)
    }
}

class GroupingParams private constructor(
    val id: String?,
    val name: String?,
    val description: String?
) {

    @ChannelMarker
    class Builder(
        var id: String? = null,
        var name: String? = null,
        var description: String? = null
    ) {

        internal fun build(init: Builder.() -> Unit): GroupingParams {
            init()
            return build()
        }

        internal fun build(): GroupingParams = GroupingParams(id, name, description)
    }
}


class Channel private constructor(
    val importance: Importance,
    val channelInfo: ChannelInfo,
    val groupingParams: GroupingParams?
) {

    @ChannelMarker
    @NotificationMarker
    class Builder(
        var importance: Importance = Importance.DEFAULT,
        var channelInfo: ChannelInfo = ChannelInfo.Builder().build(),
        var groupingParams: GroupingParams? = null
    ) {

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