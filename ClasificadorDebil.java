/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2021_p2si;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 *
 * @author PC
 */
public class ClasificadorDebil {
    
    private Pixel pixel;
    private int umbral;
    private int direccion;
    private double error;
    private double confianza;
    
    public ClasificadorDebil(){}

    public ClasificadorDebil(Pixel pixel, int umbral, int direccion,double confianza,double error){
        this.pixel = pixel;
        this.umbral = umbral;
        this.direccion = direccion;
        this.confianza = confianza;
        this.error = error;
        
    }
    
    public ClasificadorDebil(ClasificadorDebil clas){
        this.pixel = clas.pixel;
        this.umbral = clas.umbral;
        this.direccion = clas.direccion;
        this.confianza = clas.confianza;
        this.error = clas.error;
        
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

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public double getConfianza() {
        return confianza;
    }

    public void setConfianza(double confianza) {
        this.confianza = confianza;
    }
    
    public void calcularConfianza(){
        
        if(error != 0){
            double b = (1 - error)/error;
            BigDecimal bd = BigDecimal.valueOf(b);
            bd = bd.setScale(4,RoundingMode.HALF_UP);
            b = bd.doubleValue();
            
            confianza = Math.log10(b) / Math.log10(2);
            confianza = 0.5*confianza;
        }
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
