package cn.edu.sicnu.cs.lyx.android4

/** 第 3 个 Fragment：每次翻 3 张牌的翻牌游戏 */
class ThreeCardGameFragment : GameFragment() {
    override val cardsPerGroup = 3
    override val fileName = "game3.dat"
    override val cardCount = 24
}
