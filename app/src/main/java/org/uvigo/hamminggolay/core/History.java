package org.uvigo.hamminggolay.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class History implements Serializable {
    String name;
    ArrayList<String> log;
    HashMap<String,String> posPieces;

    public History(String name){
        this.name=name;
        this.log= new ArrayList<>();
        this.posPieces=new HashMap<>();
    }

    public History(String name, String log, String posPieces) {
        this.name = name;
        this.log = getParsedLog(log);
        this.posPieces=getParsedPos(posPieces);
    }

    /**Devuelve el nombre*/
    public String getName(){
        return name;
    }

    /**Devuelve la lista de movimientos*/
    public ArrayList<String> getLog(){
        return log;
    }

    /**String con los movimientos en un string*/
    public String getPlainLog(){
        return log.toString();
    }

    /**Añade un movimiento al historial*/
    public void addMove(String move) {
        log.add(move);
    }

    public HashMap<String, String> getPosPieces(){
        return posPieces;
    }

    /**String con los movimientos en un string*/
    public String getPlainPos(){
        return posPieces.toString();
    }

    /** Añade una pieza y su posicion para poder recuperarla en el continuarPartida*/
    public void addPos(String name, String pos){
        if(posPieces.get(name)!=null){
            posPieces.replace(name,pos);
        }else{
            posPieces.put(name,pos);
        }
    }

    /**Devuelve un log como lista*/
    public ArrayList<String> getParsedLog(String log){
        String[] parsedString= log.replace("[","").replace("]","").split(", ");
        ArrayList<String> toret= new ArrayList<>();

        for(String move: parsedString){
            toret.add(move);
        }

        return toret;
    }

    /**Devuelve el mapa de posiciones desde string*/
    public HashMap<String, String> getParsedPos(String pos){
        String[] parsedString= pos.replace("{","").replace("}","").split(", ");

        HashMap<String,String> toret= new HashMap<>();

        for(String line: parsedString){
            String[] parsedLine= line.split("=");
            toret.put(parsedLine[0],parsedLine[1]);
        }

        return toret;
    }

    //{key1=value1, key2=value2}
    public String toString(){
        return name+";"+log.toString()+";"+posPieces;
    }

}
