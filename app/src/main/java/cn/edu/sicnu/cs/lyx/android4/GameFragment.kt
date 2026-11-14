package cn.edu.sicnu.cs.lyx.android4

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cardgame.CardMatchingGame
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * 翻牌游戏 Fragment 的公共实现：
 * 两个子类只决定"每次翻几张牌"、"牌局存到哪个文件"、"共发多少张牌"。
 * 退出/切换页面时用对象序列化把整个 CardMatchingGame 写入文件，
 * 再次进入时读回来，历史牌面与得分都能恢复。
 */
abstract class GameFragment : Fragment() {

    /** 每次翻几张牌：2 表示两张牌玩法，3 表示三张牌玩法 */
    protected abstract val cardsPerGroup: Int

    /** 牌局序列化保存的文件名 */
    protected abstract val fileName: String

    /** 本局发出的牌数 */
    protected abstract val cardCount: Int

    private lateinit var game: CardMatchingGame
    private lateinit var adapter: CardAdapter
    private lateinit var tvScore: TextView
    private lateinit var tvHighScore: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvScore = view.findViewById(R.id.tv_score)
        tvHighScore = view.findViewById(R.id.tv_high_score)
        val titleRes =
            if (cardsPerGroup == 2) R.string.game2_title else R.string.game3_title
        view.findViewById<TextView>(R.id.tv_game_title).setText(titleRes)

        game = loadGame() ?: CardMatchingGame(cardCount, cardsPerGroup)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        adapter = CardAdapter(game)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), columns())
        adapter.setOnClickListener {
            game.chooseCardAtIndex(it)
            updateUI()
        }

        view.findViewById<MaterialButton>(R.id.btn_reset).setOnClickListener {
            game.reset()
            updateUI()
        }

        updateUI()
    }

    /** 竖屏 4 列，横屏 6 列 */
    private fun columns(): Int {
        val cfg = resources.configuration
        val land = cfg.orientation == Configuration.ORIENTATION_LANDSCAPE
        return if (land) 6 else 4
    }

    fun updateUI() {
        adapter.notifyDataSetChanged()
        tvScore.text = getString(R.string.label_score, game.score)
        tvHighScore.text = getString(R.string.label_high_score_game, game.bestScore)
        if (game.cards.isNotEmpty() && game.cards.all { it.isMatched }) {
            showGameOver()
        }
    }

    private var gameOverShown = false

    private fun showGameOver() {
        if (gameOverShown) return
        gameOverShown = true
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_game_over_title))
            .setMessage(getString(R.string.dialog_game_over_message_simple, game.score))
            .setPositiveButton(getString(R.string.dialog_play_again)) { _, _ ->
                game.reset()
                gameOverShown = false
                updateUI()
            }
            .setNegativeButton(getString(R.string.about_ok), null)
            .show()
    }

    override fun onStop() {
        super.onStop()
        saveGame()
    }

    private fun saveGame() {
        if (!::game.isInitialized) return
        try {
            val ctx = requireContext()
            ctx.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
                ObjectOutputStream(fos).use { oos ->
                    oos.writeObject(game)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadGame(): CardMatchingGame? {
        return try {
            requireContext().openFileInput(fileName).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    ois.readObject() as? CardMatchingGame
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
