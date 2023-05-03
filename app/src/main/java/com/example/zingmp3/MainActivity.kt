package com.example.zingmp3

import android.app.SearchManager
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zingmp3.adapter.IMusicAdapter
import com.example.zingmp3.adapter.MusicAdapter
import com.example.zingmp3.databinding.ActivityMainBinding
import com.example.zingmp3.manager.MusicManager
import com.example.zingmp3.model.Music
import com.example.zingmp3.utils.JsoupSongUtils
import org.jsoup.Jsoup
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), IMusicAdapter {
    private var initLink = "https://nhachayvn.net/bai-hat/nhac-tre"
    private val musics = arrayListOf<Music>()
    private var linkMp3: String? = null
    private lateinit var binding: ActivityMainBinding
    private var currentPage: Int = 1
    private var seekbarRunning: AsyncTask<Void, Int, String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        val searchEditText =
            binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(resources.getColor(R.color.text_search_bar))
        searchEditText.setHintTextColor(resources.getColor(R.color.text_search_bar))
        searchEditText.setTextSize(14f)

        val itemDecoration: DividerItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.rcMusic.addItemDecoration(itemDecoration)

        JsoupSongUtils.parseItemSongOnlineAsync(initLink, {
            musics.addAll(it)
            Log.d("listnhac", it.toString())
            binding.rcMusic.adapter?.notifyDataSetChanged()
        })

        val adapter = MusicAdapter(this)
        binding.rcMusic.adapter = adapter
        binding.rcMusic.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.swipeRefreshLayout.setOnRefreshListener {
            currentPage = 1
            musics.clear()
            (binding.rcMusic.adapter as MusicAdapter).notifyDataSetChanged()
            JsoupSongUtils.parseItemSongOnlineAsync(initLink, {
                musics.addAll(it)
                Log.d("listnhac", it.toString())
                binding.rcMusic.adapter?.notifyDataSetChanged()
                binding.swipeRefreshLayout.isRefreshing = false
            })

        }


    }


    inner class GetLinkMp3 : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val link = params[0]
            val doc = Jsoup.connect(link).get()
            val linkDownload = doc.select("div.box-menu-player").select("a").attr("href")
            Log.d("link download", linkDownload)
            return linkDownload
        }

        override fun onPostExecute(result: String?) {
            linkMp3 = result
            MusicManager.getInstance().setDataSourceOnline(this@MainActivity, linkMp3!!)
        }

    }

    override fun getItemCount(): Int {
        return musics.size
    }

    override fun getMusic(position: Int): Music {
        return musics.get(position)
    }

    override fun playMusic(position: Int) {
        val link = musics.get(position).linkMp3
        GetLinkMp3().execute(link)
        runSeekBar()
    }

    override fun loadMore() {
        currentPage++
        val currentLink = initLink + "/" + currentPage.toString()
        Log.d("current link", currentLink)
        JsoupSongUtils.parseItemSongOnlineAsync(currentLink,
            {
                musics.addAll(it)
                Log.d("listnhac", it.size.toString())
                binding.rcMusic.adapter?.notifyDataSetChanged()
            })
    }

    fun runSeekBar() {
        if (seekbarRunning != null) {
            seekbarRunning!!.cancel(false)
        }
        binding.sbMusic.max = MusicManager.getInstance().getDuration()
        seekbarRunning = object : AsyncTask<Void, Int, String>() {
            override fun doInBackground(vararg params: Void): String {
                while (!isCancelled) {
                    SystemClock.sleep(500)
                    publishProgress(MusicManager.getInstance().getCurrentPosition())
                    Log.d("position", MusicManager.getInstance().getCurrentPosition().toString())
                }
                return "OK"
            }

            override fun onProgressUpdate(vararg values: Int?) {
                binding.sbMusic.setProgress(values[0]!!)
            }

        }

        seekbarRunning!!.executeOnExecutor(Executors.newFixedThreadPool(1))
    }

}
