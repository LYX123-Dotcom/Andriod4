package cn.edu.sicnu.cardgame

class Card(private var suit: String, private var rank: String, var isChosen: Boolean = false, var isMatched: Boolean = false) {

    companion object {
        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    override fun toString(): String {
        return suit + rank
    }


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
