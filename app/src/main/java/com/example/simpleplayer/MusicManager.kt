package com.example.simpleplayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicManager @Inject constructor(private val context: Context, private val controlView: PlayerControlView) {

    private var oldSongUri: Uri? = null
    private var player: SimpleExoPlayer? = null

    private fun initializePlayer() {
        val audioAttributes =
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build()

        player = SimpleExoPlayer.Builder(context).build().apply {
            //setHandleWakeLock(true)
            setHandleAudioBecomingNoisy(true)
            setAudioAttributes(audioAttributes, true)
            addListener(playerEventListener)
        }

        controlView.player = player

    }

    private val playerEventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {

        }
    }

    fun release() {
        player?.stop()
        player?.release()
    }

    fun play(uri: Uri) {
        if (player == null)
            initializePlayer()

        if (uri != oldSongUri) {
            if (player!!.isPlaying)
                pause()
            val mediaSource = buildMediaSource(uri)
            player!!.prepare(mediaSource)
            play()
        }
        oldSongUri = uri
    }

    private fun play() {
        player?.playWhenReady = true
    }

    private fun pause() {
        player?.playWhenReady = false
    }

    private fun buildMediaSource(uri: Uri): MediaSource {

        return ProgressiveMediaSource.Factory(
            DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, context.getString(R.string.app_name))
            )
        ).createMediaSource(uri)

    }

    private fun buildMediaSource(): MediaSource? {
        /*long[] ids = MusicUtils.getSongListForPlaylist(this, playlistId);

        MediaSource[] mediaSources = new MediaSource[ids.length];
        for (int i = 0; i < mediaSources.length; i++) {
            Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ids[i]);
            mediaSources[i] = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(songUri);
        }
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        return mediaSource;*/
        /*long[] ids = MusicUtils.getSongListForPlaylist(this, playlistId);

        MediaSource[] mediaSources = new MediaSource[ids.length];
        for (int i = 0; i < mediaSources.length; i++) {
            Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ids[i]);
            mediaSources[i] = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(songUri);
        }
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        return mediaSource;*/
        return null
    }
}