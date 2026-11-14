package cn.edu.sicnu.cs.lyx.android4

import android.widget.Button
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GameUiTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    /** 每个用例开始前清掉序列化文件，保证从头开始，不受上一次运行影响 */
    @Before
    fun clearSavedFiles() {
        context.deleteFile("profile.dat")
        context.deleteFile("game2.dat")
        context.deleteFile("game3.dat")
    }

    private fun firstCard() = onView(
        allOf(
            isAssignableFrom(Button::class.java),
            withTagValue(equalTo(0))
        )
    )

    @Test
    fun mainActivity_showsProfileFragmentAndBottomNav() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_game2)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_game3)).check(matches(isDisplayed()))
        onView(withId(R.id.et_name)).check(matches(isDisplayed()))
        onView(withId(R.id.et_student)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_save_profile)).check(matches(isDisplayed()))
    }

    @Test
    fun profileFragment_saveThenSwitchBack_keepsInput() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.et_name)).perform(replaceText("李银祥"))
        onView(withId(R.id.et_student)).perform(replaceText("2024110519"))
        onView(withId(R.id.btn_save_profile)).perform(click())
        onView(withId(R.id.nav_game2)).perform(click())
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.et_name)).check(matches(withText("李银祥")))
    }

    @Test
    fun twoCardGame_flipFirstCard_costsOnePoint() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.nav_game2)).perform(click())
        onView(withId(R.id.tv_score)).check(
            matches(withText(context.getString(R.string.score_initial)))
        )
        firstCard().perform(click())
        onView(withId(R.id.tv_score)).check(matches(withText(containsString("-1"))))
    }

    @Test
    fun threeCardGame_showsThreeCardTitle() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.nav_game3)).perform(click())
        onView(withId(R.id.tv_game_title)).check(
            matches(withText(containsString("3 张牌")))
        )
    }

    @Test
    fun twoCardGame_resetRestoresInitialScore() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.nav_game2)).perform(click())
        firstCard().perform(click())
        onView(withId(R.id.btn_reset)).perform(click())
        onView(withId(R.id.tv_score)).check(
            matches(withText(context.getString(R.string.score_initial)))
        )
    }
}
