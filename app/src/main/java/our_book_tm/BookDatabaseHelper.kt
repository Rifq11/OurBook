package our_book_tm

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class BookDatabaseHelper(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "OurBook.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "nama"
        private const val COLUMN_NICKNAME = "nickname"
        private const val COLUMN_PHOTO = "photo"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ADDRESS = "alamat"
        private const val COLUMN_BIRTH = "tglLahir"
        private const val COLUMN_NUMBER = "telp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME VARCHAR(50), " +
                "$COLUMN_NICKNAME VARCHAR(10), " +
                "$COLUMN_PHOTO BLOB, " +
                "$COLUMN_EMAIL VARCHAR(100), " +
                "$COLUMN_ADDRESS TEXT, " +
                "$COLUMN_BIRTH DATE, " +
                "$COLUMN_NUMBER VARCHAR(14)" +
                ")"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTable = ("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL(dropTable)
        onCreate(db)
    }

    fun getAllUser():List<Book> {
        val booksList = mutableListOf<Book>()
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
            val photo = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
            val birth = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH))
            val number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMBER))

            val person = Book(id, name, nickname, email, address, birth, number, photo)
            booksList.add(person)
        }
        cursor.close()
        db.close()
        return booksList
    }

    fun getUserById(id: Int): Book {
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $id "
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        val nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
        val photo = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
        val birth = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH))
        val number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMBER))

        cursor.close()
        db.close()
        return Book(id, name, nickname, email, address, birth, number, photo)
    }

    fun insertUser(book: Book) {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_NAME, book.name)
            put(COLUMN_NICKNAME, book.nickname)
            put(COLUMN_PHOTO, book.photo)
            put(COLUMN_EMAIL, book.email)
            put(COLUMN_ADDRESS, book.address)
            put(COLUMN_BIRTH, book.birth.toString())
            put(COLUMN_NUMBER, book.number)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun ImageViewToByte(img: ImageView): ByteArray {
        val bitmap: Bitmap = (img.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val bytes: ByteArray = stream.toByteArray()
        return bytes
    }

    fun updateBook(book: Book) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, book.name)
            put(COLUMN_NICKNAME, book.nickname)
            put(COLUMN_PHOTO, book.photo)
            put(COLUMN_EMAIL, book.email)
            put(COLUMN_ADDRESS, book.address)
            put(COLUMN_BIRTH, book.birth.toString())
            put(COLUMN_NUMBER, book.number)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(book.id.toString())
        db.update(TABLE_NAME, values, whereClause , whereArgs)
        db.close()
    }
//
//    fun getNoteById(noteId: Int): Book {
//        val db = readableDatabase
//        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $noteId"
//        val cursor = db.rawQuery(query,null)
//        cursor.moveToFirst()
//
//        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
//        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))
//        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
//
//        cursor.close()
//        db.close()
//        return Book(id, title, content)
//    }
//
    fun deleteBook(bookId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(bookId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}