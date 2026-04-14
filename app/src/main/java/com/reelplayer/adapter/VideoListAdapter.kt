package com.reelplayer.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reelplayer.databinding.ItemVideoListBinding
import com.reelplayer.model.VideoItem

class VideoListAdapter(
    private val videos: List<VideoItem>,
    private val onVideoClick: (Int) -> Unit
) : RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListViewHolder {
        val binding = ItemVideoListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VideoListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoListViewHolder, position: Int) {
        holder.bind(videos[position], position)
    }

    override fun getItemCount() = videos.size

    inner class VideoListViewHolder(private val binding: ItemVideoListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoItem, position: Int) {
            binding.tvTitle.text = item.title
            binding.tvDuration.text = item.durationFormatted
            binding.tvFolder.text = item.folderName

            Glide.with(binding.thumbnail.context)
                .load(Uri.parse(item.uri))
                .centerCrop()
                .into(binding.thumbnail)

            binding.root.setOnClickListener {
                onVideoClick(position)
            }
        }
    }
}
