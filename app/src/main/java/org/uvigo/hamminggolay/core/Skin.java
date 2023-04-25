package org.uvigo.hamminggolay.core;
import java.io.Serializable;

/**
 * Clase que guarda la informacion sobre una skin
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class Skin implements Serializable {

    private String name;
    private String image;

    /**
     * Construye e inicializa una skin
     *
     * @param name  nombre de la skin
     * @param image rita de la imagen de la skin
     */
    public Skin(String name, String image){
        this.name=name;
        this.image=image;
    }

    /**
     * Devuelve el nombre de la skin
     *
     * @return el nombre de la skin
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve la ruta de la imagen
     *
     * @return la ruta de la imagen
     */
    public String getImagePath(){
        return image;
    }

    /**
     * Cambia el nombre de la skin
     *
     * @param name nuevo nombre para la skin
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name+","+image;
    }
}
