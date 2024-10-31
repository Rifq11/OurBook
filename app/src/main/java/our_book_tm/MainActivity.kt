package our_book_tm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import our_book_tm.databinding.AboutItemBinding
import our_book_tm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: BookDatabaseHelper
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = BookDatabaseHelper(this)
        bookAdapter = BookAdapter(db.getAllUser(), this)

//        println(db.getAllUser().get(0).id)

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = bookAdapter

        binding.plus.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

        binding.about.setOnClickListener {
            Toast.makeText(this, "Information About App Dev", Toast.LENGTH_SHORT).show()
            aboutMe()
        }
    }

    private fun aboutMe() {
        val binding = AboutItemBinding.inflate(LayoutInflater.from(this))
        binding.fotoProfile.setImageResource(R.drawable.img_6037)
        binding.textNama.text = "Rifqi Kamaluddin"
        binding.textAbsen.text = "Number 26"
        binding.textAbsen2.text = "Class XI PPL 2"
        binding.appDate.text = "This App Create on 24 October 2024"

        val aboutInf = AlertDialog.Builder(this)
            .setView(binding.root)
            .setCancelable(true)

        aboutInf.setPositiveButton("BACK") {
            dialog, which -> dialog.dismiss()
        }

        val back = aboutInf.create()
        back.setOnShowListener {
            val backButton = back.getButton(AlertDialog.BUTTON_POSITIVE)

            backButton.setTextColor(ContextCompat.getColor(this, R.color.blue))
        }
//        back.setOnShowListener {
//            val backButton = binding.root.findViewById<Button>(R.id.backBtn)
//            backButton.setOnClickListener {
//                back.dismiss()
//            }
//        }
        back.show()
    }

    override fun onResume() {
        super.onResume()
        bookAdapter.refreshData(db.getAllUser())
    }
}