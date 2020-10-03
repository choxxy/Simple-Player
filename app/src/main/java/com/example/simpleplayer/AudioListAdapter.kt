import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleplayer.OnSongClickListener
import com.example.simpleplayer.R
import com.example.simpleplayer.data.Song
import kotlinx.android.synthetic.main.item_song.view.*

class AudioListAdapter(private val context: Context, private val listener: OnSongClickListener) :
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {

    private var songList: List<Song>? = null

    class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {

        fun bind(song: Song){
            v.song_title.text = song.title

            if(song.albumArt == null)
                v.song_note_image.setImageResource(R.drawable.ic_music_note_vector)
            else
                v.song_note_image.setImageBitmap(song.albumArt)
            v.song_artist.text = song.artist
        }
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

            holder.bind(song)

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