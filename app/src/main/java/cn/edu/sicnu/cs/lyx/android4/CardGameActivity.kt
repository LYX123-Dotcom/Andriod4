package cn.edu.sicnu.cs.lyx.android4

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cardgame.CardMatchingGame
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 翻牌游戏 Activity（MVC 中的 Controller）：
 *  - Model：cn.edu.sicnu.cardgame 包下的 Card / Deck / CardMatchingGame
 *  - View  ：activity_card_game.xml 中由 16 个 Button 组成的 GridLayout 牌桌
 *  - Controller：本类。接受用户点击、调用模型处理、updateUI() 统一同步界面，并支持重新开始一局。
 *
 * 规则：翻开的两张牌若点数相同（+16）或花色相同（+4）则配对成功，
 * 否则扣 2 分；每翻一张牌扣 1 分。
 */
class CardGameActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        /** 一局游戏的牌数（与布局中的 16 个按钮一致） */
        private const val CARD_COUNT = 16

        /** 竖屏 / 横屏下的网格列数 */
        private const val COLUMNS_PORTRAIT = 4
        private const val COLUMNS_LANDSCAPE = 8
    }

    private lateinit var game: CardMatchingGame
    private lateinit var grid: GridLayout
    private lateinit var tvScore: TextView
    private lateinit var tvHighScore: TextView
    private val cardButtons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerData.ensureInitialized(this)
        setContentView(R.layout.activity_card_game)

        tvScore = findViewById(R.id.tv_score)
        tvHighScore = findViewById(R.id.tv_high_score)

        // 按屏幕方向决定列数：竖屏 4 列（4x4），横屏 8 列（8x2）
        grid = findViewById(R.id.grid)
        grid.columnCount = columnCountFor(resources.configuration.orientation)

        // 收集布局中的 16 个按钮，统一注册点击事件
        for (i in 0 until grid.childCount) {
            val button = grid.getChildAt(i) as Button
            button.tag = i
            button.setOnClickListener(this)
            cardButtons.add(button)
        }

        game = CardMatchingGame(count = CARD_COUNT)
        updateUI()

        findViewById<MaterialButton>(R.id.btn_reset).setOnClickListener {
            game.reset()
            updateUI()
        }
        findViewById<MaterialButton>(R.id.btn_exit).setOnClickListener {
            finish()
        }
    }

    /** 旋转屏幕时只调整列数，不重建界面，游戏进度保留 */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        grid.columnCount = columnCountFor(newConfig.orientation)
    }

    private fun columnCountFor(orientation: Int): Int =
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            COLUMNS_PORTRAIT
        } else {
            COLUMNS_LANDSCAPE
        }

    /** 点击某张牌：交给模型处理后同步界面 */
    override fun onClick(v: View) {
        val index = cardButtons.indexOf(v)
        if (index < 0) return
        game.chooseCardAtIndex(index)
        updateUI()
    }

    /** 统一刷新：牌面（正面 / 背面 / 已配对）、得分与最高分 */
    private fun updateUI() {
        for (i in cardButtons.indices) {
            val button = cardButtons[i]
            val card = game.cardAtIndex(i)
            button.isEnabled = !card.isMatched
            if (card.isChosen) {
                button.text = card.toString()
                button.setBackgroundColor(Color.WHITE)
            } else {
                button.text = ""
                button.setBackgroundResource(R.drawable.card_back)
            }
        }

        tvScore.text = getString(R.string.label_score, game.score)
        if (game.score > PlayerData.highScore) {
            PlayerData.highScore = game.score
        }
        tvHighScore.text = getString(R.string.label_high_score_game, PlayerData.highScore)
        checkGameFinished()
    }

    /** 全部配对完成时弹出结算对话框 */
    private fun checkGameFinished() {
        val allMatched = game.cards.all { it.isMatched }
        if (!allMatched) return

        val isNewRecord = game.score >= PlayerData.highScore && game.score > 0
        val comment = if (isNewRecord) {
            getString(R.string.dialog_game_over_new_record)
        } else {
            getString(R.string.dialog_game_over_keep_going)
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_game_over_title))
            .setMessage(getString(R.string.dialog_game_over_message, game.score, comment))
            .setPositiveButton(getString(R.string.dialog_play_again)) { _, _ ->
                game.reset()
                updateUI()
            }
            .setNegativeButton(getString(R.string.dialog_back)) { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}
