package com.reelplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.reelplayer.model.VideoItem
import com.reelplayer.util.VideoScanner
import kotlinx.coroutines.launch

enum class SortOrder {
    DATE_NEWEST,
    DATE_OLDEST,
    DURATION_LONGEST,
    DURATION_SHORTEST
}

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

    private val _sortOrder = MutableLiveData(SortOrder.DATE_NEWEST)
    val sortOrder: LiveData<SortOrder> = _sortOrder

    private var allVideos: List<VideoItem> = emptyList()

    fun loadVideos() {
        _uiState.value = VideoUiState.Loading
        viewModelScope.launch {
            try {
                allVideos = VideoScanner.scanVideos(getApplication())
                applySort()
            } catch (e: Exception) {
                _uiState.value = VideoUiState.Error(e.message ?: "Failed to load videos")
            }
        }
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        applySort()
    }

    private fun applySort() {
        val sorted = when (_sortOrder.value) {
            SortOrder.DATE_NEWEST       -> allVideos.sortedByDescending { it.dateAdded }
            SortOrder.DATE_OLDEST       -> allVideos.sortedBy { it.dateAdded }
            SortOrder.DURATION_LONGEST  -> allVideos.sortedByDescending { it.duration }
            SortOrder.DURATION_SHORTEST -> allVideos.sortedBy { it.duration }
            else -> allVideos
        }
        _uiState.value = if (sorted.isEmpty()) VideoUiState.Empty
                         else VideoUiState.Success(sorted)
    }

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }
}
