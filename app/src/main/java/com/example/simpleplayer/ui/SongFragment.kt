package com.example.simpleplayer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simpleplayer.MusicManager
import com.example.simpleplayer.R
import kotlinx.android.synthetic.main.fragment_song.*


class SongFragment : Fragment(R.layout.fragment_song), LoaderManager.LoaderCallbacks<Cursor>,
    OnSongClickListener {

    private val PERMISSIONS_REQUEST_READ_STORAGE: Int = 1200


    private var curFilter = ""
    private lateinit var mAdapter: AudioListAdapter
    private lateinit var musicManager: MusicManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_READ_STORAGE
                )

            }
        } else {
            //LoaderManager.getInstance(this).initLoader(0, null, this)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                   // LoaderManager.getInstance(this).initLoader(0, null, this)
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter =
            AudioListAdapter(view.context, this)

        with(list) {
            adapter = mAdapter
            list.layoutManager = LinearLayoutManager(view.context)
        }

//        musicManager = MusicManager(
//            requireContext(),
//            player_control_view
//        )
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val baseUri: Uri = if (curFilter != null) {
            Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Uri.encode(curFilter))
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        return CursorLoader(
            requireContext(),
            baseUri,
            projection,
            selection,
            null,
            "${MediaStore.Video.Media.DISPLAY_NAME} ASC" //Sort in alphabetical order based on display name.
        )

    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data?.let {
            mAdapter.setData(it)
        }

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mAdapter.setData(null)
    }

    var selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID

    )

    override fun onClick(songUri: Uri) {
        musicManager.play(songUri)
    }

    override fun onDestroy() {
        super.onDestroy()
        musicManager.release()
    }
}
