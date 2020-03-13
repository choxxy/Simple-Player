package com.example.simpleplayer.ui

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleplayer.R
import com.squareup.picasso.Picasso


class AudioListAdapter(private val context: Context, private val listener: OnSongClickListener) :
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {

    var dataCursor: Cursor? = null

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var title = v.findViewById<View>(R.id.song_title) as TextView
        var image = v.findViewById<View>(R.id.song_note_image) as ImageView
        var artist = v.findViewById<View>(R.id.song_artist) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardview: View = LayoutInflater.from(context)
            .inflate(R.layout.item_song, parent, false)
        return ViewHolder(cardview)
    }

    fun setData(cursor: Cursor?) {
        dataCursor = cursor
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (dataCursor!!.moveToPosition(position)) {

            val title =
                dataCursor!!.getString(dataCursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
            holder.title.text = title
            val artist = dataCursor!!.getString(dataCursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            holder.artist.text = artist
            val albumId =
                dataCursor!!.getLong(dataCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
            val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
            val albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId!!)

            Picasso.get()
                .load(albumArtUri)
                .placeholder(R.drawable.ic_music_note_vector)
                .error(R.drawable.ic_music_note_vector)
                .into(holder.image)

            val songId =
                dataCursor!!.getLong(dataCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))

            val contentUri: Uri =
                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId!!)

            holder.itemView.setOnClickListener {
                listener.onClick(contentUri)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataCursor?.count ?: -1
    }

}