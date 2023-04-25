package org.uvigo.hamminggolay.core;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import org.uvigo.hamminggolay.R;

import java.io.Serializable;

/**
 * Clase para manejar la musica del juego.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class GameMusic extends MediaPlayer implements Serializable {

    private MediaPlayer music;
    private AudioManager am;
    private float volumen;
    private int progress;

    /**
     * Construye e inicializa un AudioManager y carga la musica del juego
     *
     * @param context contexto en el va a sonar la musica
     */
    public GameMusic(Context context) {
        am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        music = MediaPlayer.create(context, R.raw.music);
        progress = 0;
        volumen = 1;
    }

    /**
     * Comienza la musica
     */
    public void onStart(){
        music.start();
    }

    /**
     * Pausa la musica y guarda su progreso
     */
    public void onPause(){
        progress = music.getCurrentPosition();
        music.pause();
    }

    /**
     * Continua la musica o la comienza si no existe
     *
     * @param context contexto en el va a sonar la musica
     */
    public void onContinue(Context context) {
        if(music==null){
            music.start();
            music.seekTo(progress);
        }else{
            music = create(context, R.raw.music);
            music.start();
            music.seekTo(progress);
        }
        music.setVolume(volumen, volumen);
    }

    /**
     * Detiene la musica y libera los recursos usados por ella
     */
    public void onStop(){
        onPause();
        music.release();
    }

    /**
     * Devuelve el volumen de la musica
     *
     * @return el volumen de la musica
     */
    public float getVolumen() {
        return volumen;
    }

    /**
     * Cambia el volumen de la musica
     *
     * @param volumen el volumen al que se cambia
     */
    public void setVolume(float volumen) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (volumen * am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        this.volumen = volumen;
    }
}