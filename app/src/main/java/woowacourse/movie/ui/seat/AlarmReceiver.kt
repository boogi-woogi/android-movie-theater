package woowacourse.movie.ui.seat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import woowacourse.movie.model.ReservationUiModel
import woowacourse.movie.ui.NotificationDialogInfo
import woowacourse.movie.ui.NotificationGenerator
import woowacourse.movie.ui.completed.CompletedActivity
import woowacourse.movie.util.SettingSharedPreference
import woowacourse.movie.util.getParcelable

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var notificationGenerator: NotificationGenerator

    override fun onReceive(context: Context, intent: Intent) {
        if (isAvailableReceivingAlarm(context)) {
            initNotificationGenerator(context)
            notificationGenerator.generate(
                dialogInfo = NotificationDialogInfo.RemindingBookingTime,
                intent = getCompletedActivityIntent(context, intent),
            )
        }
    }

    private fun isAvailableReceivingAlarm(context: Context): Boolean {
        val settingSharedPreference = SettingSharedPreference(context)
        return settingSharedPreference.receivingPushAlarm
    }

    private fun initNotificationGenerator(context: Context) {
        if (!::notificationGenerator.isInitialized) {
            notificationGenerator = NotificationGenerator(context)
        }
    }

    private fun getCompletedActivityIntent(context: Context, intent: Intent): Intent {
        val reservationUiModel = intent.getParcelable(
            RESERVATION,
            ReservationUiModel::class.java
        ) as ReservationUiModel

        return CompletedActivity.getIntent(
            context = context,
            reservation = reservationUiModel
        )
    }

    companion object {
        private const val RESERVATION = "RESERVATION"

        fun getIntent(context: Context, reservationUiModel: ReservationUiModel): Intent {
            return Intent(context, AlarmReceiver::class.java).apply {
                putExtra(RESERVATION, reservationUiModel)
            }
        }
    }
}
