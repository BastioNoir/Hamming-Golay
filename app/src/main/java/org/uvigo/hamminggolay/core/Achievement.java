package org.uvigo.hamminggolay.core;
import java.io.Serializable;

/**
 * Clase que representa y guarda informacion perteneciente a un logro.
 *
 * @author Ruben Gomez Martinez
 * @author Alvaro Novoa Fernandez
 * @author Andres Garcia Figueroa
 */
public class Achievement implements Serializable {
    private String name;
    private String description;

    /**
     * Construye e inicializa un logro
     *
     * @param name          Nombre del logro
     * @param description   Descripción del logro
     */
    public Achievement(String name, String description){
        this.name=name;
        this.description=description;
    }

    /**
     * Devuelve el nombre del logro
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve la descripcion del logro
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name+";"+description;
    }
}
