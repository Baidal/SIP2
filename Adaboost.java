/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2021_p2si;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;
import javafx.util.Pair;
import java.util.List;
import java.util.Set;


/**
 *
 * @author fidel
 */
public class Adaboost {
    
    private int clasificadoresDebiles;
    //Esta variable se encargará de guardar TODAS las imagenes que han sido usadas para el conjunto de pruebas.
    //En la primera posición de la pareja estará el número de la clase a la que pertenezca. Por ejemplo, si
    //hemos usado imagenes de la clase aviones, esta será 0. La segunda posición es el número de imágen para cada
    //clase. Así, la tercera imagen de la clase avion será <0,2>.
    private Set<Pair<Integer,Integer>> imagenesConjuntoTest;   
    private int A;
    private CIFAR10Loader ml;

    public int getA() {
        return A;
    }

    public void setA(int A) {
        this.A = A;
    }

    public Adaboost(int clasificadoresDebiles, int A) {
        this.clasificadoresDebiles = clasificadoresDebiles;
        this.A = A;
        ml = new CIFAR10Loader();
        ml.loadDBFromPath("./cifar10_2000");
        imagenesConjuntoTest = new HashSet<>();
    }
    
    
    public Set getImagenesConjuntoTest(){
        
        return imagenesConjuntoTest;
        
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
     * Devuelve el numero que le corresponde a cada clase a partir de su nombre
     * en la posición 0. En las otras 4 posiciones el array contendrá las demás
     * clases a utilizar.
     * @param clase Nombre de la clase
     * @return Int[] entre [0,9]. Devuelve -1 si no se ha introducido bien la
     * clase. 
     */
    private int[] calcularNumeroClase(String clase){
        
        
        
        int num_clase[] = new int[5];;
        
        switch(clase){
            case "avion":
                num_clase[0] = 0;
                break;
            
            case "coche":
                num_clase[0] = 1;
                break;
                
            case "pajaro":
                num_clase[0] = 2;
                break;
                
            case "gato":
                num_clase[0] = 3;
                break;
            
            case "ciervo":
                num_clase[0] = 4;
                break;
                
            case "perro":
                num_clase[0] = 5;
                break;
                
            case "rana":
                num_clase[0] = 6;
                break;
            
            case "caballo":
                num_clase[0] = 7;
                break;
                
            case "barco":
                num_clase[0] = 8;
                break;
            
            case "camion":
                num_clase[0] = 9;
                break;
                
            default:
                num_clase[0] = -1;
                break;
        }
        
        //ASIGNAMOS AL RESTO DE num_clase LAS OTRAS CLASES A ASIGNAR.
        //ESTAS CLASES NO PUEDEN SER IGUALES A LA CLASE PRESENTE EN num_clase[0]
        int contador = 1, posicion = 1;
        
        for(int i = 0; i < 4; i++){
            if(posicion == num_clase[0]){
                posicion++;
                num_clase[contador] = posicion;
                posicion++;
            }else{
                
                num_clase[contador] = posicion;
                posicion++;
            }
            
            contador++;
        }
        
     
        return num_clase;
        
    }
    
    
    /**
     * Carga un conjunto de pruebas.
     * @param porcentaje Porcentaje de imágenes de aviones presentes en el
     *                   conjunto final
     * @param tamanyo Tamaño del conjunto de pruebas
     * @return ArrayList de parejas de Imagen y enteros.
     */
    private List cargarConjuntoPrueba(int porcentaje, int tamanyo, String clase){
        Random rand = new Random();

        //ArrayList que vamos a devolver
        List<Pair<Imagen,Integer>> conjuntoPrueba = new ArrayList<>(tamanyo);
       
        int num_clase[] = calcularNumeroClase(clase);
        
        
        //Cargamos las imagenes de la clase a entrenar
        ArrayList clase0 = ml.getImageDatabaseForDigit(num_clase[0]);
        
        //Cargamos imagenes de otras clases
        ArrayList clase1 = ml.getImageDatabaseForDigit(num_clase[1]);
        ArrayList clase2 = ml.getImageDatabaseForDigit(num_clase[2]);
        ArrayList clase3 = ml.getImageDatabaseForDigit(num_clase[3]);
        ArrayList clase4 = ml.getImageDatabaseForDigit(num_clase[4]);
    
        //Generamos aleatoriamente las posiciones de la arraylist que tendrán
        //imagenes de la clase. El numero de fotos vendrá dictado por los
        //parámetros porcentaje y tamanyo.
        int numImagenes = (porcentaje*tamanyo)/100;
        TreeSet<Integer> posicionesImagenes = new TreeSet<>();
        
        while(posicionesImagenes.size() < numImagenes){
            posicionesImagenes.add(rand.nextInt(tamanyo));
        }
        
        //Añadimos las imagenes
        int posicionClase0 = 0;
        int posicionClase1 = 0;
        int posicionClase2 = 0;
        int posicionClase3 = 0;
        int posicionClase4 = 0;
        for(int i = 0 ; i < tamanyo; i++){
            if(posicionesImagenes.contains(i)){ //Imagenes de la clase a entrenar
                
                imagenesConjuntoTest.add(new Pair<>(num_clase[0], posicionClase0));
                conjuntoPrueba.add(new Pair<>((Imagen)clase0.get(posicionClase0), 1));
                posicionClase0++;
               
            }else{ //Imagenes del resto de clases
                switch(rand.nextInt(4)){
                    case 0:
                        imagenesConjuntoTest.add(new Pair<>(num_clase[1], posicionClase1));
                        conjuntoPrueba.add(new Pair<>((Imagen)clase1.get(posicionClase1), -1));
                        posicionClase1++;
                        break;
                    
                    case 1:
                        imagenesConjuntoTest.add(new Pair<>(num_clase[2], posicionClase2));
                        conjuntoPrueba.add(new Pair<>((Imagen)clase2.get(posicionClase2), -1));
                        posicionClase2++;
                        break;
                        
                    case 2:
                        imagenesConjuntoTest.add(new Pair<>(num_clase[3], posicionClase3));
                        conjuntoPrueba.add(new Pair<>((Imagen)clase3.get(posicionClase3), -1));
                        posicionClase3++;
                        break;
                       
                    case 3:
                        imagenesConjuntoTest.add(new Pair<>(num_clase[4], posicionClase4));
                        conjuntoPrueba.add(new Pair<>((Imagen)clase4.get(posicionClase4), -1));
                        posicionClase4++;
                        break;
                }
                
                
            }
                    
            
        }
        
        return conjuntoPrueba;
    }
    
    /**
     * Carga el conjunto de test a partir de las N imagenes finales de la clase 
     * @param tamanyo Tamaño del conjunto de prueba
     * @param clase Clase del conjunto de test a probar
     * @return List<Imagen>
     */
    private List cargarConjuntoTest(int tamanyo, String clase){
        List<Imagen> lista = new ArrayList<>();
        
        int num_clase[] = calcularNumeroClase(clase);
             
        
        //Cargamos las imagenes que son de la clase a probar
        ArrayList clase0 = ml.getImageDatabaseForDigit(num_clase[0]);
        
        //Recorre las n ultimas imagenes de la clase
        for(int i = clase0.size() - tamanyo; i < clase0.size(); i++){
            
            lista.add((Imagen)clase0.get(i));
            
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
    
    /**
     * Aplica adaboost a una clase determinada, que vendrá indicada por el
     * parámetro clase. 
     * @param clase String
     * @return List<ClasificadorDebil>. Devuelve el clasificador fuerte
     * construido por el algoritmo.
     */
    private List<ClasificadorDebil> ADABOOST(String clase){
        
        List<Pair<Imagen,Integer>> entrenamiento = cargarConjuntoPrueba(50,300,clase);
        List<Double> D = new ArrayList<>(Collections.nCopies(entrenamiento.size(), (double)1/entrenamiento.size())); //Creamos la arraylist D inicializada a 1/N
        List<ClasificadorDebil> T = new ArrayList<>(clasificadoresDebiles);
        ClasificadorDebil mejorClasificador;
        
        
        for(int i = 0; i < clasificadoresDebiles; i++){ //Bucle principal
            
            mejorClasificador = null;
            
            //BUCLE A
            for(int j = 0; j < A; j++){
                
                //Generamos el clasificador y lo aplicamos al conjunto de pruebas
                ClasificadorDebil h = generarClasifAzar(3072,256);
                List<Integer> resultado = aplicarClasifDebil(h,entrenamiento);
                
                //Calculamos el error
                for(int k = 0; k < entrenamiento.size();k++){
                    
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
            
            if(mejorClasificador != null){
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
        
        }
        
        
        return T;
    }
    
    private List cargarTodasImagenesPrueba(Set<Pair<Integer,Integer>> imagenesConjuntoTest){
        List<Pair<Imagen,Integer>> conjuntoPrueba = new ArrayList<>();
        
        List<List<Imagen>> todasImagenes = new ArrayList();
        
        for(int i = 0; i < 10; i++){
            todasImagenes.add(ml.getImageDatabaseForDigit(i));
        }

        int contador = 0;
        
        for(int i = 0; i < 10; i++){//recorre las 10 clases
            for(int j = 0; j < 200; j++){ //recorre todas las posiciones de cada clase
                
                if(imagenesConjuntoTest.contains(new Pair<>(i,j))){
                    
                    conjuntoPrueba.add(new Pair<>((Imagen)todasImagenes.get(i).get(j), i));
                    
                       contador++;
                }
                
            }
            
            
        }
        
        System.out.println("Hay " + contador + " imágenes.");
        
        return conjuntoPrueba;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Set<Pair<Integer,Integer>> imagenesConjuntoTest;
        
        //Son necesarios dos argumentos 
        if (args.length == 2) {
            
            //Se ejecuta la práctica como entrenamiento
            if (args[0].equals("-t")) {
               
               Adaboost adaboost = new Adaboost(800,100);
               
               System.out.println("Generando Clasificadores débiles de la clase avion.");
               List<ClasificadorDebil> T_avion = adaboost.ADABOOST("avion");
               System.out.println("Completado. Generando Clasificadores débiles de la clase coche.");
               List<ClasificadorDebil> T_coche = adaboost.ADABOOST("coche");
               System.out.println("Completado. Generando Clasificadores débiles de la clase ciervo.");
               List<ClasificadorDebil> T_ciervo = adaboost.ADABOOST("ciervo");
               System.out.println("Completado. Generando Clasificadores débiles de la clase gato.");
               List<ClasificadorDebil> T_gato = adaboost.ADABOOST("gato");
               System.out.println("Completado. Generando Clasificadores débiles de la clase pajaro.");
               List<ClasificadorDebil> T_pajaro = adaboost.ADABOOST("pajaro");
               System.out.println("Completado. Generando Clasificadores débiles de la clase perro.");
               List<ClasificadorDebil> T_perro = adaboost.ADABOOST("perro");
               System.out.println("Completado. Generando Clasificadores débiles de la clase rana.");
               List<ClasificadorDebil> T_rana = adaboost.ADABOOST("rana");
               System.out.println("Completado. Generando Clasificadores débiles de la clase caballo.");
               List<ClasificadorDebil> T_caballo = adaboost.ADABOOST("caballo");
               System.out.println("Completado. Generando Clasificadores débiles de la clase barco.");
               List<ClasificadorDebil> T_barco = adaboost.ADABOOST("barco");
               System.out.println("Completado. Generando Clasificadores débiles de la clase camion.");
               List<ClasificadorDebil> T_camion = adaboost.ADABOOST("camion");
               System.out.println("Completado.");
               
               imagenesConjuntoTest = adaboost.getImagenesConjuntoTest();
               
               List<Pair<Imagen,Integer>> todasImagenesConjuntoPrueba = adaboost.cargarTodasImagenesPrueba(imagenesConjuntoTest);
               
               /*
               //PARTE DE GENERERAR EL TEST.
        
                List test = adaboost.cargarConjuntoTest(10,"avion");

                double sumatorio;

                for(int i = 0; i < test.size(); i++){
                    sumatorio = 0;
                    List<Integer> resultado = adaboost.aplicarClasificadoresImagen(T,(Imagen)test.get(i));

                    for(int j = 0; j < resultado.size(); j++){

                        sumatorio = sumatorio + T.get(j).getConfianza()*(double)resultado.get(j);

                    }

                    if(sumatorio > 0 )
                        System.out.println("La imagen " + i + " se ha analizado como avión.");
                    else
                        System.out.println("La imagen " + i + " no se ha analizado como no avión.");
                }*/
               
                
                
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
