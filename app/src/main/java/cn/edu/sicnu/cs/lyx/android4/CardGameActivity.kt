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


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        startGame(newConfig.orientation)
    }


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


    private fun updateUI() {
        adapter.notifyDataSetChanged()
        tvScore.text = getString(R.string.label_score, game.score)
        if (game.score > PlayerData.highScore) {
            PlayerData.highScore = game.score
        }
        tvHighScore.text = getString(R.string.label_high_score_game, PlayerData.highScore)
        checkGameFinished()
    }


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
