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


class CardGameModelTest {

    // ---------- Card.match ----------

    @Test
    fun match_sameRank_scoresFour() {
        val card = Card(suit = "♥", rank = "A")
        assertEquals(4, card.match(arrayOf(Card(suit = "♠", rank = "A"))))
    }

    @Test
    fun match_sameSuit_scoresOne() {
        val card = Card(suit = "♥", rank = "A")
        assertEquals(1, card.match(arrayOf(Card(suit = "♥", rank = "K"))))
    }

    @Test
    fun match_differentRankAndSuit_scoresZero() {
        val card = Card(suit = "♥", rank = "A")
        assertEquals(0, card.match(arrayOf(Card(suit = "♠", rank = "K"))))
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
    fun chooseCard_costsOnePoint() {
        val game = CardMatchingGame(2)
        game.chooseCardAtIndex(0)
        assertEquals(-1, game.score)
    }

    @Test
    fun chooseChosenCard_flipsItBackWithoutExtraCost() {
        val game = CardMatchingGame(2)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(0)
        assertFalse("再次选择应把牌盖回去", game.cardAtIndex(0).isChosen)
        assertEquals("盖回已翻开的牌不再扣分", -1, game.score)
    }

    @Test
    fun twoChoices_scoreWithinExpectedRange() {
        // 翻两张牌后：点数配对 +14，花色配对 +2，配对失败 -4
        val game = CardMatchingGame(2)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertTrue(listOf(14, 2, -4).contains(game.score))
    }

    @Test
    fun reset_redealsCardsAndClearsScore() {
        val game = CardMatchingGame(2)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.reset()
        assertEquals("重置后得分应清零", 0, game.score)
        assertEquals("重置后仍应发出 count 张牌", 2, game.cards.size)
        assertTrue("重置后的牌都不应处于已翻开状态", game.cards.none { it.isChosen || it.isMatched })
    }

    @Test
    fun matchedCard_cannotBeChosenAgain() {
        // 反复开局（有上限），直到前两张牌配对成功
        var matched: CardMatchingGame? = null
        for (attempt in 1..1000) {
            val g = CardMatchingGame(2)
            g.chooseCardAtIndex(0)
            g.chooseCardAtIndex(1)
            if (g.cardAtIndex(0).isMatched) {
                matched = g
                break
            }
        }
        assertNotNull("1000 次内应能出现配对", matched)
        val game = matched!!
        val before = game.score
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertEquals("已配对的牌不可再被选择，得分不变", before, game.score)
        assertTrue(game.cardAtIndex(0).isMatched)
    }
}
