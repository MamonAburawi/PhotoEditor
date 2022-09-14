package com.example.photoeditor

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.photoeditor.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import java.io.File
import java.io.FileOutputStream


/**

this project build from : https://github.com/burhanrashid52/PhotoEditor

 * **/

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mPhotoEditor: PhotoEditor
    private lateinit var imagePicker: ImagePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)


        imagePicker = ImagePicker(this)

        mPhotoEditor = PhotoEditor.Builder(this@MainActivity,binding.photoEditorView)
            .setPinchTextScalable(true)
            .setClipSourceImage(true)
            .build()


        checkPermission()

        isEnableButtons(false)

        setViews()


    }


    private fun setViews() {
        binding.apply {


            /** button brush **/
            btnBrush.setOnClickListener {
                unSelectButtons()
                btnBrush.setColorFilter(R.color.purple_200)
                mPhotoEditor.setBrushDrawingMode(true)
            }

            /** button brush **/
            btnEraser.setOnClickListener {
                unSelectButtons()
                btnEraser.setColorFilter(R.color.purple_200)
                mPhotoEditor.brushEraser()
            }

            /** button brush **/
            btnText.setOnClickListener {
                unSelectButtons()
                mPhotoEditor.addText("sdfsd", TextStyleBuilder())

            }

            /** button brush **/
            btnEmoji.setOnClickListener {
                unSelectButtons()
                btnEmoji.setColorFilter(R.color.purple_200)
            }

            /** button brush **/
            btnFilter.setOnClickListener {
                unSelectButtons()
                btnFilter.setColorFilter(R.color.purple_200)
            }

            /** button brush **/
            btnSticker.setOnClickListener {
                unSelectButtons()
                btnSticker.setColorFilter(R.color.purple_200)
            }


            /** button camera **/
            btnGallery.setOnClickListener {
                imagePicker.pickFromStorage { imageResult ->
                    imageCallBack(imageResult)
                }
            }


            /** button camera **/
            btnCamera.setOnClickListener {
                imagePicker.takeFromCamera { imageResult ->
                    imageCallBack(imageResult)
                }
            }

            /** button save **/
            btnSave.setOnClickListener {
                save()
            }


        }



    }

    private fun checkPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {

                }
            }).check()
    }


    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        var outputStream: FileOutputStream? = null
        val file = Environment.getExternalStorageDirectory()
        val fileName = "PhotoEditor"
        val dir = File(file,fileName)
        if (!dir.exists()) { // if file is not exist then create one.
            dir.mkdirs()
        }
        val filename = String.format("%d.jpg", System.currentTimeMillis())
        val outfile = File(dir, filename)
        try {
            outputStream = FileOutputStream(outfile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Log.d("PhotoEditor", "SaveImageToGallery: " + e.message)
        }
    }


    private fun save(){
        mPhotoEditor.saveAsBitmap(object: OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                if (saveBitmap != null) {
                    saveImageToInternalStorage(saveBitmap)
                }else{
                    Log.i("PhotoEditor","no image found")
                }
            }
            override fun onFailure(e: Exception?) {
                Toast.makeText(this@MainActivity,"${e?.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun imageCallBack(imageResult: ImageResult<Uri>) {
        when (imageResult) {
            is ImageResult.Success -> {
                val uri = imageResult.value
                setImageToEdit(uri)
            }
            is ImageResult.Failure -> {
                val error = imageResult.errorString
            }
        }
    }


    private fun setImageToEdit(uri: Uri) {
        clear(null)
        binding.photoEditorView.source.setImageURI(uri)
        mPhotoEditor.setBrushDrawingMode(true)
        binding.btnBrush.setColorFilter(R.color.purple_200)
        isEnableButtons(true)
    }


    private fun unSelectButtons() {
        binding.apply {
            btnBrush.setColorFilter(Color.WHITE)
            btnText.setColorFilter(Color.WHITE)
            btnEmoji.setColorFilter(Color.WHITE)
            btnEraser.setColorFilter(Color.WHITE)
            btnFilter.setColorFilter(Color.WHITE)
            btnSticker.setColorFilter(Color.WHITE)
        }
    }


    private fun isEnableButtons(b: Boolean) {
        binding.apply {
            btnBrush.isEnabled = b
            btnText.isEnabled = b
            btnEmoji.isEnabled = b
            btnEraser.isEnabled = b
            btnFilter.isEnabled = b
            btnSticker.isEnabled = b
        }
    }


    fun clear(v: View?) {
        mPhotoEditor.clearAllViews()
    }

    fun undo(v: View?) {
        mPhotoEditor.undo()
    }

    fun redo(v: View?){
        mPhotoEditor.redo()
    }


}