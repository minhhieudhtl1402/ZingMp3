package com.example.zingmp3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zingmp3.databinding.ItemMusicBinding
import com.example.zingmp3.model.Music

class MusicAdapter : RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private var inter: IMusicAdapter

    constructor(inter: IMusicAdapter) : super() {
        this.inter = inter
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var binding: ItemMusicBinding

        constructor(binding: ItemMusicBinding) : super(binding.root) {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMusicBinding =
            ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.music = inter.getMusic(position)
        holder.binding.root.setOnClickListener {
            inter.playMusic(holder.adapterPosition)
        }
        Glide.with(holder.itemView).load(inter.getMusic(position).linkImage)
            .into(holder.binding.civMusicAvatar)
        if (holder.adapterPosition == itemCount - 1) {
            inter.loadMore()
        }
    }


    override fun getItemCount(): Int {
        return inter.getItemCount()
    }

}

interface IMusicAdapter {
    fun getItemCount(): Int
    fun getMusic(position: Int): Music
    fun playMusic(position: Int)
    fun loadMore()
}