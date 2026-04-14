package com.reelplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.reelplayer.adapter.VideoListAdapter
import com.reelplayer.databinding.ActivityVideoListBinding
import com.reelplayer.model.VideoItem
import com.reelplayer.viewmodel.SortOrder
import com.reelplayer.viewmodel.VideoUiState
import com.reelplayer.viewmodel.VideoViewModel

class VideoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoListBinding
    private val viewModel: VideoViewModel by viewModels()
    private var currentVideos: List<VideoItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSortSpinner()
        setupObserver()
        viewModel.loadVideos()
    }

    private fun setupSortSpinner() {
        val options = listOf("Newest First", "Oldest First", "Longest First", "Shortest First")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSort.adapter = adapter

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val order = when (pos) {
                    0 -> SortOrder.DATE_NEWEST
                    1 -> SortOrder.DATE_OLDEST
                    2 -> SortOrder.DURATION_LONGEST
                    3 -> SortOrder.DURATION_SHORTEST
                    else -> SortOrder.DATE_NEWEST
                }
                viewModel.setSortOrder(order)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupObserver() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is VideoUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                }
                is VideoUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    currentVideos = state.videos
                    binding.tvVideoCount.text = "${state.videos.size} videos"

                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = VideoListAdapter(state.videos) { position ->
                        // Open reel player at selected position
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("start_index", position)
                        intent.putParcelableArrayListExtra(
                            "videos",
                            ArrayList(state.videos.map { it.uri })
                        )
                        startActivity(intent)
                    }
                }
                is VideoUiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                }
                is VideoUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmpty.text = state.message
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            }
        }
    }
}
