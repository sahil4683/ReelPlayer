package com.reelplayer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.reelplayer.adapter.ReelAdapter
import com.reelplayer.databinding.ActivityMainBinding
import com.reelplayer.model.VideoItem
import com.reelplayer.util.VideoScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var reelAdapter: ReelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startIndex = intent.getIntExtra("start_index", 0)
        val uriList = intent.getStringArrayListExtra("videos")

        if (!uriList.isNullOrEmpty()) {
            // Started from VideoListActivity with pre-built list
            val videos = uriList.mapIndexed { i, uri ->
                VideoItem(i.toLong(), uri, uri.substringAfterLast("/"), 0L, 0L, 0L, "")
            }
            setupPlayer(videos, startIndex)
        } else {
            // Fallback: scan videos directly
            binding.progressBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                val videos = withContext(Dispatchers.IO) {
                    VideoScanner.scanVideos(applicationContext)
                }
                binding.progressBar.visibility = View.GONE
                if (videos.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    setupPlayer(videos, 0)
                }
            }
        }

        // Back button
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupPlayer(videos: List<VideoItem>, startIndex: Int) {
        binding.viewPager.visibility = View.VISIBLE
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        binding.viewPager.offscreenPageLimit = 1

        reelAdapter = ReelAdapter(videos)
        binding.viewPager.adapter = reelAdapter
        binding.viewPager.setCurrentItem(startIndex, false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                reelAdapter?.playVideoAt(position, getRecyclerView())
            }
        })

        // Play the starting video after layout
        binding.viewPager.post {
            reelAdapter?.playVideoAt(startIndex, getRecyclerView())
        }
    }

    private fun getRecyclerView(): androidx.recyclerview.widget.RecyclerView {
        return binding.viewPager.getChildAt(0) as androidx.recyclerview.widget.RecyclerView
    }

    override fun onPause() { super.onPause(); reelAdapter?.pauseAll() }
    override fun onResume() { super.onResume(); reelAdapter?.resumeCurrent() }
    override fun onDestroy() { super.onDestroy(); reelAdapter?.releaseAll(getRecyclerView()) }
}
