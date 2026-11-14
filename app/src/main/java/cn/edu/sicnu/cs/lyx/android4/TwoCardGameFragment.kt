package cn.edu.sicnu.cs.lyx.android4

/** 第 2 个 Fragment：每次翻 2 张牌的翻牌游戏 */
class TwoCardGameFragment : GameFragment() {
    override val cardsPerGroup = 2
    override val fileName = "game2.dat"
    override val cardCount = 24
}
