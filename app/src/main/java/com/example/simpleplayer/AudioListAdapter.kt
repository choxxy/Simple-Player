import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleplayer.OnSongClickListener
import com.example.simpleplayer.R
import com.example.simpleplayer.data.Song
import com.squareup.picasso.Picasso

class AudioListAdapter(private val context: Context, private val listener: OnSongClickListener) :
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {

    private var songList: List<Song>? = null

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

    fun setData(songs: List<Song>) {
        songList = songs
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        songList?.get(position)?.let {song->
            holder.title.text = song.title
            holder.artist.text = song.artist
            Picasso.get()
                .load(song.artUri)
                .placeholder(R.drawable.ic_music_note_vector)
                .error(R.drawable.ic_music_note_vector)
                .into(holder.image)

            val contentUri: Uri =
                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.id)

            holder.itemView.setOnClickListener {
                listener.onClick(contentUri)
            }
        }
    }

    override fun getItemCount(): Int {
        return songList?.size?: -1
    }

}