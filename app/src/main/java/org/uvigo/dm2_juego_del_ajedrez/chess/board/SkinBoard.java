package org.uvigo.dm2_juego_del_ajedrez.chess.board;

/**
 * Clase en la que se almacena la skin que usa el tablero.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class SkinBoard{
    int lightcolor;
    int darkcolor;

    /**
     * Construye e inicializa la skin del tablero
     *
     * @param lightcolor    color de las casillas claras del tablero
     * @param darkcolor     color de las casillas oscuras del tablero
     */
    public SkinBoard(int lightcolor, int darkcolor) {
        this.lightcolor = lightcolor;
        this.darkcolor = darkcolor;
    }

    /**
     * Devuelve el color claro del tablero
     *
     * @return el color de las casillas claras del tablero
     */
    public int getLightColor(){
        return lightcolor;
    }

    /**
     * Devuelve el color oscuro del tablero
     *
     * @return el color de las casillas oscuras del tablero
     */
    public int getDarkcolor() {
        return darkcolor;
    }
}