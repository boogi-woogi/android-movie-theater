package woowacourse.movie.ui.seat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import woowacourse.movie.R
import woowacourse.movie.SelectResult
import woowacourse.movie.SelectedSeats
import woowacourse.movie.model.BookedMovie
import woowacourse.movie.model.Mapper.toDomainModel
import woowacourse.movie.model.Mapper.toUiModel
import woowacourse.movie.model.SelectedSeatUiModel
import woowacourse.movie.movie.Movie
import woowacourse.movie.reservation.Reservation
import woowacourse.movie.reservation.ReservationRepository
import woowacourse.movie.theater.Theater
import woowacourse.movie.ticket.Seat
import woowacourse.movie.ticket.Ticket
import woowacourse.movie.ui.completed.CompletedActivity
import woowacourse.movie.util.getParcelable
import woowacourse.movie.util.getParcelableBundle
import woowacourse.movie.util.shortToast

class SeatActivity : AppCompatActivity(), SeatContract.View {

    private val movieTitleText: TextView by lazy {
        findViewById(R.id.textSeatMovieTitle)
    }
    private val textPayment by lazy {
        findViewById<TextView>(R.id.textSeatPayment)
    }
    private val buttonConfirm by lazy {
        findViewById<TextView>(R.id.buttonSeatConfirm)
    }
    private val seatTable by lazy {
        findViewById<SeatTableLayout>(R.id.seatTableLayout)
    }
    private val bookedMovie: BookedMovie? by lazy {
        intent.getParcelable(BOOKED_MOVIE, BookedMovie::class.java)
    }
    private val seatPresenter: SeatPresenter by lazy {
        bookedMovie?.let {
            SeatPresenter(
                view = this,
                bookedMovie = it
            )
        } ?: throw IllegalArgumentException(RECEIVING_MOVIE_ERROR)
    }

    private lateinit var movie: Movie
    private lateinit var theater: Theater
    private lateinit var selectedSeats: SelectedSeats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat)

        seatPresenter.initMovieTitle()
        // initSeatTable()
        // initView()
        // clickConfirmButton()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(SELECTED_SEAT, selectedSeats.toUiModel())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val selectedSeatUiModel =
            savedInstanceState.getParcelableBundle(SELECTED_SEAT, SelectedSeatUiModel::class.java)
        selectedSeatUiModel.selectedSeat.forEach {
            setSeatState(it.toDomainModel())
        }
    }

    override fun initMovieTitleText(movieTitle: String) {
        movieTitleText.text = movieTitle
    }

    private fun initSeatTable() {
        seatTable.setView(theater.rowSize, theater.columnSize)
        seatTable.setColorRange(
            mapOf(
                theater.sRankRange to R.color.green_400,
                theater.aRankRange to R.color.blue_500,
                theater.bRankRange to R.color.purple_400,
            ),
        )
        table.setClickListener { clickedPosition ->
            val seat = theater.selectSeat(clickedPosition)
            setSeatState(seat)
        }
    }

    private fun setSeatState(seat: Seat) {
        val view = table[seat.position.row][seat.position.column]
        val result = selectedSeats.clickSeat(seat)
        when (result) {
            SelectResult.Select.Full -> shortToast("더 이상 좌석을 선택할 수 없습니다.")
            SelectResult.Select.Success -> view.isSelected = !view.isSelected
            SelectResult.Unselect -> view.isSelected = !view.isSelected
        }
        setConfirmButtonEnable(selectedSeats.isSeatFull)
        setPayment(selectedSeats.seats)
    }

    private fun setConfirmButtonEnable(isSeatFull: Boolean) {
        buttonConfirm.isEnabled = isSeatFull

        if (buttonConfirm.isEnabled) {
            buttonConfirm.setBackgroundResource(R.color.purple_700)
            return
        }
        buttonConfirm.setBackgroundResource(R.color.gray_400)
    }

    private fun clickConfirmButton() {
        buttonConfirm.setOnClickListener {
            showDialog()
        }
    }

    private fun completeBooking() {
        val tickets: List<Ticket> =
            selectedSeats.seats.map { movie.reserve(bookedMovie.bookedDateTime, it) }
        val reservation = Reservation(tickets.toSet())
        ReservationRepository.addReservation(reservation)
        ScreeningTimeReminder(this, reservation.toUiModel())
        startActivity(CompletedActivity.getIntent(this, reservation.toUiModel()))
        finish()
    }

    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("예매 확인")
            .setMessage("정말 예매하시겠습니까?")
            .setPositiveButton("예") { _, _ -> completeBooking() }
            .setNegativeButton("아니요") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setPayment(seats: Set<Seat>) {
        val tickets: List<Ticket> = seats.map { movie.reserve(bookedMovie.bookedDateTime, it) }
        textPayment.text = tickets.sumOf { it.price }.toString() + "원"
    }

    companion object {
        private const val RECEIVING_MOVIE_ERROR = "예약하는 영화의 정보를 받아올 수 없습니다."
        private const val BOOKED_MOVIE = "BOOKED_MOVIE"
        private const val SELECTED_SEAT = "SELECTED_SEAT"

        fun getIntent(context: Context, bookedMovie: BookedMovie?): Intent {
            return Intent(context, SeatActivity::class.java).apply {
                putExtra(BOOKED_MOVIE, bookedMovie)
            }
        }
    }
}
