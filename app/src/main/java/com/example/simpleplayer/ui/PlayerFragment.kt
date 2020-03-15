package com.example.simpleplayer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.simpleplayer.R
import com.example.simpleplayer.appComponent
import kotlinx.android.synthetic.main.fragment_player.*
import javax.inject.Inject


class PlayerFragment : Fragment() {

    private  lateinit  var songFragmentViewModel: SongFragmentViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireContext().appComponent.inject(this)

        songFragmentViewModel = ViewModelProviders.of(this, viewModelFactory)[SongFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songFragmentViewModel.playerControlView =  player_control_view
    }

}
