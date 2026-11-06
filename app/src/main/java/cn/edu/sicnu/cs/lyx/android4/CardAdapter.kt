package cn.edu.sicnu.cs.lyx.android4

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cardgame.CardMatchingGame

/**
 * 翻牌游戏的 RecyclerView 适配器（View 层）。
 * 每张牌是一个 Button：未翻开显示牌背图片，已翻开显示“花色+点数”并设为白底，
 * 已配对则置为不可点击；点击时通过 mListener 回调把位置交给控制器。
 */
class CardAdapter(val game: CardMatchingGame) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardButton: Button

        init {
            cardButton = itemView.findViewById(R.id.button)
        }
    }

    /** 点击回调，由 Activity 注册 */
    var mListener: ((Int) -> Unit)? = null

    fun setOnClickListener(l: (Int) -> Unit) {
        mListener = l
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return game.cards.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = game.cardAtIndex(position)
        holder.cardButton.isEnabled = !card.isMatched
        if (card.isChosen) {
            holder.cardButton.text = card.toString()
            holder.cardButton.setBackgroundColor(Color.WHITE)
        } else {
            holder.cardButton.text = ""
            holder.cardButton.setBackgroundResource(R.drawable.card_back)
        }
        holder.cardButton.setOnClickListener {
            mListener?.invoke(position)
        }
        // 供 UI 测试定位某一张牌
        holder.cardButton.tag = position
    }
}
