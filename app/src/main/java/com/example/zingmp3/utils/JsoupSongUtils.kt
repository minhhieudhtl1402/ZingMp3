package com.example.zingmp3.utils

import android.os.AsyncTask
import com.example.zingmp3.model.Music
import org.jsoup.Jsoup

object JsoupSongUtils {
    fun parseItemSongOnline(link: String): ArrayList<Music> {
        val musics = arrayListOf<Music>()
        val doc = Jsoup.connect(link).get()
        val firstElement = doc.select("div.list-content-music")
        for (item in firstElement) {
            val linkMp3 = item.select("h3").select("a").attr("href")
            val title = item.select("h3").select("a").attr("title")
            val linkImage = item.select("a").select("img").attr("src")
            val singer = item.select("p.list-music-singer").text()
            val listenCount = item.select("span.counter").text()
            musics.add(Music(title, singer, listenCount, linkMp3, linkImage))
        }

        return musics
    }

    fun parseItemSongOnlineAsync(link: String, callback: (result: ArrayList<Music>) -> Unit) {
        object : AsyncTask<String, Void, ArrayList<Music>>() {
            override fun doInBackground(vararg params: String): ArrayList<Music> {
                val link = params[0]
                return parseItemSongOnline(link)
            }

            override fun onPostExecute(result: ArrayList<Music>?) {
                if (result != null) {
                    callback.invoke(result)
                }
            }

        }.execute(link)
    }


    class ParseHTML : AsyncTask<String, Void, ArrayList<Music>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Music> {
            val musics = arrayListOf<Music>()
            val link = params[0]
            val doc = Jsoup.connect(link).get()
            val firstElement = doc.select("div.list-content-music")
            for (item in firstElement) {
                val linkMp3 = item.select("h3").select("a").attr("href")
                val title = item.select("h3").select("a").attr("title")
                val linkImage = item.select("a").select("img").attr("src")
                val singer = item.select("p.list-music-singer").text()
                val listenCount = item.select("span.counter").text()
                musics.add(Music(title, singer, listenCount, linkMp3, linkImage))
            }

            return musics
        }

        override fun onPostExecute(result: ArrayList<Music>) {
//            musics.addAll(result)
//            binding.rcMusic.adapter?.notifyDataSetChanged()
        }
    }
}