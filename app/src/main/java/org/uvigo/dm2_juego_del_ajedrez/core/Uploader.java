package org.uvigo.dm2_juego_del_ajedrez.core;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.dm2_juego_del_ajedrez.ui.MainActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Clase de utilidad que permite el guardar perfiles, cambiar las imagenes, actualiza los
 * historiales de partidas y guarda/carga el perfil seleccionado.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class Uploader extends AppCompatActivity {

    private static DBManager dbManager;
    private static Profile selectedProfile;
    private static ArrayList<Profile> profiles= new ArrayList<Profile>();

    /**
     * Convierte una skin en los assets
     *
     * @param context   contexto actual
     * @param imagePath ruta de la imagen
     * @return          la imagen transformada en un asset
     */
    public static Bitmap bitmapFromAssets(Context context, String imagePath)
    {
        InputStream stream = null;
        try{
            stream = context.getAssets().open(imagePath);
            return BitmapFactory.decodeStream(stream);
        }catch (Exception e) {}
        finally{
            try{
                if(stream != null){
                    stream.close();
                }
            }catch (Exception e) {}
        }
        return null;
    }

    /**
     * Actualiza los historiales
     *
     * @param context   contexto actual
     * @param h         historial a actualizar
     */
    public static void updateHistory(Context context, History h){
        dbManager = new DBManager( context );
        Log.w("ACTUALIZA HISTORY","");
        dbManager.deleteHistory(h.getName());
        dbManager.addHistory(h);
    }

    /**
     * Cambia el perfil global
     * @param profile perfil que guardar
     */
    public static void changeGlobalSelectedProfile(Context context,Profile profile){
        try{
            MainActivity.setSelectedProfile(context,profile);
        }catch(NullPointerException e){
            Toast.makeText(context, "No se ha seleccionado ningun perfil, por favor selecciona uno", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Guarda el perfil global entre arranques de la aplicacion
     *
     * @param context contexto actual
     */
    public static void saveGlobalProfile(Context context){
        selectedProfile= MainActivity.getSelectedProfile();

        try (FileOutputStream f = context.openFileOutput( "global_data.cfg", Context.MODE_PRIVATE ) )
        {
            PrintStream cfg = new PrintStream( f );
            cfg.println( selectedProfile.getName() ); //PROFILE NAME
            cfg.println( selectedProfile.getImagePath()); //PROFILE IMAGE
            cfg.println( selectedProfile.getSkinBoardName()); //PROFILE BOARD
            cfg.println( selectedProfile.getSkinPieceName()); //PROFILE PIECE
            cfg.println( selectedProfile.getPoints()); //PROFILE POINTS
            cfg.println( selectedProfile.getAchievements().toString()); //PROFILE ACHIEVEMENTS
            cfg.println( selectedProfile.getFriends().toString()); //PROFILE FRIENDS

            cfg.close();
        }
        catch(IOException exc) {
        }
    }

    /**
     * Carga el perfil al cargar la aplicacion
     *
     * @param context contexto actual
     */
    public static void loadGlobalProfile(Context context){

        try (FileInputStream f = context.openFileInput("global_data.cfg")){
            BufferedReader cfg = new BufferedReader( new InputStreamReader( f ) );

            String profileLine = cfg.readLine(); //Corresponde al nombre del perfil

            String cfg_image, cfg_board, cfg_piece, cfg_point, cfg_achievements, cfg_friends;
            while( profileLine != null ) {

                //Recuperamos cada perfil
                cfg_image= cfg.readLine();

                cfg_board= cfg.readLine();
                cfg_piece= cfg.readLine();

                cfg_point= cfg.readLine();

                cfg_achievements= cfg.readLine();
                cfg_friends= cfg.readLine();

                selectedProfile= new Profile(profileLine,cfg_image, cfg_board, cfg_piece, Integer.parseInt(cfg_point), cfg_achievements, cfg_friends);

                MainActivity.setSelectedProfile(context, selectedProfile);

                profileLine = cfg.readLine();
            }

            cfg.close();

            if(selectedProfile.getName().equals("")){
                MainActivity.setSelectedProfile(context, new Profile("default"));
                Toast.makeText(context, "Seleccionado perfil default", Toast.LENGTH_SHORT).show();
            }

        }
        catch (IOException exc)
        {
        }
    }

    /**
     * Guarda los perfiles dados como una lista
     *
     * @param context           contexto actual
     * @param profilesToSave    lista de perfiles a guardar
     */
    public static void saveProfiles(Context context, ArrayList<Profile> profilesToSave){

        //Si no hay skins en el momento de guardar es la primera ejecucion y tenemos que generar todas las skins
        if(profilesToSave.isEmpty()){
            Profile defaultProfile= new Profile("default");
            profilesToSave.add(defaultProfile);
            selectedProfile= defaultProfile;
        }

        try (FileOutputStream f = context.openFileOutput( "profile_data.cfg", Context.MODE_PRIVATE ) )
        {
            PrintStream cfg = new PrintStream( f );

            for(Profile profile: profilesToSave) {
                cfg.println( profile.getName() ); //PROFILE NAME
                cfg.println( profile.getImagePath()); //PROFILE IMAGE
                cfg.println( profile.getSkinBoardName()); //PROFILE BOARD
                cfg.println( profile.getSkinPieceName()); //PROFILE PIECE
                cfg.println( profile.getPoints()); //PROFILE POINTS
                cfg.println( profile.getAchievements().toString()); //PROFILE ACHIEVEMENTS
                cfg.println( profile.getFriends().toString()); //PROFILE FRIENDS
            }

            cfg.close();
        }
        catch(IOException exc) {
        }
    }

    /**
     * Devuelve una arraylist con los perfiles permanentes
     *
     * @param context   contexto actual
     * @return          una arraylist con los perfiles permanentes
     */
    public static ArrayList<Profile> loadProfiles(Context context){
        profiles.clear();
        try (FileInputStream f = context.openFileInput("profile_data.cfg")){
            BufferedReader cfg = new BufferedReader( new InputStreamReader( f ) );

            String profileLine = cfg.readLine(); //Corresponde al nombre del perfil

            String cfg_image, cfg_board, cfg_piece, cfg_point, cfg_achievements, cfg_friends;
            while( profileLine != null ) {

                //Recuperamos cada perfil
                cfg_image= cfg.readLine();

                cfg_board= cfg.readLine();
                cfg_piece= cfg.readLine();

                cfg_point= cfg.readLine();

                cfg_achievements= cfg.readLine();
                cfg_friends= cfg.readLine();

                profiles.add(new Profile(profileLine,cfg_image, cfg_board, cfg_piece, Integer.parseInt(cfg_point), cfg_achievements, cfg_friends));

                profileLine = cfg.readLine();
            }

            cfg.close();
        }
        catch (IOException exc)
        {
        }

        return profiles;
    }
}
