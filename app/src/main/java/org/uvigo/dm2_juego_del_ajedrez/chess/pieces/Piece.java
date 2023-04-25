package org.uvigo.dm2_juego_del_ajedrez.chess.pieces;
import android.util.Log;

/**
 * Clase que representa y guarda la informacion de una pieza de ajedrez.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class Piece {
    private String name;
    private char color;
    private String image;
    int position;

    /**
     * Constuye e inicializa una pieza
     *
     * @param name      nombre de la pieza
     * @param color     color del objeto Piece
     * @param image     ruta de la imagen que representa graficamente la pieza
     * @param position  posicion de la pieza
     */
    public Piece(String name, char color, String image, int position){
        this.name=name;
        this.color=color;
        this.image=image;
        this.position=position;
        Log.w("POS",Integer.toString(position));
    }

    /**
     * Devuelve el nombre de la pieza
     *
     * @return el nombre de la pieza
     */
    public String getName(){
        return name;
    }

    /**
     * Devuelve el color de la pieza
     *
     * @return el color de la pieza
     */
    public char getColor(){
        return color;
    }

    /**
     * Devuelve la ruta de la imagen de la pieza
     *
     * @return la ruta de la imagen de la pieza
     */
    public String getImage(){
        return image;
    }

    /**
     * Obtiene la posicion de la pieza
     *
     * @return  la posicion de la pieza
     */
    public int getPos(){
        return position;
    }
}
