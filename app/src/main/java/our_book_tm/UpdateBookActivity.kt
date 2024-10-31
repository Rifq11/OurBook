package our_book_tm

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.squareup.picasso.Picasso
import our_book_tm.databinding.ActivityUpdateBinding
import java.util.Calendar

class UpdateBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var db: BookDatabaseHelper
    private var bookId: Int = -1

    val CAMERA_REQUEST = 100
    val STORAGE_PERMISSION = 101

    val cameraPermissions: Array<String> = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val storagePermissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri: Uri? = result.uriContent
            Picasso.get().load(uri).into(binding.updatePhoto)
        } else {
            val error = result.error
            error?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = BookDatabaseHelper(this)

        binding.updatePhoto.setOnClickListener {
            var avatar = 0
            if (avatar == 0) {
                if (!checkCameraPermission()) {
                    requstCameraPersmission()
                } else {
                    pickFromGallery()
                }
            } else if (avatar == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }

        binding.updateBirth.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.updateBirth.setText("$selectedDay-${selectedMonth + 1}-$selectedYear")
                },
                year, month, day
            )

            datePickerDialog.window?.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
            datePickerDialog.show()
            datePickerDialog.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }

        bookId = intent.getIntExtra("book_id", -1)
        if (bookId == -1) {
            finish()
            return
        }

        val book = db.getUserById(bookId)

        val img = book.photo
        val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)

        binding.updateName.setText(book.name)
        binding.updateNickname.setText(book.nickname)
        binding.updateNumber.setText(book.number)
        binding.updateEmail.setText(book.email)
        binding.updateAddress.setText(book.address)
        binding.updateBirth.setText(book.birth)
        binding.updatePhoto.setImageBitmap(bitmap)

        binding.updateSaveButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Update Confirmation")
            builder.setMessage("Are you sure want to update this?")

            builder.setPositiveButton("Yes") { dialog, _ ->
                val newName = binding.updateName.text.toString()
                val newNickname = binding.updateNickname.text.toString()
                val newEmail = binding.updateEmail.text.toString()
                val newAddress = binding.updateAddress.text.toString()
                val newBirth = binding.updateBirth.text.toString()
                val newNumber = binding.updateNumber.text.toString()

                if (newName.isEmpty() || newNickname.isEmpty() || newEmail.isEmpty() || newAddress.isEmpty() || newBirth.isEmpty() || newNumber.isEmpty() || binding.updatePhoto.drawable == null)
                    Toast.makeText(this, "Please fill in all fields and select a photo", Toast.LENGTH_SHORT).show()
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches())
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                else {
                    val updateBook = Book(bookId, newName, newNickname, newEmail, newAddress, newBirth, newNumber, db.ImageViewToByte(binding.updatePhoto))
                    db.updateBook(updateBook)
                    finish()
                    Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()

            alertDialog.setOnShowListener {
                val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

                positiveButton.setTextColor(ContextCompat.getColor(this, R.color.blue))
                negativeButton.setTextColor(ContextCompat.getColor(this, R.color.blue))
            }

            alertDialog.show()
        }
    }

    private fun requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_PERMISSION)
    }

    private fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun pickFromGallery() {
//        val intent = Intent(this, CropImageActivity::class.java)
//        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, CAMERA_REQUEST)
//        startActivity(intent)
        cropImageLauncher.launch(CropImageContractOptions(null, CropImageOptions()))
    }

    private fun requstCameraPersmission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST)
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)
        return result && result2
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.size > 0) {
                    val cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Enable Camera and Storage Permissions", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_PERMISSION -> {
                if (grantResults.size > 0) {
                    val storegaAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storegaAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Enable Storage Permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}