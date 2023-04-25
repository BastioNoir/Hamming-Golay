package org.uvigo.dm2_juego_del_ajedrez.core;
import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Clase que almacena la informacion relacionada a un perfil.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class Profile implements Serializable {

    private String name;
    private String image;

    private String skinBoardName, skinPieceName;

    private int points;

    private ArrayList<String> achievementsList;
    private ArrayList<String> friendsList;

    /**
     * Construye e inicializa un perfil vacio
     */
    public Profile(){
        this.name="";
        this.image="cath_image.png";
        
        this.points=0;

        this.skinBoardName="image000000#ffffff";
        this.skinPieceName="2";

        this.achievementsList= new ArrayList<String>();
        this.friendsList= new ArrayList<String>();
    }

    /**
     * Construye e inicializa un perfil
     *
     * @param name              nombre del perfil
     * @param image             ruta de la imagen del perfil
     * @param skinBoardName     nombre de la skin del tablero asociada
     * @param skinPieceName     nombre de la skin de las piezas asociada
     * @param points            puntos del perfil
     * @param achievementsList  lista de logros serializada
     * @param friendsList       lista de amigos serializada
     */
    public Profile(String name, String image, String skinBoardName, String skinPieceName, int points, String achievementsList, String friendsList){
        this.name=name;
        this.image= image;
        this.points=points;

        this.skinBoardName= skinBoardName;
        this.skinPieceName= skinPieceName;

        //Rellena perfil desde archivo
        ArrayList<String> achievementsElement= new ArrayList<String>(Arrays.asList(achievementsList.replace("[","").replace("]","").split(", ")));
        ArrayList<String> friendsElement= new ArrayList<String>(Arrays.asList(friendsList.replace("[","").replace("]","").split(", ")));

        this.achievementsList= new ArrayList<>();
        this.friendsList= new ArrayList<>();

        if(!achievementsList.equals("[]")){
            for(String achievement: achievementsElement){
                this.achievementsList.add(achievement); //Linea con el achievement
            }
        }

        if(!friendsList.equals("[]")){
            for(String friend: friendsElement){
                this.friendsList.add(friend); //Linea con el friend
            }
        }

    }

    /**
     * Construye e inicializa un perfil con valores por defecto
     *
     * @param name nombre del perfil
     */
    public Profile(String name){
        this.name=name;

        this.image="cath_image.png";
        this.skinBoardName="image000000#ffffff";
        this.skinPieceName="1";

        this.points=0;

        this.achievementsList= new ArrayList<String>();
        this.friendsList= new ArrayList<String>();
    }

    /**
     * Devuelve el nombre de un perfil
     *
     * @return nombre del perfil
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve los puntos de un perfil
     *
     * @return los puntos del perfil
     */
    public String getPoints(){ return String.valueOf(points); }

    /**
     * Devuelve la ruta de la imagen del perfil
     *
     * @return la ruta de la imagen
     */
    public String getImagePath(){
        return image;
    }

    /**
     * Obtiene los logros obtenidos por el perfil
     *
     * @return lista con los logros del perfil
     */
    public ArrayList<String> getAchievements(){
        return achievementsList;
    }

    /**
     * Obtiene los amigos por perfil
     *
     * @return lista con los amigos del perfil
     */
    public ArrayList<String> getFriends(){
        return friendsList;
    }

    /**
     * Devuelve el nombre del tablero seleccionado para este jugador
     *
     * @return el nombre del tablero seleccionado
     */
    public String getSkinBoardName(){
        return skinBoardName;
    }

    /**
     * Devuelve el nombre de las piezas seleccionadas por este jugador
     *
     * @return el nombre de las piezas seleccionadas
     */
    public String getSkinPieceName(){
        return skinPieceName;
    }

    /**
     * Añade sus puntos después de cada partida
     *
     * @param points puntos ganados en esta partida
     */
    public void setPoints(int points){
        this.points+=points;
    }

    /**
     * Modifica el nombre
     *
     * @param name nuevo nombre del perfil
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Modifica la imagen
     *
     * @param image ruta de la nueva imagen de perfil
     */
    public void setImage(String image){ this.image=image; }

    /**
     * Modifica el tablero
     *
     * @param skinName nombre de la nueva skin seleccionada
     */
    public void setSkinBoardName(String skinName){
        this.skinBoardName=skinName;
    }

    /**
     * Modifica las piezas
     * @param skinName nombre de la nueva skin seleccionada
     */
    public void setSkinPieceName(String skinName){
        this.skinPieceName=skinName;
    }

    /**
     * Añade un amigo
     *
     * @param context   contexto actual
     * @param friend    nombre del amigo a añadir
     */
    public void addFriend(Context context, Profile friend){
        if(!friendsList.contains(friend.getName())){
            friendsList.add(friend.getName());
        }else{
            Toast.makeText(context, friend.getName()+" ya es tu amigo", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Elimina un amigo
     *
     * @param friend nombre del amigo a eliminar
     */
    public void removeFriend(Profile friend){
        friendsList.remove(friend.getName());
    }

    /**
     * Añade un nuevo logro
     *
     * @param achievement nombre del logro a añadir
     */
    public void addAchievement(Context context, Achievement achievement){
        if(!achievementsList.contains(achievement.getName())){
            achievementsList.add(achievement.getName());
            Toast.makeText(context, "El logro "+achievement.getName()+" ha sido conseguido", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "El logro "+achievement.getName()+" ya ha sido conseguido", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public String toString() {
        return name+";"+image+";"+skinBoardName+";"+skinPieceName+";"+points+";"+achievementsList.toString()+";"+friendsList.toString();
    }
}
