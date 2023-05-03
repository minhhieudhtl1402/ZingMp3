package com.example.zingmp3.manager

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

class MusicManager : MediaPlayer.OnPreparedListener {

    companion object {
        private var musicManager: MusicManager? = null

        fun getInstance(): MusicManager {
            if (musicManager == null) {
                musicManager = MusicManager()
            }
            return musicManager!!
        }
    }


    private constructor()

    private var mediaPlayer: MediaPlayer? = null

    fun setDataSourceOnline(context: Context, link: String) {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(context, Uri.parse(link))
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener(this)
        Log.d("link to play",link)
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
    }

    fun getCurrentPosition(): Int {
        if (mediaPlayer != null) {
            return mediaPlayer!!.currentPosition
        }
        return 0
    }

    fun getDuration(): Int {
        if (mediaPlayer != null) {
            return mediaPlayer!!.duration
        }
        return 0
    }
}