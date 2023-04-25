package org.uvigo.dm2_juego_del_ajedrez.ui;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import org.uvigo.dm2_juego_del_ajedrez.R;
import org.uvigo.dm2_juego_del_ajedrez.core.GameMusic;
import org.uvigo.dm2_juego_del_ajedrez.core.Skin;
import org.uvigo.dm2_juego_del_ajedrez.core.Uploader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Actividad que muestra una lista de las skins disponibles para los tableros y las piezas.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class SkinsActivity extends AppCompatActivity {

    private ArrayList<Skin> skins = new ArrayList<Skin>();
    private SkinArrayAdapter skinArrayAdapter;
    private ImageButton backButton;
    private GameMusic music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skins);

        music = MainActivity.getMusic();

        saveSkins();

        boolean mode=(boolean)getIntent().getSerializableExtra("mode");

        ListView listView = findViewById(R.id.listViewSkin);

        backButton = this.findViewById(R.id.backButton);


        loadSkins();

        ArrayList<Skin> selectedSkins= new ArrayList<>();

        //MODO BOARD
        if(mode){
            for(Skin skin: skins){
                if(skin.getImagePath().contains("#")){
                    selectedSkins.add(skin);
                }
            }
        //MODO PIECE
        }else{
            for(Skin skin: skins){
                if(!skin.getImagePath().contains("#")){
                    selectedSkins.add(skin);
                }
            }
        }

        skinArrayAdapter = new SkinArrayAdapter(this, selectedSkins, mode);
        listView.setAdapter(skinArrayAdapter);

        skinArrayAdapter.notifyDataSetChanged();

        registerForContextMenu(listView);

        backButton = this.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinsActivity.this.setResult( MainActivity.RESULT_CANCELED );
                SkinsActivity.this.finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!skins.isEmpty()){
            saveSkins();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Empezamos la musica
        music.onContinue(getApplicationContext());
        if (skins.isEmpty()) {
            loadSkins();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Empezamos la musica
        music.onContinue(getApplicationContext());
        if (skins.isEmpty()) {
            loadSkins();
        }
    }

    /**
     * Guarda las skins o si no hay ninguna guardada las crea
     */
    private void saveSkins(){
        //Si no hay skins en el momento de guardar es la primera ejecucion y tenemos que generar todas las skins
        if(skins.isEmpty()){
            skins.add(new Skin("BOARDSKIN0","imageffffff#000000"));
            skins.add(new Skin("BOARDSKIN1","image000000#ffffff"));

            skins.add(new Skin("BOARDSKIN2","imageff4f7bf6#000000"));
            skins.add(new Skin("BOARDSKIN3","imageffc70039#000000"));
            skins.add(new Skin("BOARDSKIN4","imageff5aedf7#000000"));
            skins.add(new Skin("BOARDSKIN5","imageff844930#000000"));
            skins.add(new Skin("BOARDSKIN6","imagefff758f7#000000"));
            skins.add(new Skin("BOARDSKIN7","imageffdaf7a6#000000"));

            skins.add(new Skin("BOARDSKIN8" ,"imageffffff#ff4f7bf6"));
            skins.add(new Skin("BOARDSKIN9" ,"imageffffff#ffc70039"));
            skins.add(new Skin("BOARDSKIN10","imageffffff#ff5aedf7"));
            skins.add(new Skin("BOARDSKIN11","imageffffff#ff844930"));
            skins.add(new Skin("BOARDSKIN12","imageffffff#fff758f7"));
            skins.add(new Skin("BOARDSKIN13","imageffffff#ffdaf7a6"));

            skins.add(new Skin("BOARDSKIN14" ,"imageffc70039#ff4f7bf6"));
            skins.add(new Skin("BOARDSKIN15","imageff844930#ff5aedf7"));
            skins.add(new Skin("BOARDSKIN16","imageffdaf7a6#fff758f7"));

            //PIECE SKINS
            skins.add(new Skin("PIECESKIN1","1"));
            skins.add(new Skin("PIECESKIN2","2"));
            skins.add(new Skin("PIECESKIN3","3"));
            skins.add(new Skin("PIECESKIN4","4"));
            skins.add(new Skin("PIECESKIN5","5"));
        }

        try (FileOutputStream f = this.openFileOutput( "skins_data.cfg", Context.MODE_PRIVATE ) )
        {
            PrintStream cfg = new PrintStream( f );

            for(Skin skin: this.skins) {
                cfg.println( skin.getName() ); //SKIN NAME
                cfg.println( skin.getImagePath()); //SKIN IMAGE
            }

            cfg.close();
        }
        catch(IOException exc) {
        }
    }

    /**
     * Carga las skins guardadas
     */
    private void loadSkins(){
        skins.clear();
        try (FileInputStream f = this.openFileInput("skins_data.cfg")){
            BufferedReader cfg = new BufferedReader( new InputStreamReader( f ) );

            String skinLine = cfg.readLine(); //Corresponde al nombre de la skin

            String cfg_image;
            while( skinLine != null ) {

                //Recuperamos cada skin
                cfg_image= cfg.readLine();

                this.skins.add(new Skin(skinLine,cfg_image));

                skinLine = cfg.readLine();
            }

            cfg.close();
        }
        catch (IOException exc)
        {
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewSkin){
            getMenuInflater().inflate(R.menu.skin_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position;
        if (item.getItemId()==R.id.skinInfo) {
            position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
            showSkinImageDialog(position);
        }else{
            return super.onContextItemSelected(item);
        }
        return true;
    }

    /**
     * Muestra una preview de como se veria la skin
     *
     * @param position  posicion de la skin en la lista
     */
    private void showSkinImageDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Skin Image: ");
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(Uploader.bitmapFromAssets(getApplicationContext(),skins.get(position).getImagePath()));

        builder.setView(imageView);
        builder.setPositiveButton("CLOSE", null);

        builder.create().show();
    }
}