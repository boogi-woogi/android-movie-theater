package woowacourse.movie.util

import woowacourse.movie.movie.ScreeningPeriod
import java.time.LocalDate

fun Movie(startDate: LocalDate, endDate: LocalDate) = woowacourse.movie.movie.Movie(
    1,
    "해리 포터와 마법사의 돌",
    ScreeningPeriod(
        startDate,
        endDate,
    ),
    152,
    "《해리 포터와 마법사의 돌》은 2001년 J. K. 롤링의 동명 소설을 원작으로 하여 만든, 영국과 미국 합작, 판타지 영화이다. 해리포터 시리즈 영화 8부작 중 첫 번째에 해당하는 작품이다. 크리스 콜럼버스가 감독을 맡았다.",
)

const val movieName = "해리 포터와 마법사의 돌"