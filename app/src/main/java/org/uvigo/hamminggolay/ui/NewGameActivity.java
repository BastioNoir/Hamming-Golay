package org.uvigo.hamminggolay.ui;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.uvigo.hamminggolay.core.Profile;
import org.uvigo.hamminggolay.R;
import org.uvigo.hamminggolay.core.Uploader;
import org.uvigo.hamminggolay.core.History;

import java.util.ArrayList;

/**
 * Actividad para escoger las opciones para un nuevo juego de ajedrez.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class NewGameActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ProfileArrayAdapter profileArrayAdapter;

    private ArrayList<Profile> rivals = new ArrayList<>();
    private ArrayList<Profile> profiles = new ArrayList<>();

    private Profile selectedProfile= MainActivity.getSelectedProfile();
    private Profile selectedRival= null;

    private ArrayList<String> times;
    private ArrayAdapter<String> timesAdapter;
    private int selectedTime= 0;

    private ImageButton backButton;

    boolean normalMode; //TRUE NORMAL; FALSE RANDOM
    boolean turn; //FALSE J1 juega con negras; TRUE J2 juega con negras
    boolean continueGame;

    History history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        continueGame=(boolean)getIntent().getSerializableExtra("type");
        history= (History)getIntent().getSerializableExtra("history");

        rivals= loadRivals();

        ListView listView = findViewById(R.id.newGameOponentList);
        profileArrayAdapter = new ProfileArrayAdapter(this, rivals);
        listView.setAdapter(profileArrayAdapter);

        ListView timeListView = findViewById(R.id.newGameTimeList);
        this.times = new ArrayList<String>();
        this.timesAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_selectable_list_item,
                this.times );
        timesAdapter.addAll("1:00", "5:00", "10:00", "25:00", "60:00", "90:00");
        timeListView.setAdapter( this.timesAdapter );

        registerForContextMenu(listView);

        registerForContextMenu(timeListView);

        ImageButton white= (ImageButton)findViewById(R.id.newGameWhiteButton);
        ImageButton black= (ImageButton)findViewById(R.id.newGameBlackButton);

        Button normalModeButton= (Button)findViewById(R.id.NormalButton);

        Button startGame= (Button)findViewById(R.id.startButton);

        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),selectedProfile.getName()+" jugara blancas",Toast.LENGTH_SHORT).show();
                turn=true;
            }
        });

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),selectedProfile.getName()+" jugara negras",Toast.LENGTH_SHORT).show();
                turn=false;
            }
        });

        normalModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Modo normal seleccionado",Toast.LENGTH_SHORT).show();
                normalMode=true;
            }
        });

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subActividad = new Intent( NewGameActivity.this, GameActivity.class );

                subActividad.putExtra("mode",normalMode); //Enviamos el modo de juego
                subActividad.putExtra("rival",selectedRival); //Enviamos al rival
                subActividad.putExtra("turn",turn); //Enviamos el turno al juego
                subActividad.putExtra("time",selectedTime); //Enviamos el tiempo al juego

                if(continueGame){
                    subActividad.putExtra("type",true);
                }else{
                    subActividad.putExtra("type",false);
                    subActividad.putExtra("history",history);
                }

                activityResultLauncher.launch(subActividad);
            }
        });

        ActivityResultContract<Intent, ActivityResult> contract = new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback = new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
            }
        };

        this.activityResultLauncher = this.registerForActivityResult(contract, callback);

        backButton = this.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewGameActivity.this.setResult( MainActivity.RESULT_CANCELED );
                NewGameActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Guardamos los perfiles actualizados en el uploader
        profiles= Uploader.loadProfiles(getApplicationContext());
        rivals=loadRivals();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Guardamos los perfiles actualizados en el uploader
        profiles=Uploader.loadProfiles(getApplicationContext());
        rivals=loadRivals();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.newGameOponentList){
            getMenuInflater().inflate(R.menu.rivals_menu, menu);
        }
        if(v.getId() == R.id.newGameTimeList){
            getMenuInflater().inflate(R.menu.time_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position;
        if (item.getItemId()==R.id.chooseRival) {
            position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
            selectedRival=rivals.get(position);
            Toast.makeText(this, "Tu rival es "+rivals.get(position).getName(), Toast.LENGTH_SHORT).show();
        }else{
            if(item.getItemId()==R.id.chooseTime){
                position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                selectedTime=Integer.parseInt(times.get(position).split(":")[0]);
                Toast.makeText(this, "La partida durará "+times.get(position), Toast.LENGTH_SHORT).show();
            }else{
                return super.onContextItemSelected(item);
            }
        }
        return true;
    }

    /**
     * Carga los rivales disponibles desde la Base de Datos
     *
     * @return lista con los rivales disponibles
     */
    public ArrayList<Profile> loadRivals(){
        profiles=Uploader.loadProfiles(getApplicationContext());
        rivals=profiles;
        Profile tempSP=null;

        for(Profile pr: profiles){
            if(selectedProfile.getName().equals(pr.getName())){
                tempSP=pr;
            }
        }

        if(tempSP!=null){
            rivals.remove(tempSP);
        }

        return rivals;
    }
}