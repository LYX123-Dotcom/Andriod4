package cn.edu.sicnu.cs.lyx.android4

import android.widget.Button
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GameUiTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun firstCard() = onView(
        allOf(
            isAssignableFrom(Button::class.java),
            withTagValue(equalTo(0))
        )
    )

    @Test
    fun mainActivity_showsPlayerInfoAndEntries() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.img_avatar)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_student)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_phone)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_email)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_hobby)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_high_score)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_edit_info)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_play_game)).check(matches(isDisplayed()))
    }

    @Test
    fun cardGame_flipFirstCard_costsOnePoint() {
        ActivityScenario.launch(CardGameActivity::class.java)
        onView(withId(R.id.tv_score)).check(
            matches(withText(context.getString(R.string.score_initial)))
        )
        // 翻开第一张牌（位置 0），应扣 1 分
        firstCard().perform(click())
        onView(withId(R.id.tv_score)).check(
            matches(withText(containsString("-1")))
        )
    }

    @Test
    fun cardGame_resetRestoresInitialScore() {
        ActivityScenario.launch(CardGameActivity::class.java)
        firstCard().perform(click())
        onView(withId(R.id.btn_reset)).perform(click())
        onView(withId(R.id.tv_score)).check(
            matches(withText(context.getString(R.string.score_initial)))
        )
    }
}
