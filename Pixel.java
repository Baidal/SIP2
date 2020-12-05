/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2021_p2si;

/**
 *
 * @author Luis Vidal Rico
 */
public class Pixel {
    private int posicion;
    private int canal;
    
    public Pixel(){ 
    }
    
    public Pixel(int posicion, int canal){
        this.posicion = posicion;
        this.canal = canal;
    }
    
    public Pixel(Pixel pixel){
        this.posicion = pixel.posicion;
        this.canal = pixel.canal;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getCanal() {
        return canal;
    }

    public void setCanal(int canal) {
        this.canal = canal;
    }

    @Override
    public String toString() {
        return posicion + " " + canal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.posicion;
        hash = 31 * hash + this.canal;
        return hash;
    }

    
  

    /**
     * Comprueba si dos pixeles son iguales. Estos lo serán sí coinciden su
     * poisición y su canal.
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pixel other = (Pixel) obj;
        if (this.posicion != other.posicion) {
            return false;
        }
        if (this.canal != other.canal) {
            return false;
        }
        return true;
    }
    
    
    
    
    
}
