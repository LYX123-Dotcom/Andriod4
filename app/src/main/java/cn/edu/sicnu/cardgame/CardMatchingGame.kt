package cn.edu.sicnu.cardgame

/**
 * 翻牌游戏模型。
 * 实验五的玩法改为三张牌：连续翻开三张未配对的牌，
 * 若三张点数全部相同或花色全部相同则配对成功，否则失配盖回。
 */
class CardMatchingGame(val count: Int) {
    var score = 0
        private set

    val cards: MutableList<Card>

    init {
        val deck = Deck()
        cards = mutableListOf()
        for (i in 1..count) {
            val card: Card? = deck.drawRandomCard()
            if (card != null) {
                cards.add(card)
            }
        }
    }

    /** 重新发一局：从 52 张牌中重新抽取 count 张，得分清零 */
    fun reset() {
        val deck = Deck()
        cards.clear()
        for (i in 1..count) {
            val card: Card? = deck.drawRandomCard()
            if (card != null) {
                cards.add(card)
            }
        }
        score = 0
    }

    fun cardAtIndex(index: Int): Card {
        return cards[index]
    }

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    /** 已翻开且未配对的牌 */
    private fun chosenCards(): List<Card> = cards.filter { it.isChosen && !it.isMatched }

    /**
     * 三张牌的翻牌逻辑：
     *  - 点击已翻开的牌：盖回去，不再扣分
     *  - 点击未翻开的牌：扣 1 分并翻开
     *  - 翻开后若已有 3 张未配对的牌，则判断三张是否配对：
     *      配对成功 → 加分并把三张标记为已配对
     *      配对失败 → 扣 2 分并把三张盖上
     */
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
        if (chosen.size < 3) return

        val matchScore = chosen[0].match(chosen.drop(1).toTypedArray())
        if (matchScore > 0) {
            score += matchScore * MATCH_BONUS
            chosen.forEach { it.isMatched = true }
        } else {
            score -= MISMATCH_PENALTY
            chosen.forEach { it.isChosen = false }
        }
    }
}
