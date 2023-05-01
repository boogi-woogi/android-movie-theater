package woowacourse.movie.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import woowacourse.movie.R
import woowacourse.movie.model.main.MainModelHandler
import woowacourse.movie.ui.booking.BookingActivity
import woowacourse.movie.ui.main.adapter.recyclerview.MainAdapter

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(view)
    }

    private fun initAdapter(view: View) {
        val mainAdapter = MainAdapter(
            requireContext(),
            { clickBook(it) },
            { clickAdvertisement(it) },
        )
        view.findViewById<RecyclerView>(R.id.listMainMovie).adapter = mainAdapter
        mainAdapter.initMovies(MainModelHandler.getMainData())
    }

    private fun clickBook(movieId: Long) {
        startActivity(BookingActivity.getIntent(requireContext(), movieId))
    }

    private fun clickAdvertisement(intent: Intent) {
        startActivity(intent)
    }
}
