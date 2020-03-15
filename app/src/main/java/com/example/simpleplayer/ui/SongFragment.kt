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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simpleplayer.MusicManager
import com.example.simpleplayer.R
import com.example.simpleplayer.appComponent
import kotlinx.android.synthetic.main.fragment_song.*
import javax.inject.Inject


class SongFragment : Fragment(R.layout.fragment_song), OnSongClickListener {

    private val PERMISSIONS_REQUEST_READ_STORAGE: Int = 1200

    private var curFilter = ""
    private lateinit var mAdapter: AudioListAdapter
    private lateinit var musicManager: MusicManager
    private lateinit var songFragmentViewModel: SongFragmentViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireContext().appComponent.inject(this)

        songFragmentViewModel = ViewModelProviders.of(this, viewModelFactory)[SongFragmentViewModel::class.java]
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

    private fun checkPermissions(){
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
            loadSongs()
        }
    }

    override fun onResume() {
        super.onResume()
       checkPermissions()
    }

    fun loadSongs() {
        songFragmentViewModel.getSongs().observe(viewLifecycleOwner, Observer { songs->
            mAdapter.setData(songs)
        })
    }

    override fun onClick(songUri: Uri) {
        musicManager.play(songUri)
    }

    override fun onDestroy() {
        super.onDestroy()
        musicManager.release()
    }
}
