package com.accident.emergency

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.accident.emergency.databinding.ActivityMainBinding
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var output: String
    private var state: Boolean = false
    private var recordingPaused: Boolean = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        playButton = binding.startBtn
        pauseButton = binding.pauseBtn
        stopButton = binding.stopBtn
        mediaRecorder = MediaRecorder()
        output = Environment.getExternalStorageDirectory().absolutePath + "/recording.mp3"

        setUpMediaRecorder()



        //Disable Pause and Stop Button
        pauseButton.isEnabled = false
        stopButton.isEnabled = false

        playButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions,0)
            } else {
                startRecording()
            }
        }

        pauseButton.setOnClickListener { pauseRecording() }
        stopButton.setOnClickListener { stopRecording() }

    }

    private fun setUpMediaRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setOutputFile(output)
    }

    private fun startRecording() {
        binding.pauseBtn.isEnabled = true
        binding.stopBtn.isEnabled = true
        state = true
        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun pauseRecording(){
        if (state){
            if (!recordingPaused){
                mediaRecorder.pause()
                Toast.makeText(this,"Recording Paused!",Toast.LENGTH_SHORT).show()
                binding.pauseBtn.text = getString(R.string.resume_text)
                recordingPaused = true

            }else{
                resumeRecording()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        Toast.makeText(this,"Resumed Recording!",Toast.LENGTH_SHORT).show()
        mediaRecorder.resume()
        binding.pauseBtn.text = getString(R.string.pause_text)
        recordingPaused = false
    }


    private fun stopRecording(){
        if (state){
            Toast.makeText(this,"Recording Stopped!",Toast.LENGTH_SHORT).show()
            mediaRecorder.stop()
            mediaRecorder.release()
            state = false
        }
    }


}





