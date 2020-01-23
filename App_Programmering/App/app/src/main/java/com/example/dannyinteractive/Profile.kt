package com.example.dannyinteractive

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


//  TODO:
//  Check if file exists:


@Suppress("DEPRECATION")
class Profile : AppCompatActivity() {

    val REQUEST_TAKE_PHOTO = 123
    val IMAGE_PICK_CODE = 122
    private var filePath: Uri? = null
    var currentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        //  Getting SharedPreferences variables:
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val darktheme = sharedPreferences.getBoolean("dark_theme", false)
        val profilePicture = sharedPreferences.getString("profile_pic", "")

        val profileName = sharedPreferences.getString("profile_name", "")

        //  Setting Color/theme for the activity:
        if(darktheme)
            setTheme(R.style.Dark_AppTheme)
        else
            setTheme(R.style.AppTheme)

        //  Initializing Activity and layout for activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //  Sets profilePicture if exist:
        if(profilePicture != null && profilePicture != "")
        {
            currentPhotoPath = profilePicture
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            profile_profileImg.setImageBitmap(bitmap)
        }

        //  Creating click event on imageView
        profile_profileImg.setOnClickListener {
            //  Initializing
            val popupMenu = PopupMenu(this, profile_profileImg)

            popupMenu.menuInflater.inflate(R.menu.poupup_menu,popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.cam_select ->
                        openCamera()
                    R.id.lib_select ->
                        pickImageFromGallery()
                }
                true
            }
            popupMenu.show()
        }

        name_input_feild.setText(profileName)
    }


    override fun onBackPressed() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()

        editor.putString("profile_name", name_input_feild.text.toString())
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //  Get's the SharedPreference and edit it.
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()

        if(requestCode == REQUEST_TAKE_PHOTO)
        {
            editor.putString("profile_pic", currentPhotoPath).apply()
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE)
        {
            if(data == null || data.data == null) return

            filePath = data.data

            try {

                var bitmap = if(Build.VERSION.SDK_INT < 28)
                    MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)
                else
                    filePath?.let {ImageDecoder.createSource(this.contentResolver, it)}?.let { ImageDecoder.decodeBitmap(it) }!!


                currentPhotoPath = bitmap?.let { bitmapToFile(it) }.toString()

                editor.putString("profile_pic", currentPhotoPath)
                editor.apply()

            } catch (e: IOException) {
                Toast.makeText(this, "Failed saving image from cache to actual file", Toast.LENGTH_LONG).show()
            }
        }

        profile_profileImg.setImageURI(currentPhotoPath.toUri())

    }


    private fun bitmapToFile(bitmap:Bitmap): Uri {
        // Initialize a new file instance to save bitmap object
        var file = createImageFile()

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            Toast.makeText(this, "BitmapToImageConversion went wrong!", Toast.LENGTH_LONG).show()
        }

        return Uri.parse(file.absolutePath)
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }


    private fun openCamera() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

            takePictureIntent.resolveActivity(packageManager)?.also {

                //  Trying to create img_file
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    return Toast.makeText(this, "Failed to create img_file!", Toast.LENGTH_LONG).show()
                }

                //  Get's the path for the file, and open's the camera!
                photoFile?.also {

                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.dannyinteractive.fileprovider",
                        it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //  Starts camera Activity:
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {

        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        //  Returns the file
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {

            //  Set's the current filePath for later use:
            currentPhotoPath = absolutePath
        }
    }

}
