package com.accident.emergency

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
//import sun.jvm.hotspot.utilities.IntArray






class MainActivity : AppCompatActivity() {

    lateinit var output: String

    //private var mediaRecorder: MediaRecorder? = null
    private lateinit var mediaRecorder: MediaRecorder
    private var state: Boolean = false
    private var recordingStopped: Boolean = false
    lateinit var uri : Uri
    private val TAG = "EMERGENCY"
    private val RECORD_REQUEST_CODE = 101
    var permissionGranted = false
    var storageRef = FirebaseStorage.getInstance().reference
    lateinit var mediaFile:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // output = Environment.getExternalStorageDirectory().absolutePath + "/recording.mp3"
      //  var outputFile:File = File.createTempFile("stuff","more")
       // output = Environment.DIRECTORY_DCIM
//        output  = File(this.filesDir, "recording.mp3").toString()
        val sep = File.separator // Use this instead of hardcoding the "/"
        val newFolder = "Music"
        val extStorageDirectory = Environment.getExternalStorageState()
        val myNewFolder = File(extStorageDirectory + sep + newFolder)
        myNewFolder.mkdir()
        mediaFile = (Environment.getExternalStorageDirectory().toString()
                + sep + newFolder + sep + "myRecordings.mp3")
        mediaRecorder = MediaRecorder()



        button_start_recording.setOnClickListener { startRecording() }

        button_stop_recording.setOnClickListener { stopRecording() }

        button_pause_recording.setOnClickListener { pauseRecording() }


    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()

        }
    }

    private fun setupfilePermission(){
        val permission =  ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "you dont have file permition", Toast.LENGTH_SHORT).show()
            makeFileRequest()
        }else{
            Toast.makeText(this, "you have file permition but something else is wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private  fun makeFileRequest(){
        ActivityCompat.requestPermissions(this,
        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE)

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    permissionGranted = true
                }
            }
        }
    }

    private fun startRecording() {

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setOutputFile(mediaFile)

            state = true
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
//        start()
    }

    private fun stopRecording(){
        if(state){
            mediaRecorder.stop()
            mediaRecorder.release()
            state = false
        }else{
            Toast.makeText(this, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }



    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if(state) {
            if(!recordingStopped){
                Toast.makeText(this,"Stopped!", Toast.LENGTH_SHORT).show()
                mediaRecorder.pause()
                recordingStopped = true
                button_pause_recording.text = "Resume"
            }else{
                //resumeRecording()
            }
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        Toast.makeText(this,"Resume!", Toast.LENGTH_SHORT).show()
        mediaRecorder.resume()
        button_pause_recording.text = "Pause"
        recordingStopped = false
    }

    override fun onStart() {
        super.onStart()
        setupPermissions()
        setupfilePermission()
    }
}
