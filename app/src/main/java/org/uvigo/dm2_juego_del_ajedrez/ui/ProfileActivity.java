package org.uvigo.dm2_juego_del_ajedrez.ui;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.uvigo.dm2_juego_del_ajedrez.R;
import org.uvigo.dm2_juego_del_ajedrez.core.GameMusic;
import org.uvigo.dm2_juego_del_ajedrez.core.Profile;
import org.uvigo.dm2_juego_del_ajedrez.core.Uploader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Actividad que muestra la lista de perfiles y permite añadir, modificar y eliminar perfiles.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class ProfileActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private ArrayList<Profile> profiles = new ArrayList<>();

    private ProfileArrayAdapter profileArrayAdapter;

    public Profile selectedProfile;

    private ImageButton backButton;
    private ListView listView;
    private GameMusic music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profiles.add(new Profile("default"));

        listView = findViewById(R.id.listViewProfile);
        profiles= Uploader.loadProfiles(getApplicationContext());
        profileArrayAdapter = new ProfileArrayAdapter(this, profiles);

        //El perfil seleccionado sera por defecto el default, sino cambiar
        try{
            selectedProfile= MainActivity.getSelectedProfile();
            Toast.makeText(this, "El perfil seleccionado es "+selectedProfile.getName(), Toast.LENGTH_SHORT).show();
        }catch(NullPointerException e){
            selectedProfile=new Profile("default");
            profileArrayAdapter.notifyDataSetChanged();
            Toast.makeText(this, "El perfil seleccionado es default", Toast.LENGTH_SHORT).show();
        }

        listView.setAdapter(profileArrayAdapter);

        registerForContextMenu(listView);

        //Si se le da al boton de añadir perfil
        findViewById(R.id.buttonAddProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProfile();
            }
        });

        ActivityResultContract<Intent, ActivityResult> contract = new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback = new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //cargarInterfaz();

            }
        };

        this.activityResultLauncher = this.registerForActivityResult(contract, callback);

        music = MainActivity.getMusic();

        backButton = this.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uploader.changeGlobalSelectedProfile(getApplicationContext(),selectedProfile);
                ProfileActivity.this.setResult( MainActivity.RESULT_CANCELED );
                ProfileActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        profiles= Uploader.loadProfiles(getApplicationContext());

        listView = findViewById(R.id.listViewProfile);
        profileArrayAdapter = new ProfileArrayAdapter(this, profiles);
        listView.setAdapter(profileArrayAdapter);

        //Empezamos la musica
        music.onContinue(getApplicationContext());

        registerForContextMenu(listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        profiles= Uploader.loadProfiles(getApplicationContext());
        //Empezamos la musica
        music.onContinue(getApplicationContext());

        profileArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Guardamos en el uploader
        updateProfiles();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Guardamos en el uploader
        updateProfiles();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewProfile){
            getMenuInflater().inflate(R.menu.profile_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position= ( (AdapterView.AdapterContextMenuInfo) item.getMenuInfo() ).position;

        switch (item.getItemId()){
            case(R.id.profileMenuInfo):
                position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;

                Profile infoProfile= getProfileByName(profiles.get(position).getName());
                Intent subActividad = new Intent( ProfileActivity.this, VisualizeProfileActivity.class );
                subActividad.putExtra( "visualizeprofile", infoProfile);
                activityResultLauncher.launch(subActividad);
                break;
            case(R.id.profileMenuAddFriend):
                position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                Profile addProfile= getProfileByName(profiles.get(position).getName());
                selectedProfile.addFriend(getApplicationContext(),addProfile);
                Toast.makeText( this, "Has añadido a "+addProfile.getName()+" como amigo", Toast.LENGTH_SHORT ).show();
                profileArrayAdapter.notifyDataSetChanged();

                //Guardamos en el uploader
                updateProfiles();
                break;
            case(R.id.profileMenuRemoveFriend):
                position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                Profile removedProfile= getProfileByName(profiles.get(position).getName());
                if(selectedProfile.getFriends().contains(removedProfile.getName())){
                    selectedProfile.removeFriend(removedProfile);
                    Toast.makeText( this, removedProfile.getName()+" ya no es tu amigo", Toast.LENGTH_SHORT ).show();
                }else{
                    Toast.makeText( this, removedProfile.getName()+" no es tu amigo", Toast.LENGTH_SHORT ).show();
                }
                profileArrayAdapter.notifyDataSetChanged();
                //Guardamos en el uploader
                updateProfiles();
                break;
            case(R.id.profileMenuUse):
                position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                //CAMBIA EL PERFIL SELECCIONADO

                selectedProfile=getProfileByName(profiles.get(position).getName());

                Toast.makeText( this, "Perfil seleccionado: "+selectedProfile.getName(), Toast.LENGTH_SHORT ).show();
                Uploader.changeGlobalSelectedProfile(getApplicationContext(),selectedProfile);
                break;
            case(R.id.profileMenuEdit):
                position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                doEdit(position);
                break;

            case(R.id.profileMenuDelete):
                position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                profiles.remove(position);
                Toast.makeText( this, "Perfil eliminado correctamente", Toast.LENGTH_SHORT ).show();
                profileArrayAdapter.notifyDataSetChanged();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    /**
     * Añade un nuevo perfil
     */
    private void addProfile() {
        profiles.add(new Profile());
        showEditNameDialog(profiles.size()-1);
        //Guardamos en el uploader
        updateProfiles();
    }

    /**
     * Añade al nuevo perfil un nombre, o modifica un nombre
     *
     * @param position posicion del perfil en la lista de perfiles
     */
    private void showEditNameDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Profile Name");
        EditText editText = new EditText(this);
        editText.setText(profiles.get(position).getName());
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String profileName = editText.getText().toString();
                profiles.get(position).setName(profileName);
                profileArrayAdapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    /**
     * Modifica la foto de perfil
     *
     * @param position posicion del perfil en la lista de perfiles
     */
    private void showEditPhotoDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Profile Photo Path");
        EditText editText = new EditText(this);
        editText.setText(profiles.get(position).getImagePath());
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String imageName = editText.getText().toString();
                profiles.get(position).setImage(imageName);
                profileArrayAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    /**
     * Permite editar el nombre y la imagen de un perfil
     *
     * @param position  posicion del perfil en la lista de perfiles
     */
    private void doEdit(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to modify the profile photo?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //MODIFICA LA RUTA DE LA FOTO
                showEditPhotoDialog(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();

        builder.setTitle("Do you want to modify the profile name?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //MODIFICA EL NOMBRE
                showEditNameDialog(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    /**
     * Devuelve un perfil con el nombre especificado
     *
     * @param name  nombre del perfil a devolver
     * @return      un perfil con el nombre especificado
     */
    public Profile getProfileByName(String name){
        Profile pr;
        int i=0;
        int toret=-1;

        while(i<=profiles.size()-1){
            pr= profiles.get(i);
            //Encuentra el perfil
            if(name.equals(pr.getName())){
                toret=i;
            }
            i++;
        }

        return profiles.get(toret);
    }

    /**
     * Actualiza los perfiles generales
     */
    public void updateProfiles(){

        ArrayList<Profile> tempProfiles= profiles;
        Profile tempSP=null;

        for(Profile pr: profiles){
            if(selectedProfile.getName().equals(pr.getName())){
                //Quitamos el selected profile
                tempSP=pr;
            }
        }

        if(tempSP!=null){
            tempProfiles.remove(tempSP);
            tempProfiles.add(selectedProfile);
        }

        Uploader.saveProfiles(getApplicationContext(),tempProfiles);
    }
}