/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2021_p2si;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import javafx.util.Pair;
import java.util.List;


/**
 *
 * @author fidel
 */
public class Adaboost {
    
    private int clasificadoresDebiles;
    
    private int A;

    public int getA() {
        return A;
    }

    public void setA(int A) {
        this.A = A;
    }

    public Adaboost(int clasificadoresDebiles, int A) {
        this.clasificadoresDebiles = clasificadoresDebiles;
        this.A = A;
    }
    
    
    
    

    public int getClasificadoresDebiles() {
        return clasificadoresDebiles;
    }

    public void setClasificadoresDebiles(int clasificadoresDebiles) {
        this.clasificadoresDebiles = clasificadoresDebiles;
    }
    
    
    public static int Byte2Unsigned(byte b) {
        return b & 0xFF;
    }
    
    /**
     * Genera un clasificador débil al azar. La posición del pixel y el umbral
     * vienen dados en dimPixel y dimUmbral. De esa forma, si dimPixel es 3072,
     * el pixel elegido estará definido en [0,3071]
     * @param dimPixel
     * @param dimUmbral
     * @return ClasificadorDebil
     */
    private ClasificadorDebil generarClasifAzar(int dimPixel, int dimUmbral){
        
        Random rand = new Random();
        
        int posPixel = rand.nextInt(dimPixel); //Generamos el pixel a examinar
        int umbral = rand.nextInt(dimUmbral); //Generamos el umbral
        int direccion = rand.nextInt(2); //genera un numero entre 0 y 1
        if(direccion == 0)
            direccion = -1;
        else
            direccion = 1;
        
        
        
        return new ClasificadorDebil(new Pixel(posPixel,-1),umbral,direccion,0,0);
        
        
    }
    /**
     * Aplica el clasificador debil a cada elemento del conjunto de prueba.
     * En la posición i, el vector devuelto contendrá 0 si se ha clasificado
     * como que pertenece o 1 si se ha clasificado como que no pertenece.
     * @param h Clasificador Debil
     * @param prueba Conjunto de pruebas
     * @return List<Integer>
     */
    private List aplicarClasifDebil(ClasificadorDebil h, List<Pair<Imagen,Integer>> prueba){
        List<Integer> vector_resultado = new ArrayList<>(prueba.size());
        
        for(int i = 0; i < prueba.size(); i++){
            //Cargamos la imagen i del conjunto de pruebas
            Imagen img = (Imagen)prueba.get(i).getKey();
            
            //Cargamos el vector de bytes de la imagen
            byte imageData[] = img.getImageData();
            
            //Asignamos el canal al clasificador debil
            h.getPixel().setCanal(Byte2Unsigned(imageData[h.getPixel().getPosicion()]));
        
            int pertenece;
            
            //Clasificamos si pertenece a la clase usando el umbral
            if(h.getDireccion() == 1){
                //Pertenecerá a la clase si el pixel es mayor que el umbral
                if(h.getPixel().getCanal() > h.getUmbral())
                    pertenece = 0; //Pertenece
                else
                    pertenece = 1; //No pertenece 
            }else{
                //Pertenecerá a la clase si el pixel es mayor que el umbral
                if(h.getPixel().getCanal() >= h.getUmbral())
                    pertenece = 1; //No Pertenece
                else
                    pertenece = 0; //Pertenece
            }
            
            vector_resultado.add(pertenece);
        }
    
    
        return vector_resultado;
    }
    
