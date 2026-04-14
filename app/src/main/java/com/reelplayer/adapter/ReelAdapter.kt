package com.reelplayer.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reelplayer.databinding.ItemReelBinding
import com.reelplayer.model.VideoItem

class ReelAdapter(
    private val videos: List<VideoItem>
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    // Track currently playing holder to pause it when swiping
    private var currentPlayingHolder: ReelViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val binding = ItemReelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    /**
     * Called by MainActivity when a new page becomes visible.
     * Pauses old video, plays new one.
     */
    fun playVideoAt(position: Int, recyclerView: RecyclerView) {
        // Pause current
        currentPlayingHolder?.pause()

        // Find and play new
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as? ReelViewHolder
        holder?.play()
        currentPlayingHolder = holder
    }

    /**
     * Pause all players (e.g. app goes to background)
     */
    fun pauseAll() {
        currentPlayingHolder?.pause()
    }

    /**
     * Resume current player
     */
    fun resumeCurrent() {
        currentPlayingHolder?.play()
    }

    /**
     * Release all ExoPlayer instances when adapter is destroyed
     */
    fun releaseAll(recyclerView: RecyclerView) {
        for (i in 0 until itemCount) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? ReelViewHolder
            holder?.release()
        }
    }

    // ─── ViewHolder ───────────────────────────────────────────────────────────

    inner class ReelViewHolder(private val binding: ItemReelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var player: ExoPlayer? = null
        private var videoItem: VideoItem? = null

        fun bind(item: VideoItem) {
            videoItem = item

            // Show thumbnail while video loads
            Glide.with(binding.thumbnail.context)
                .load(Uri.parse(item.uri))
                .centerCrop()
                .into(binding.thumbnail)

            // Video title & info
            binding.tvTitle.text = item.title
            binding.tvDuration.text = item.durationFormatted
            binding.tvFolder.text = item.folderName

            // Tap to toggle play/pause
            binding.root.setOnClickListener {
                player?.let {
                    if (it.isPlaying) pause() else play()
                }
            }

            // Tap play icon overlay
            binding.btnPlayPause.setOnClickListener {
                player?.let {
                    if (it.isPlaying) pause() else play()
                }
            }
        }

        fun play() {
            val item = videoItem ?: return
            val ctx = binding.root.context

            // Create player if not exists
            if (player == null) {
                player = ExoPlayer.Builder(ctx).build().also { exo ->
                    binding.playerView.player = exo
                    exo.repeatMode = Player.REPEAT_MODE_ONE
                    exo.volume = 1f

                    val mediaItem = MediaItem.fromUri(Uri.parse(item.uri))
                    exo.setMediaItem(mediaItem)
                    exo.prepare()

                    exo.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            if (state == Player.STATE_READY) {
                                // Hide thumbnail once video is ready
                                binding.thumbnail.visibility = View.GONE
                                binding.progressBar.visibility = View.GONE
                            } else if (state == Player.STATE_BUFFERING) {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                    })
                }
            }

            player?.playWhenReady = true
            binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        }

        fun pause() {
            player?.playWhenReady = false
            binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
        }

        fun release() {
            player?.release()
            player = null
        }
    }

    // ─── RecyclerView lifecycle callbacks ────────────────────────────────────

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.release()
        if (currentPlayingHolder == holder) {
            currentPlayingHolder = null
        }
    }
}
