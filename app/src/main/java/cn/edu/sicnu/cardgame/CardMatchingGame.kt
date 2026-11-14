package cn.edu.sicnu.cardgame

import java.io.Serializable

class CardMatchingGame(
    val count: Int,
    val cardsPerGroup: Int = 2
) : Serializable {

    var score = 0
        private set

    var bestScore = 0
        private set

    val cards: MutableList<Card> = dealCards(count)

    private fun dealCards(size: Int): MutableList<Card> {
        val deck = Deck()
        val result = mutableListOf<Card>()
        for (i in 1..size) {
            val card: Card? = deck.drawRandomCard()
            if (card != null) {
                result.add(card)
            }
        }
        return result
    }

    /** 重新发一局：从 52 张牌中重新抽取 count 张，得分清零 */
    fun reset() {
        cards.clear()
        cards.addAll(dealCards(count))
        score = 0
    }

    fun cardAtIndex(index: Int): Card {
        return cards[index]
    }

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    private fun chosenCards(): List<Card> = cards.filter { it.isChosen && !it.isMatched }

    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) return

        if (card.isChosen) {
            card.isChosen = false
            return
        }

        score -= COST_TO_CHOOSE
        card.isChosen = true

        val chosen = chosenCards()
        if (chosen.size < cardsPerGroup) return

        val matchScore = chosen[0].match(chosen.drop(1).toTypedArray())
        if (matchScore > 0) {
            score += matchScore * MATCH_BONUS
            chosen.forEach { it.isMatched = true }
        } else {
            score -= MISMATCH_PENALTY
            chosen.forEach { it.isChosen = false }
        }
        if (score > bestScore) {
            bestScore = score
        }
    }
}