    /**
     * Carga un conjunto de pruebas para la clase avion.
     * @param porcentaje Porcentaje de imágenes de aviones presentes en el
     *                   conjunto final
     * @param tamanyo Tamaño del conjunto de pruebas
     * @return ArrayList de parejas de Imagen y enteros.
     */
    private List cargarConjuntoPrueba(int porcentaje, int tamanyo){
        Random rand = new Random();

        //ArrayList que vamos a devolver
        List<Pair<Imagen,Integer>> conjuntoPrueba = new ArrayList<>(tamanyo);
        
        //Cargador CIFAR10 de SI
        CIFAR10Loader ml = new CIFAR10Loader();
        ml.loadDBFromPath("./cifar10_2000");
        
        //Cargamos las imagenes que son aviones
        ArrayList aviones = ml.getImageDatabaseForDigit(0);
        
        //Cargamos imagenes de otras clases
        ArrayList automoviles = ml.getImageDatabaseForDigit(1);
        ArrayList gatos = ml.getImageDatabaseForDigit(3);
        ArrayList ranas = ml.getImageDatabaseForDigit(6);
        ArrayList barcos = ml.getImageDatabaseForDigit(8);
    
        //Generamos aleatoriamente las posiciones de la arraylist que tendrán
        //imagenes de aviones. El numero de aviones vendrá dictado por los
        //parámetros porcentaje y tamanyo.
        int numAviones = (porcentaje*tamanyo)/100;
        TreeSet<Integer> posicionesAviones = new TreeSet<>();
        
        while(posicionesAviones.size() < numAviones){
            posicionesAviones.add(rand.nextInt(tamanyo));
        }
        
        
        int posicionAvion = 0;
        int posicionBarco = 0;
        int posicionGato = 0;
        int posicionRana = 0;
        int posicionAutomovil = 0;
        for(int i = 0 ; i < tamanyo; i++){
            if(posicionesAviones.contains(i)){
                
                conjuntoPrueba.add(new Pair<>((Imagen)aviones.get(posicionAvion), 1));
                posicionAvion++;
               
            }else{
                switch(rand.nextInt(4)){
                    case 0:
                        conjuntoPrueba.add(new Pair<>((Imagen)automoviles.get(posicionAutomovil), -1));
                        posicionAutomovil++;
                        break;
                    
                    case 1:
                        conjuntoPrueba.add(new Pair<>((Imagen)gatos.get(posicionGato), -1));
                        posicionGato++;
                        break;
                        
                    case 2:
                        conjuntoPrueba.add(new Pair<>((Imagen)ranas.get(posicionRana), -1));
                        posicionRana++;
                        break;
                       
                    case 3:
                        conjuntoPrueba.add(new Pair<>((Imagen)barcos.get(posicionBarco), -1));
                        posicionBarco++;
                        break;
                }
                
                
            }
                    
            
        }
        
        return conjuntoPrueba;
    }
    
    private double ADABOOST(){
        
        List<Pair<Imagen,Integer>> entrenamiento = cargarConjuntoPrueba(50,350);
        List<Double> D = new ArrayList<>(Collections.nCopies(entrenamiento.size(), (double)1/entrenamiento.size())); //Creamos la arraylist D inicializada a 1/N
        List<ClasificadorDebil> T = new ArrayList<>(clasificadoresDebiles);
        ClasificadorDebil mejorClasificador;
        int si = 0, no = 0;
        for(int i = 0; i < clasificadoresDebiles; i++){ //Bucle principal
            
            mejorClasificador = null;
            
            for(int j = 0; j < A; j++){
                
                //Generamos el clasificador y lo aplicamos al conjunto de pruebas
                ClasificadorDebil h = generarClasifAzar(3072,256);
                List<Integer> resultado = aplicarClasifDebil(h,entrenamiento);
                
                /*
                if(i == 0 && j == 0){
                    for(int z = 0; z < resultado.size(); z++)
                        if(resultado.get(z) == 0)
                            no++;
                        else
                            si++;
                    
                    System.out.println("No: " + no + " Si: " + si);
                }*/
                
                for(int k = 0; k < entrenamiento.size();k++){//Calculamos el error
                    if(entrenamiento.get(k).getValue().equals(1) && resultado.get(k).equals(1)){
                        h.setError(h.getError() + D.get(k));
                    }
                    if(entrenamiento.get(k).getValue().equals(-1) && resultado.get(k).equals(0)){
                        h.setError(h.getError() + D.get(k));
                    }
                
                }
                
                /*
                if(i == 0){
                    System.out.println("Numero errores para j("+ j +"): " + numErrores);
                    if(errorPeque > numErrores || errorPeque == 0)
                        errorPeque = numErrores;
                }*/
                
                if(mejorClasificador == null || mejorClasificador.getError() > h.getError()){
                    mejorClasificador = h;                
                }
            }
            
            if(mejorClasificador != null)
                mejorClasificador.calcularConfianza();
            T.add(mejorClasificador);
            
            
        }
        
        //System.out.println("Error mas pequeño para i = 0 " + errorPeque);
        
        return 1.0;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Son necesarios dos argumentos 
        if (args.length == 2) {
            
            //Se ejecuta la práctica como entrenamiento
            if (args[0].equals("-t")) {
               
               Adaboost adaboost = new Adaboost(1024,1024);
               
               adaboost.ADABOOST();
               
                
                
            } else {
                //Se ejecuta la práctica como test
               ;

            }
        } else {
            System.out.println("El número de parámetros es incorrecto");
            System.out.println("Las posibilidades son:");
            System.out.println("Adaboost –t <fichero_almacenamiento_clasificador_fuerte>");
            System.out.println("Adaboost <fichero_origen_clasificador_fuerte> <imagen_prueba>");
        }
    }
    
}
