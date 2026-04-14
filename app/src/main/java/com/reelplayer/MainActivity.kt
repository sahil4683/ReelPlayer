package com.reelplayer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.reelplayer.adapter.ReelAdapter
import com.reelplayer.databinding.ActivityMainBinding
import com.reelplayer.viewmodel.VideoUiState
import com.reelplayer.viewmodel.VideoViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: VideoViewModel by viewModels()
    private var reelAdapter: ReelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        observeViewModel()

        // Trigger video scan
        viewModel.loadVideos()
    }

    private fun setupViewPager() {
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        // Smooth swipe feel — offscreen pages to keep loaded
        binding.viewPager.offscreenPageLimit = 1

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.setCurrentIndex(position)
                // Play video at the newly selected page
                reelAdapter?.playVideoAt(position, getRecyclerView())
            }
        })
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is VideoUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                    binding.viewPager.visibility = View.GONE
                }
                is VideoUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.viewPager.visibility = View.VISIBLE

                    reelAdapter = ReelAdapter(state.videos)
                    binding.viewPager.adapter = reelAdapter

                    // Auto-play first video
                    reelAdapter?.playVideoAt(0, getRecyclerView())
                }
                is VideoUiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.viewPager.visibility = View.GONE
                    binding.tvEmpty.text = "No videos found on your device."
                }
                is VideoUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * ViewPager2 wraps a RecyclerView internally. We need it to call
     * findViewHolderForAdapterPosition on the adapter.
     */
    private fun getRecyclerView(): androidx.recyclerview.widget.RecyclerView {
        return binding.viewPager.getChildAt(0) as androidx.recyclerview.widget.RecyclerView
    }

    override fun onPause() {
        super.onPause()
        reelAdapter?.pauseAll()
    }

    override fun onResume() {
        super.onResume()
        reelAdapter?.resumeCurrent()
    }

    override fun onDestroy() {
        super.onDestroy()
        reelAdapter?.releaseAll(getRecyclerView())
    }
}
