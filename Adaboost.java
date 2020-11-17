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
     * Aplica un clasificador debil a una imagen en concreto
     * @param h Clasificador debil
     * @param prueba Conjunto de imagenes de prueba
     * @param pos Posicion de prueba a aplicar
     * @return int -1 si no determina que no pertenece, 1 si determina que sí
     * pertenece
     */
    private int aplicarUnClasifDebil(ClasificadorDebil h, List<Pair<Imagen,Integer>> prueba, int pos){
            //Cargamos la imagen pos del conjunto de pruebas
            Imagen img = (Imagen)prueba.get(pos).getKey();
            
            //Cargamos el vector de bytes de la imagen
            byte imageData[] = img.getImageData();
            
            //Asignamos el canal al clasificador debil
            h.getPixel().setCanal(Byte2Unsigned(imageData[h.getPixel().getPosicion()]));
        
            int pertenece;
            
            //Clasificamos si pertenece a la clase usando el umbral
            if(h.getDireccion() == 1){
                //Pertenecerá a la clase si el pixel es mayor que el umbral
                if(h.getPixel().getCanal() > h.getUmbral())
                    pertenece = 1; //Pertenece
                else
                    pertenece = -1; //No pertenece 
            }else{
                //Pertenecerá a la clase si el pixel es mayor que el umbral
                if(h.getPixel().getCanal() >= h.getUmbral())
                    pertenece = -1; //No Pertenece
                else
                    pertenece = 1; //Pertenece
            }
            
            return pertenece;
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
     * En la posición i, el vector devuelto contendrá 1 si se ha clasificado
     * como que pertenece o -1 si se ha clasificado como que no pertenece.
     * @param h Clasificador Debil
     * @param prueba Conjunto de pruebas
     * @return List<Integer>
     */
    private List aplicarClasifDebil(ClasificadorDebil h, List<Pair<Imagen,Integer>> prueba){
        List<Integer> vector_resultado = new ArrayList<>(prueba.size());
        
        for(int i = 0; i < prueba.size(); i++){
            
            int pertenece = aplicarUnClasifDebil(h,prueba,i);
            
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
    
    private List cargarConjuntoTest(int tamanyo){
        List<Imagen> lista = new ArrayList<>();
        
        //Cargador CIFAR10 de SI
        CIFAR10Loader ml = new CIFAR10Loader();
        ml.loadDBFromPath("./cifar10_2000");
        
        //Cargamos las imagenes que son aviones
        ArrayList aviones = ml.getImageDatabaseForDigit(1);
        
        for(int i = aviones.size() - tamanyo; i < aviones.size(); i++){
            
            lista.add((Imagen)aviones.get(i));
            
        }
        
        return lista;
        
    }
    
    /**
     * Aplica un conjunto de clasificadores debiles a una imagen-
     * @param clasificadores Clasificadores a aplicar
     * @param img Imagen a la que aplicar los clasificadores.
     * @return Lista de enteros que contendrá -1 o 1, dependiendo de si pertenece
     * o no.
     */
    private List<Integer> aplicarClasificadoresImagen(List<ClasificadorDebil> clasificadores, Imagen img){
        List<Integer> list = new ArrayList<>();
    
        //Cargamos el vector de bytes de la imagen
        byte imageData[] = img.getImageData();
            
        
        
        for(int i = 0; i < clasificadores.size(); i++){
            //Asignamos el canal al clasificador debil
            clasificadores.get(i).getPixel().setCanal(Byte2Unsigned(imageData[clasificadores.get(i).getPixel().getPosicion()]));
        
            int pertenece;
            
            //Clasificamos si pertenece a la clase usando el umbral
            if(clasificadores.get(i).getDireccion() == 1){
                //Pertenecerá a la clase si el pixel es mayor que el umbral
                if(clasificadores.get(i).getPixel().getCanal() > clasificadores.get(i).getUmbral())
                    pertenece = 1; //Pertenece
                else
                    pertenece = -1; //No pertenece 
            }else{
                //Pertenecerá a la clase si el pixel no es mayor que el umbral
                if(clasificadores.get(i).getPixel().getCanal() >= clasificadores.get(i).getUmbral())
                    pertenece = -1; //No Pertenece
                else
                    pertenece = 1; //Pertenece
            }
            
            list.add(pertenece);
            
        }
        
        return list;
    }
    
    private double ADABOOST(){
        
        List<Pair<Imagen,Integer>> entrenamiento = cargarConjuntoPrueba(50,350);
        List<Double> D = new ArrayList<>(Collections.nCopies(entrenamiento.size(), (double)1/entrenamiento.size())); //Creamos la arraylist D inicializada a 1/N
        List<ClasificadorDebil> T = new ArrayList<>(clasificadoresDebiles);
        ClasificadorDebil mejorClasificador;
        double sumatorio = 0;
        
        
        for(int i = 0; i < clasificadoresDebiles; i++){ //Bucle principal
            
            mejorClasificador = null;
            
            for(int j = 0; j < A; j++){
                
                //Generamos el clasificador y lo aplicamos al conjunto de pruebas
                ClasificadorDebil h = generarClasifAzar(3072,256);
                List<Integer> resultado = aplicarClasifDebil(h,entrenamiento);
                
                
                for(int k = 0; k < entrenamiento.size();k++){//Calculamos el error
                    if(!entrenamiento.get(k).getValue().equals(resultado.get(k))){
                        h.setError(h.getError() + D.get(k));
                    }
                    
                }
            
                
                if(mejorClasificador == null || mejorClasificador.getError() > h.getError()){
                    mejorClasificador = h;                
                }
            }
            
            //Calculamos la confianza del clasificador y lo guardamos
            if(mejorClasificador != null)
                mejorClasificador.calcularConfianza();
            T.add(mejorClasificador);
            
            //Calculamos la nueva distribucion
            for(int j = 0; j < D.size();j++){
                
                //Calculo de y(i)*h(x_i)
                int pertenece = aplicarUnClasifDebil(mejorClasificador,entrenamiento,j);
                
                double nueva = D.get(j);
                nueva = nueva*Math.exp(-1 * mejorClasificador.getConfianza() * pertenece * entrenamiento.get(j).getValue());
                
                D.set(j, nueva);
                
            }
            
            //Calculo de z
            double z = 0;
            for(int j = 0; j < D.size(); j++)
                z = z + D.get(j);
            
            //Calculo definitivo de D
            for(int j = 0; j < D.size(); j++){
                D.set(j, D.get(j)/z);
             
            }
            
        }
        
        List test = cargarConjuntoTest(10);
        
        for(int i = 0; i < test.size(); i++){
            sumatorio = 0;
            List<Integer> resultado = aplicarClasificadoresImagen(T,(Imagen)test.get(i));
            
            for(int j = 0; j < resultado.size(); j++){
            
                sumatorio = sumatorio + T.get(j).getConfianza()*(double)resultado.get(j);
                
            }
            
            if(sumatorio > 0 )
                System.out.println("La imagen " + i + " se ha analizado correctamente.");
            else
                System.out.println("La imagen " + i + " no se ha analizado correctamente.");
        }
        
        
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
               
               Adaboost adaboost = new Adaboost(200,200);
               
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
