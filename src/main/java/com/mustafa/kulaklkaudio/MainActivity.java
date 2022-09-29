package com.mustafa.kulaklkaudio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    BluetoothDevice btDevice;
    AudioManager audioManager;
    Button btButton;
    SeekBar seekBar;
    BassBoost bassBoost;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println(btAdapter.getBondedDevices());

        btDevice = btAdapter.getRemoteDevice("41:42:48:BB:08:D8");
        System.out.println(btDevice.getName());

        btButton = findViewById(R.id.btButton);
        seekBar = findViewById(R.id.seekBar);

        if(btAdapter == null){
            Toast.makeText(this, "Bağlı Aygıt Yok!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Bağlı Cihaz: "+btDevice.getName(), Toast.LENGTH_LONG).show();
        }

        if(btAdapter.isEnabled()){
            btButton.setText("Bluetooth Kapat");
        }else if(!btAdapter.isEnabled()){
            btButton.setText("Bluetooth Aç");
        }

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            seekBar.setProgress(audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC));
        }


    }


    public void upVoice(View view){

        if(btAdapter.isEnabled()){
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE,AudioManager.FLAG_PLAY_SOUND);
            seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }

    }

    public void downVoice(View view){

        if(btAdapter.isEnabled()){
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        }
    }

    @SuppressLint("MissingPermission")
    public void openBt(View view){
        if(!btAdapter.isEnabled()){
            Intent btOpen = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(btOpen);
        }else if(btAdapter.isEnabled()){
            btAdapter.disable();
            Toast.makeText(this, "Bluetooth Kapandı.", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Bağlı cihaz yok!", Toast.LENGTH_SHORT).show();
            btButton.setText("Bluetooth Aç");
        }
    }

    public void bassClicked(View view){
        bassBoost = new BassBoost(0, 0);
        bassBoost.setEnabled(true);
        BassBoost.Settings bassBoostSettingTemp =  bassBoost.getProperties();
        BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
        bassBoostSetting.strength = 1000;
        bassBoost.setProperties(bassBoostSetting);

    }

    public void bassCloseClicked(View view){
        if(bassBoost.getEnabled()){
            bassBoost.setEnabled(false);
        }
    }

    public boolean stopMusic(View view){
        int result = audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                                                        @Override
                                                        public void onAudioFocusChange(int focusChange) {
                                                        }
                                                    },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("AudioFocus", "Audio focus received");
            return true;
        } else {
            Log.d("AudioFocus", "Audio focus NOT received");
            return false;
        }
    }

    public void resumeMusic(View view){
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        audioManager.dispatchMediaKeyEvent(event);
    }


}