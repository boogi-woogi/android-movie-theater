package woowacourse.movie.ui.home

import woowacourse.movie.model.main.MainModelHandler

class HomePresenter(val view: HomeContract.View) : HomeContract.Presenter {

    override fun initMainData() {
        val mainData = MainModelHandler.getMainData()

        view.initAdapter(mainData)
    }
}