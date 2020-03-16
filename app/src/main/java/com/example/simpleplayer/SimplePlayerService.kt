package com.example.simpleplayer

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Pair
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.Util

const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
const val PLAYBACK_CHANNEL_ID = "playback_channel"
const val PLAYBACK_NOTIFICATION_ID = 1
const val MEDIA_SESSION_TAG = "audio_demo"

class SimplePlayerService : MediaBrowserServiceCompat(){

    private var player: SimpleExoPlayer? = null
    private lateinit  var playerNotificationManager: PlayerNotificationManager
    private lateinit  var mediaSessionConnector: MediaSessionConnector
    private lateinit  var mediaSessionCompat: MediaSessionCompat
    private var oldSongUri: Uri? = null

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
    }

    private fun initializePlayer() {
        val audioAttributes =
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build()

        player = SimpleExoPlayer.Builder(this).build().apply {
            //setHandleWakeLock(true)
            setHandleAudioBecomingNoisy(true)
            setAudioAttributes(audioAttributes, true)
            addListener(playerEventListener)
        }

        mediaSessionCompat = MediaSessionCompat(
            this,
            "MediaSession",
            null,
            null
        )

        // Set initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        // Set initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        val mStateBuilder = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
        mediaSessionCompat.setPlaybackState(mStateBuilder.build())

        mediaSessionCompat.isActive = true
        // Set the session's token so that client activities can communicate with it.
        // Set the session's token so that client activities can communicate with it.
        sessionToken = mediaSessionCompat.sessionToken

        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat)
        mediaSessionConnector.setPlayer(player)

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            this,
            PLAYBACK_CHANNEL_ID,
            R.string.app_name,
            R.string.app_name,
            1,
            object : MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): String {
                    return "trackTitle"
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    return null
                }

                override fun getCurrentContentText(player: Player): String? {
                    return "Title"
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: BitmapCallback
                ): Bitmap? {
                   /* if (mStation != null) {
                        Picasso.get().load(mStation.getLogoUri())
                            .into(object : Target {
                                override fun onBitmapLoaded(
                                    bitmap: Bitmap,
                                    from: LoadedFrom
                                ) {
                                    callback.onBitmap(bitmap)
                                }

                                override fun onBitmapFailed(
                                    e: Exception,
                                    errorDrawable: Drawable
                                ) {
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
                            })
                    }*/
                    return null
                }
            }, object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    stopSelf()
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {

                    //   You need to be sure that the service is started when it begins to play by calling startService().
                    //   A started service must be explicitly stopped, whether or not it's bound.
                    //   This ensures that your player continues to perform even if the controlling UI activity unbinds.
                    //   Read more here -> https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice.html
                    if (ongoing) {
                        startService(Intent(this@SimplePlayerService, SimplePlayerService::class.java))
                        startForeground(notificationId, notification)
                    } else {
                        // Take the service out of the foreground
                        stopForeground(false)
                    }
                }
            }
        )

        playerNotificationManager.setPlayer(player)
        playerNotificationManager.setUseNavigationActions(false)
        playerNotificationManager.setFastForwardIncrementMs(0)
        playerNotificationManager.setRewindIncrementMs(0)
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        playerNotificationManager.setMediaSessionToken(mediaSessionCompat.sessionToken)

        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat)
        mediaSessionConnector.setPlayer(player)
        mediaSessionConnector.setPlaybackPreparer(object : MediaSessionConnector.PlaybackPreparer {

            override fun getSupportedPrepareActions(): Long {
                return MediaSessionConnector.PlaybackPreparer.ACTIONS
            }
            override fun onPrepare(playWhenReady: Boolean) {}
            override fun onPrepareFromMediaId(
                mediaId: String,
                playWhenReady: Boolean,
                extras: Bundle
            ) {
            }

            override fun onPrepareFromSearch(
                query: String,
                playWhenReady: Boolean,
                extras: Bundle
            ) {
            }

            override fun onCommand(
                player: Player,
                controlDispatcher: ControlDispatcher,
                command: String,
                extras: Bundle?,
                cb: ResultReceiver?
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onPrepareFromUri(
                uri: Uri,
                playWhenReady: Boolean,
                extras: Bundle
            ) {
                play(uri)
                //mStation = extras.getSerializable("STATION") as Station?
                //oldUri = uri
            }
        })
        mediaSessionConnector.setErrorMessageProvider(PlayerErrorMessageProvider())
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
                this,
                Util.getUserAgent(this, getString(R.string.app_name))
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

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        //  Browsing not allowed

        //  Browsing not allowed
        if (TextUtils.equals(EMPTY_MEDIA_ROOT_ID, parentId)) {
            result.sendResult(null)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // Clients can connect, but this BrowserRoot is an empty hierachy
        // so onLoadChildren returns nothing. This disables the ability to browse for content.

        // Clients can connect, but this BrowserRoot is an empty hierachy
        // so onLoadChildren returns nothing. This disables the ability to browse for content.
        return BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
    }

    private fun releaseResource() {
        mediaSessionCompat.isActive = false
        mediaSessionCompat.release()
        mediaSessionConnector.setPlayer(null)
        playerNotificationManager.setPlayer(null)
        player?.release()
    }

    override fun onDestroy() {
        releaseResource()
        super.onDestroy()
    }

    private inner class PlayerErrorMessageProvider :
        ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
            var errorString: String = getString(R.string.app_name)
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    if (cause.codecInfo == null) {
                        if (cause.cause is DecoderQueryException) {
                            errorString = getString(R.string.error)
                        } else if (cause.secureDecoderRequired) {
                            errorString = getString(
                                R.string.error,
                                cause.mimeType
                            )
                        } else {
                            errorString = getString(
                                R.string.error,
                                cause.mimeType
                            )
                        }
                    } else {
                        errorString = getString(
                            R.string.error,
                            cause.codecInfo!!.name
                        )
                    }
                }
            }
            return Pair.create(0, errorString)
        }
    }

}