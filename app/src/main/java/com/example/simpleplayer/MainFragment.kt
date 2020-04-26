package com.example.simpleplayer

import AudioListAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simpleplayer.data.repos.SongRepository
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment(R.layout.fragment_main), OnSongClickListener {

    private val PERMISSIONS_REQUEST_READ_STORAGE: Int = 1200


    private var curFilter = ""
    private lateinit var mAdapter: AudioListAdapter
    private lateinit var musicManager: MusicManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadSongs()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun askPermission(){
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        askPermission()

        mAdapter = AudioListAdapter(view.context, this)

        with(list) {
            adapter = mAdapter
            list.layoutManager = LinearLayoutManager(view.context)
        }

        musicManager = MusicManager(requireContext(), player_control_view)
    }

    private fun loadSongs() {
        SongRepository.getInstance(requireContext()).getSongs()
            .observe(viewLifecycleOwner, Observer { songs ->
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
