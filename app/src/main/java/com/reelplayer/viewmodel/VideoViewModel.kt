package com.reelplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.reelplayer.model.VideoItem
import com.reelplayer.util.VideoScanner
import kotlinx.coroutines.launch

sealed class VideoUiState {
    object Loading : VideoUiState()
    data class Success(val videos: List<VideoItem>) : VideoUiState()
    object Empty : VideoUiState()
    data class Error(val message: String) : VideoUiState()
}

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<VideoUiState>(VideoUiState.Loading)
    val uiState: LiveData<VideoUiState> = _uiState

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    fun loadVideos() {
        _uiState.value = VideoUiState.Loading
        viewModelScope.launch {
            try {
                val videos = VideoScanner.scanVideos(getApplication())
                _uiState.value = if (videos.isEmpty()) {
                    VideoUiState.Empty
                } else {
                    VideoUiState.Success(videos)
                }
            } catch (e: Exception) {
                _uiState.value = VideoUiState.Error(e.message ?: "Failed to load videos")
            }
        }
    }

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }
}
