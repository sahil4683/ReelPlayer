package com.reelplayer.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reelplayer.databinding.ItemReelBinding
import com.reelplayer.model.VideoItem
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar

class ReelAdapter(
    private val videos: List<VideoItem>
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    
    private var recyclerView: RecyclerView? = null

override fun onAttachedToRecyclerView(rv: RecyclerView) {
    super.onAttachedToRecyclerView(rv)
    recyclerView = rv
}

override fun onDetachedFromRecyclerView(rv: RecyclerView) {
    super.onDetachedFromRecyclerView(rv)
    recyclerView = null
}

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

    override fun getItemCount() = videos.size

    fun playVideoAt(position: Int, recyclerView: RecyclerView) {
        currentPlayingHolder?.pause()
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as? ReelViewHolder
        holder?.play()
        currentPlayingHolder = holder
    }

    fun pauseAll() { currentPlayingHolder?.pause() }
    fun resumeCurrent() { currentPlayingHolder?.play() }

    fun releaseAll(recyclerView: RecyclerView) {
        for (i in 0 until itemCount) {
            (recyclerView.findViewHolderForAdapterPosition(i) as? ReelViewHolder)?.release()
        }
    }

    inner class ReelViewHolder(private val binding: ItemReelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var player: ExoPlayer? = null
        private var videoItem: VideoItem? = null
        private var isSpeedBoosted = false
        private var currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        private val resizeModeNames = mapOf(
            AspectRatioFrameLayout.RESIZE_MODE_FIT to "Fit",
            AspectRatioFrameLayout.RESIZE_MODE_FILL to "Fill",
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM to "Zoom"
        )

        private val handler = Handler(Looper.getMainLooper())
private val updateSeekBar = object : Runnable {
    override fun run() {
        player?.let { exo ->
            if (exo.isPlaying) {
                val position = exo.currentPosition
                val duration = exo.duration
                if (duration > 0) {
                    binding.seekBar.max = duration.toInt()
                    binding.seekBar.progress = position.toInt()
                    val cur = formatTime(position)
                    val total = formatTime(duration)
                    binding.tvTime.text = "$cur / $total"
                }
            }
        }
        handler.postDelayed(this, 500)
    }
}

private fun formatTime(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return String.format("%d:%02d", min, sec)
}

        fun bind(item: VideoItem) {
            videoItem = item

            Glide.with(binding.thumbnail.context)
                .load(Uri.parse(item.uri))
                .centerCrop()
                .into(binding.thumbnail)

            binding.tvTitle.text = item.title
            binding.tvDuration.text = item.durationFormatted
            binding.tvFolder.text = item.folderName

            // Tap to toggle play/pause
            binding.root.setOnClickListener {
                player?.let { if (it.isPlaying) pause() else play() }
            }

            binding.btnPlayPause.setOnClickListener {
                player?.let { if (it.isPlaying) pause() else play() }
            }

            binding.btnAspectRatio.setOnClickListener {
                toggleResizeMode()
            }

            updateAspectRatioLabel()

            // Long press = 2x speed, release = normal speed
            binding.root.setOnLongClickListener {
                setSpeed(2.0f)
                binding.tvSpeedIndicator.visibility = View.VISIBLE
                true
            }

            binding.root.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP ||
                    event.action == MotionEvent.ACTION_CANCEL) {
                    if (isSpeedBoosted) {
                        setSpeed(1.0f)
                        binding.tvSpeedIndicator.visibility = View.GONE
                    }
                }
                false
            }
        }

        private fun setSpeed(speed: Float) {
            isSpeedBoosted = speed > 1f
            player?.let {
                val params = it.playbackParameters
                it.playbackParameters = params.withSpeed(speed)
            }
        }

        private fun toggleResizeMode() {
            currentResizeMode = when (currentResizeMode) {
                AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                AspectRatioFrameLayout.RESIZE_MODE_FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
            binding.playerView.resizeMode = currentResizeMode
            updateAspectRatioLabel()
        }

        private fun updateAspectRatioLabel() {
            binding.tvAspectRatio.text = resizeModeNames[currentResizeMode] ?: "Zoom"
        }

        fun play() {
            val item = videoItem ?: return
            val ctx = binding.root.context

            if (player == null) {
                player = ExoPlayer.Builder(ctx).build().also { exo ->
                    binding.playerView.player = exo
                    binding.playerView.resizeMode = currentResizeMode
                    exo.repeatMode = Player.REPEAT_MODE_OFF
                    exo.volume = 1f

                    exo.setMediaItem(MediaItem.fromUri(Uri.parse(item.uri)))
                    exo.prepare()

                    exo.addListener(object : Player.Listener {
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_READY -> {
                binding.thumbnail.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            }
            Player.STATE_BUFFERING -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            Player.STATE_ENDED -> {
                // Auto-play next video
                val next = adapterPosition + 1
                if (next < itemCount) {
                    recyclerView?.smoothScrollToPosition(next)
                }
            }
        }
    }
})
                }
            }

            player?.playWhenReady = true
            // Start seekbar updates
handler.post(updateSeekBar)

// Seekbar drag to seek
binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) player?.seekTo(progress.toLong())
    }
    override fun onStartTrackingTouch(sb: SeekBar) {
        handler.removeCallbacks(updateSeekBar)
    }
    override fun onStopTrackingTouch(sb: SeekBar) {
        handler.post(updateSeekBar)
    }
})
            binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        }

        fun pause() {
            player?.playWhenReady = false
            binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
            handler.removeCallbacks(updateSeekBar)
        }

        fun release() {
            player?.release()
            player = null
            handler.removeCallbacks(updateSeekBar)
        }
    }

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.release()
        if (currentPlayingHolder == holder) currentPlayingHolder = null
    }
}
