package ecgm.com.roomforstudents

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ecgm.com.roomforstudents.api.Anuncio
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AnuncioAdapter(val context: Context,/* private val cellClickListener: CellClickListener*/): RecyclerView.Adapter<AnuncioAdapter.AnunciosViewHolder>() {


    var anuncios : List<Anuncio> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnunciosViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerline, parent, false)
        return AnunciosViewHolder(view)
    }

    override fun getItemCount(): Int {
        return anuncios.size
    }

    fun Anuncios(anuncio: List<Anuncio>){
        this.anuncios = anuncio;
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: AnunciosViewHolder, position: Int) {
        return holder.bind(anuncios[position])
    }

    inner class AnunciosViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        private val morada: TextView = itemView.findViewById(R.id.morada)
        private val telemovel: TextView = itemView.findViewById(R.id.number)
        private val fotografia: ImageView = itemView.findViewById(R.id.image)


        fun bind(anuncio: Anuncio) {
            morada.text = anuncio.morada
            telemovel.text = anuncio.telemovel

         /*   itemView.setOnClickListener {
                cellClickListener.onCellClickListener(anuncio)
            }*/

            val decodedString: ByteArray = Base64.decode(anuncio.fotografia, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            //    fotografia.setImageBitmap(decodedByte)

            val file = bitmapToFile(decodedByte)

            fotografia.setImageURI(file)

            /*     Picasso.get()
                     .load(teste)
                     .fit()
                     .centerCrop()
                     .into(fotografia);
     */
        }

        // Method to save an bitmap to a file
        private fun bitmapToFile(bitmap: Bitmap): Uri {
            // Get the context wrapper
            val wrapper = ContextWrapper(itemView.getContext())

            // Initialize a new file instance to save bitmap object
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file,"${UUID.randomUUID()}.jpg")

            try{
                // Compress the bitmap and save in jpg format
                val stream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
                stream.flush()
                stream.close()
            }catch (e: IOException){
                e.printStackTrace()
            }

            // Return the saved bitmap uri
            return Uri.parse(file.absolutePath)
        }

    }


}