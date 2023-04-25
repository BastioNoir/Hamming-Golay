package org.uvigo.hamminggolay.ui;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.uvigo.hamminggolay.R;
import org.uvigo.hamminggolay.core.GameMusic;

/**
 * Actividad para mostrar la configuracion de la aplicacion.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class SettingsActivity extends AppCompatActivity {

    private ImageButton backButton;
    private SeekBar seekBarMusic;
    private GameMusic music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backButton = this.findViewById(R.id.backButton);
        seekBarMusic = this.findViewById(R.id.seekBarMusic);
        music = MainActivity.getMusic();
        seekBarMusic.setProgress((int) (music.getVolumen() * 100));

        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(music != null && fromUser){
                    Boolean b = music.isPlaying();
                    music.setVolume(((float) progress)/100);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.setResult( MainActivity.RESULT_CANCELED );
                SettingsActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Empezamos la musica
        music.onContinue(getApplicationContext());
    }

    @Override
    protected void onStart(){
        super.onStart();
        //Empezamos la musica
        music.onContinue(getApplicationContext());
    }
}