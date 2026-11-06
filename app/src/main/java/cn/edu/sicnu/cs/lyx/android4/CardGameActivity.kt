package cn.edu.sicnu.cs.lyx.android4

import android.content.res.Configuration
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cardgame.CardMatchingGame
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 翻牌游戏 Activity（MVC 中的 Controller）：
 *  - Model：cn.edu.sicnu.cardgame 包下的 Card / Deck / CardMatchingGame（三张牌玩法）
 *  - View  ：item_layout（每张牌一个 Button）+ CardAdapter + RecyclerView 网格
 *  - Controller：本类。点击牌调用模型的 chooseCardAtIndex，updateUI 统一刷新界面。
 *
 * 横竖屏使用不同牌数：竖屏 4 列 x 6 行共 24 张，横屏 6 列 x 6 行共 36 张。
 */
class CardGameActivity : AppCompatActivity() {

    companion object {
        private const val CARD_COUNT_PORTRAIT = 24
        private const val CARD_COUNT_LANDSCAPE = 36
        private const val COLUMNS_PORTRAIT = 4
        private const val COLUMNS_LANDSCAPE = 12
    }

    private lateinit var game: CardMatchingGame
    private lateinit var adapter: CardAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvScore: TextView
    private lateinit var tvHighScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerData.ensureInitialized(this)
        setContentView(R.layout.activity_card_game)

        tvScore = findViewById(R.id.tv_score)
        tvHighScore = findViewById(R.id.tv_high_score)
        recyclerView = findViewById(R.id.recycler_view)

        startGame(resources.configuration.orientation)

        findViewById<MaterialButton>(R.id.btn_reset).setOnClickListener {
            game.reset()
            updateUI()
        }
        findViewById<MaterialButton>(R.id.btn_exit).setOnClickListener {
            finish()
        }
    }

    /** 旋转屏幕时按新的方向重建牌局（横竖屏牌数不同） */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        startGame(newConfig.orientation)
    }

    /** 按屏幕方向创建模型、适配器与网格布局 */
    private fun startGame(orientation: Int) {
        game = CardMatchingGame(cardCountFor(orientation))
        adapter = CardAdapter(game)
        recyclerView.layoutManager = GridLayoutManager(this, columnsFor(orientation))
        recyclerView.adapter = adapter
        adapter.setOnClickListener { index ->
            game.chooseCardAtIndex(index)
            updateUI()
        }
        updateUI()
    }

    private fun cardCountFor(orientation: Int): Int =
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) CARD_COUNT_LANDSCAPE else CARD_COUNT_PORTRAIT

    private fun columnsFor(orientation: Int): Int =
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) COLUMNS_LANDSCAPE else COLUMNS_PORTRAIT

    /** 统一刷新：牌面、得分与最高分，并在全部配对完成时弹结算框 */
    private fun updateUI() {
        adapter.notifyDataSetChanged()
        tvScore.text = getString(R.string.label_score, game.score)
        if (game.score > PlayerData.highScore) {
            PlayerData.highScore = game.score
        }
        tvHighScore.text = getString(R.string.label_high_score_game, PlayerData.highScore)
        checkGameFinished()
    }

    /** 全部配对完成时弹出结算对话框 */
    private fun checkGameFinished() {
        if (game.cards.isEmpty() || !game.cards.all { it.isMatched }) return

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
