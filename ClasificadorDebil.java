/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2021_p2si;

import java.util.Objects;

/**
 *
 * @author PC
 */
public class ClasificadorDebil {
    
    private Pixel pixel;
    private int umbral;
    private int direccion;
    
    public ClasificadorDebil(){}

    public ClasificadorDebil(Pixel pixel, int umbral, int direccion){
        this.pixel = pixel;
        this.umbral = umbral;
        this.direccion = direccion;
    }
    
    public ClasificadorDebil(ClasificadorDebil clas){
        this.pixel = clas.pixel;
        this.umbral = clas.umbral;
        this.direccion = clas.direccion;
    }

    public Pixel getPixel() {
        return pixel;
    }

    public void setPixel(Pixel pixel) {
        this.pixel = pixel;
    }

    public int getUmbral() {
        return umbral;
    }

    public void setUmbral(int umbral) {
        this.umbral = umbral;
    }

    public int getDireccion() {
        return direccion;
    }

    public void setDireccion(int direccion) {
        this.direccion = direccion;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.pixel);
        hash = 47 * hash + this.umbral;
        hash = 47 * hash + this.direccion;
        return hash;
    }

    /**
     * Dos clasificadores debiles seran iguales si todos sus parametros son
     * iguales
     * @param obj Objeto
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
        final ClasificadorDebil other = (ClasificadorDebil) obj;
        if (this.umbral != other.umbral) {
            return false;
        }
        if (this.direccion != other.direccion) {
            return false;
        }
        if (!Objects.equals(this.pixel, other.pixel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ClasificadorDebil{" + "pixel=" + pixel + ", umbral=" + umbral + ", direccion=" + direccion + '}';
    }
    
    
    
    
    
}
