package org.uvigo.dm2_juego_del_ajedrez.ui;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.uvigo.dm2_juego_del_ajedrez.core.DBManager;
import org.uvigo.dm2_juego_del_ajedrez.core.GameMusic;
import org.uvigo.dm2_juego_del_ajedrez.R;
import org.uvigo.dm2_juego_del_ajedrez.core.Achievement;

import java.util.ArrayList;

/**
 * Actividad que muestra los logros.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class AchievementActivity extends AppCompatActivity {

    private DBManager dbManager;
    private SimpleCursorAdapter dbAdapter;

    private ArrayList<Achievement> achievements = new ArrayList<>();
    private ImageButton backButton;
    private GameMusic music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        this.dbManager = new DBManager( this.getApplicationContext() );

        ListView listView = this.findViewById( R.id.listViewAchievement );
        this.dbAdapter=new SimpleCursorAdapter(this, R.layout.achievement_listview, null, new String[] { DBManager.ACHIEVEMENT_NAME, DBManager.ACHIEVEMENTS_CLUE }, new int[] { R.id.textViewName, R.id.achievementMenuInfo }, 0);
        listView.setAdapter( this.dbAdapter );
        this.dbAdapter.changeCursor( this.dbManager.getAchievements() );

        registerForContextMenu(listView);

        music = MainActivity.getMusic();

        backButton = this.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AchievementActivity.this.setResult( MainActivity.RESULT_CANCELED );
                AchievementActivity.this.finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onPause();
        this.dbManager.close();
        this.dbAdapter.getCursor().close();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Empezamos la musica
        music.onContinue(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbManager.addAchievement(new Achievement("Voraz","Come una pieza"));
        dbManager.addAchievement(new Achievement("Un dia Oscuro","Come las dos torres"));
        dbManager.addAchievement(new Achievement("Francotirador en posicion","Coloca un alfil en una esquina del tablero"));
        dbManager.addAchievement(new Achievement("Al final si que era mortal","Come una reina"));
        dbManager.addAchievement(new Achievement("100 metros, vaya","Consigue transformar un peon en reina en una partida"));
        dbManager.addAchievement(new Achievement("Zona hostil","Lleva un peón a la ultima fila del tablero"));
        dbManager.addAchievement(new Achievement("Insaciable","Intenta comerte tu propia pieza"));

        ListView listView = this.findViewById( R.id.listViewAchievement );
        this.dbAdapter=new SimpleCursorAdapter(this, R.layout.achievement_listview, null, new String[] { DBManager.ACHIEVEMENT_NAME, DBManager.ACHIEVEMENTS_CLUE }, new int[] { R.id.textViewName, R.id.achievementMenuInfo }, 0);

        this.dbAdapter.changeCursor( this.dbManager.getAchievements() );
        registerForContextMenu(listView);

        //Empezamos la musica
        music.onContinue(getApplicationContext());

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewAchievement){
            getMenuInflater().inflate(R.menu.achievement_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position= ( (AdapterView.AdapterContextMenuInfo) item.getMenuInfo() ).position;
        Cursor cursor = this.dbAdapter.getCursor();

        if (item.getItemId()==R.id.achievementMenuInfo) {
            if(cursor.moveToPosition(position)){
                String name= cursor.getString(0);
                showAchievementClueDialog(name);
            }else{
                Toast.makeText(this, "Error al visualizar pista", Toast.LENGTH_SHORT).show();
            }
        }else{
            return super.onContextItemSelected(item);
        }
        return true;
    }

    /**
     * Muestra la pista del achievement
     *
     * @param name nombre del logro del que se va a mostrar la pista
     */
    private void showAchievementClueDialog(String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pista: ");
        TextView textView = new TextView(this);
        textView.setText(this.dbManager.getDescription(name));
        builder.setView(textView);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }
}