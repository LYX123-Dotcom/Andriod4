package cn.edu.sicnu.cs.lyx.android4

import cn.edu.sicnu.cardgame.Card
import cn.edu.sicnu.cardgame.CardMatchingGame
import cn.edu.sicnu.cardgame.Deck
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 游戏模型（Model 层）的单元测试：
 * 覆盖 Card 的两张 / 三张配对计分、Deck 发牌，以及 CardMatchingGame 的三张牌计分与 reset。
 */
class CardGameModelTest {

    // ---------- Card.match：两张牌 ----------

    @Test
    fun match_twoCards_sameRank_scoresFour() {
        val card = Card(suit = "♥", rank = "A")
        assertEquals(4, card.match(arrayOf(Card(suit = "♠", rank = "A"))))
    }

    @Test
    fun match_twoCards_sameSuit_scoresOne() {
        val card = Card(suit = "♥", rank = "A")
        assertEquals(1, card.match(arrayOf(Card(suit = "♥", rank = "K"))))
    }

    @Test
    fun match_twoCards_different_scoresZero() {
        val card = Card(suit = "♥", rank = "A")
        assertEquals(0, card.match(arrayOf(Card(suit = "♠", rank = "K"))))
    }

    // ---------- Card.match：三张牌 ----------

    @Test
    fun match_threeCards_sameRank_eachPairScoresFour() {
        val card = Card(suit = "♥", rank = "A")
        val others = arrayOf(Card(suit = "♠", rank = "A"), Card(suit = "♣", rank = "A"))
        assertEquals("三对都同点，每对 4 分", 12, card.match(others))
    }

    @Test
    fun match_threeCards_sameSuit_eachPairScoresOne() {
        val card = Card(suit = "♥", rank = "A")
        val others = arrayOf(Card(suit = "♥", rank = "K"), Card(suit = "♥", rank = "5"))
        assertEquals("三对都同花，每对 1 分", 3, card.match(others))
    }

    @Test
    fun match_threeCards_onePairIsEnough() {
        // 只有一对同点，另外两张既不同点也不同花，仍算这一组配对成功
        val card = Card(suit = "♥", rank = "A")
        val others = arrayOf(Card(suit = "♠", rank = "A"), Card(suit = "♦", rank = "5"))
        assertEquals(4, card.match(others))
    }

    @Test
    fun match_threeCards_allDifferent_scoresZero() {
        val card = Card(suit = "♥", rank = "A")
        val others = arrayOf(Card(suit = "♠", rank = "K"), Card(suit = "♦", rank = "5"))
        assertEquals("三张点数和花色都不同，判定为不配对", 0, card.match(others))
    }

    // ---------- Deck ----------

    @Test
    fun deck_drawsAllFiftyTwoCardsThenEmpty() {
        val deck = Deck()
        repeat(52) { assertNotNull(deck.drawRandomCard()) }
        assertNull("52 张牌发完后应返回 null", deck.drawRandomCard())
    }

    // ---------- CardMatchingGame ----------

    @Test
    fun newGame_dealsRequestedCardCount() {
        val game = CardMatchingGame(24)
        assertEquals(24, game.cards.size)
        assertEquals(0, game.score)
    }

    @Test
    fun chooseCard_costsOnePoint() {
        val game = CardMatchingGame(6)
        game.chooseCardAtIndex(0)
        assertEquals(-1, game.score)
    }

    @Test
    fun chooseChosenCard_flipsItBackWithoutExtraCost() {
        val game = CardMatchingGame(6)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(0)
        assertFalse("再次选择应把牌盖回去", game.cardAtIndex(0).isChosen)
        assertEquals("盖回已翻开的牌不再扣分", -1, game.score)
    }

    @Test
    fun onlyTwoChosen_noMatchJudgementYet() {
        // 三张牌玩法：翻两张时只扣分，不判断配对
        val game = CardMatchingGame(6)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertEquals(-2, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertTrue(game.cardAtIndex(1).isChosen)
    }

    @Test
    fun thirdCard_triggersMatchJudgement() {
        // 翻满三张才判定：失配 -2（三张各扣 1，共 -5）；配对成功则加分，总分大于 0
        val game = CardMatchingGame(6)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)
        assertTrue("第三张后必然判定", game.score == -5 || game.score > 0)
    }

    @Test
    fun reset_redealsCardsAndClearsScore() {
        val game = CardMatchingGame(6)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)
        game.reset()
        assertEquals("重置后得分应清零", 0, game.score)
        assertEquals("重置后仍应发出 count 张牌", 6, game.cards.size)
        assertTrue("重置后的牌都不应处于已翻开状态", game.cards.none { it.isChosen || it.isMatched })
    }
}
