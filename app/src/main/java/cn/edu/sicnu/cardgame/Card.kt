package cn.edu.sicnu.cardgame

class Card(private var suit: String, private var rank: String, var isChosen: Boolean = false, var isMatched: Boolean = false) {

    companion object {
        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    override fun toString(): String {
        return suit + rank
    }

    /**
     * 配对判断。
     * 传入的 otherCards 与自身一起构成待判定的一组牌（两张牌玩法传 1 张，三张牌玩法传 2 张）：
     *  - 组内任意两张点数相同：每对 +4
     *  - 组内任意两张花色相同：每对 +1
     *  - 完全没有相同的点数或花色：返回 0（判定为不配对）
     *
     * 例：三张牌中只要有两张点数或花色相同，返回大于 0，即视为这一组配对成功。
     */
    fun match(otherCards: Array<Card>): Int {
        if (otherCards.isEmpty()) return 0
        val all = otherCards + this
        var score = 0
        for (i in all.indices) {
            for (j in i + 1 until all.size) {
                if (all[i].rank == all[j].rank) {
                    score += 4
                } else if (all[i].suit == all[j].suit) {
                    score += 1
                }
            }
        }
        return score
    }
}
