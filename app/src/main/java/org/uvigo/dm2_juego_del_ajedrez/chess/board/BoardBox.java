package org.uvigo.dm2_juego_del_ajedrez.chess.board;
import android.util.Log;

import org.uvigo.dm2_juego_del_ajedrez.chess.pieces.Piece;

/**
 * Clase que guarda la informacion de una casilla de un tablero.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class BoardBox {

    public int drawableBackground;
    public String drawablePieza;
    private Piece piece;

    /**
     * Construye e inicializa una casilla del tablero de ajedrez
     *
     * @param drawableBackground    color del fondo de la casilla
     */
    public BoardBox(int drawableBackground) {
        this.drawableBackground = drawableBackground;
        this.drawablePieza = "";
        this.piece= new Piece("EMPTY",'E',"",64);
    }

    /**
     * Devuelve el color del fondo de la casilla
     *
     * @return el color del fondo de la casilla
     */
    public int getDrawableBackground() {
        return drawableBackground;
    }

    /**
     * Devuelve la pieza a dibujar
     *
     * @return la pieza a dibujar
     */
    public String getDrawablePiece() {
        return drawablePieza;
    }

    /**
     * Devuelve la pieza asociada
     *
     * @return la pieza asociada
     */
    public Piece getPiece(){
        return piece;
    }

    /**
     * Mueve la pieza y modifica la vista
     *
     * @param piece pieza a mover a esta casilla
     */
    public void setPiece(Piece piece){
        if(!piece.equals(null)){
            Log.e("","PIEZA:"+piece.getName());
            this.piece= piece;
            setDrawablePiece(piece.getImage());
        }else{
            Log.e("","PIECE ES NULA");
        }
    };

    /**
     * Actualiza la pieza a dibujar
     *
     * @param drawablePieza la pieza a dibujar
     */
    public void setDrawablePiece(String drawablePieza) {
        this.drawablePieza = drawablePieza;
    }
}