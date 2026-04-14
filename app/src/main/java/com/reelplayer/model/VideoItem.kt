package com.reelplayer.model

data class VideoItem(
    val id: Long,
    val uri: String,
    val title: String,
    val duration: Long,       // in milliseconds
    val size: Long,           // in bytes
    val dateAdded: Long,      // timestamp
    val folderName: String
) {
    val durationFormatted: String
        get() {
            val totalSeconds = duration / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
}
