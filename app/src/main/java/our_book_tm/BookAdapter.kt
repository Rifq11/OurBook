package our_book_tm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import our_book_tm.databinding.BookItemBinding

class BookAdapter(private var books: List<Book>, context: Context): RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    private val db: BookDatabaseHelper = BookDatabaseHelper(context)

    class ViewHolder(val binding: BookItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]

        val img = book.photo
        val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)

        holder.binding.textNama.text = book.name
        holder.binding.textPanggilan.text = book.nickname
        holder.binding.textTelp.text = book.number
        holder.binding.textEmail.text = book.email
        holder.binding.textAlamat.text = book.address
        holder.binding.textTglLahir.text = book.birth
        holder.binding.foto.setImageBitmap(bitmap)

        holder.binding.editBtn.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateBookActivity::class.java).apply {
                putExtra("book_id", book.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.binding.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete Confirmation")
            builder.setMessage("Are you sure want to delete this?")

            builder.setPositiveButton("Yes") { dialog, _ ->
                db.deleteBook(book.id)
                refreshData(db.getAllUser())
                Toast.makeText(holder.itemView.context, "Information successfully deleted", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()

            alertDialog.setOnShowListener {
                val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

                positiveButton.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
                negativeButton.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
            }

            alertDialog.show()
        }

    }

    fun refreshData(newBook: List<Book>) {
        books = newBook
        notifyDataSetChanged()
    }
}