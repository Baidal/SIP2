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
        
        
        
        int num_clase[] = new int[5];
        
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
     * Aplica un conjunto de clasificadores debiles a una imagen.
     * Devolverá un valor que indicará si se ha resuelto como que pertenece o
     * como que no.
     * @param clasificadores Clasificadores a aplicar
     * @param img Imagen a la que aplicar los clasificadores.
     * @return int Negativo si no pertenece, positivo si sí
     */
    private int aplicarClasificadorFuerteImagen(List<ClasificadorDebil> clasificadores, Imagen img){
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
        
        double pertenece = 0.0;
        
        for(int i = 0; i < list.size(); i++){
            
            pertenece = pertenece + clasificadores.get(i).getConfianza()*list.get(i);
            
        }
        
        if(pertenece > 0.0)
            return 1;
        else
            return -1;
        
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
    
    /**
     * Carga todas las imagenes que han sido usadas para probar las distintas
     * clases y las que no. Estas están en la variable pasada por parámetro.
     * 
     * @param imagenesConjuntoTest Set de parejas que contienen las imagenes 
     *                             para probar el conjunto de tests. La key de
     *                             la pareja será la clase a la que pertenece,
     *                             el value la posición de la foto.
     * 
     * @return List<List<Pair<Imagen,Integer>>> Devuelve una lista de listas de parejas.
     *                                          Esta primera lista contendrá en la posición 
     *                                          0 las imagenes que se han usado en la prueba,
     *                                          y en la posición 1 las que no se han usado 
     *                                          (y se usarán en el test).
     */
    private List cargarTodasImagenesPruebayTest(Set<Pair<Integer,Integer>> imagenesConjuntoTest){
        //Lista a devolver
        List<List<Pair<Imagen,Integer>>> testYPrueba = new ArrayList<>();
        
        List<Pair<Imagen,Integer>> imagenesPrueba = new ArrayList<>();
        List<Pair<Imagen,Integer>> imagenesTest = new ArrayList<>();
        
        //Lista de cada una de las listas de las imagenes
        List<List<Imagen>> todasImagenes = new ArrayList();
        
        for(int i = 0; i < 10; i++){
            todasImagenes.add(ml.getImageDatabaseForDigit(i));
        }
        
        

        //int contador = 0;
        
        for(int i = 0; i < 10; i++){//recorre las 10 clases
            for(int j = 0; j < todasImagenes.get(i).size(); j++){ //recorre todas las posiciones de cada clase
                
                //Si contiene la clase
                if(imagenesConjuntoTest.contains(new Pair<>(i,j))){
                    
                    imagenesPrueba.add(new Pair<>((Imagen)todasImagenes.get(i).get(j), i));
                    //contador++;
                }else{
                    
                    imagenesTest.add(new Pair<>((Imagen)todasImagenes.get(i).get(j), i));
                    
                }
                
            }
            
        }
        
        testYPrueba.add(imagenesPrueba);
        testYPrueba.add(imagenesTest);
        //System.out.println("Hay " + contador + " imágenes.");
        
        return testYPrueba;
    }
    
    
    private boolean comprobarResultadosAplicarClasificadoresFuertes(int resultados[]){
        
        int numeroUnos = 0, numeroMenosUnos = 0;
        
        for(int i = 0; i < resultados.length;i++){
            if(resultados[i] == 1)
                numeroUnos++;
            else
                numeroMenosUnos++;
        }
    
        return !(numeroUnos != 1 && numeroMenosUnos != 1);
        
    }
    
    /**
     * Aplica el conjunto de los 10 clasificadores débiles a un conjunto de imagenes
     * pasado por parámetro. Devuelve el porcentaje de acierto que se ha tenido.
     * @param clasificadoresDebiles Lista de Listas de clasificadores débiles. Por ejemplo,
     *                              la posición 0 tendrá la lista de los clasificadores
     *                              débiles de la clase avión.
     * @param imagenes Pareja de imagenes. La key es la imagen, el value es la clase a la que
     *                  pertenece realmente la imagen.
     * @return Integer. Porcentaje de acierto de los clasificadores débiles.
     */
    private int comprobarImagenes(List<List<ClasificadorDebil>> clasificadoresDebiles, List<Pair<Imagen,Integer>> imagenes){
        
        //Variable que guardará el resultado de aplicar cada clasificador débil a una imagen
        int resultados[] = new int[10];
        
        //recorremos todas las imagenes
        for(int i = 0; i < imagenes.size(); i++){
            
            //Aplicamos cada clasificadorfuerte a cada imagen
            for(int y = 0; y < resultados.length; y++){
                
                resultados[y] = aplicarClasificadorFuerteImagen(clasificadoresDebiles.get(y),imagenes.get(i).getKey());
                
            }
           
            if(comprobarResultadosAplicarClasificadoresFuertes(resultados)){
                
            }else{
                
                
            }
        
        
        }
        
        
        return 1;
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
               
               String[] clases = {"avion","coche","ciervo","gato","pajaro","perro","rana","caballo","barco","camion"};
               
               List<List<ClasificadorDebil>> clasificadoresDebiles = new ArrayList<>();
               
               for(int i = 0; i < clases.length;i++){
                   if(i == 0)
                        System.out.print("Generando Clasificadores débiles de la clase " + clases[i] + ". ");
                   else
                       System.out.print("Completado." + "\n" + "Generando Clasificadores débiles de la clase "+ clases[i] + ". ");
                       
                    List<ClasificadorDebil> clasificadores = adaboost.ADABOOST(clases[i]);
                    clasificadoresDebiles.add(clasificadores);
                    
                    if(i == clases.length - 1)
                        System.out.println("Completado.");
               }
               
               imagenesConjuntoTest = adaboost.getImagenesConjuntoTest();
               
               List<List<Pair<Imagen,Integer>>> todasImagenes = adaboost.cargarTodasImagenesPruebayTest(imagenesConjuntoTest);
               List<Pair<Imagen,Integer>> imagenesPrueba = todasImagenes.get(0);
               List<Pair<Imagen,Integer>> imagenesTest = todasImagenes.get(1);
               
               System.out.println("imagenesPrueba: " + imagenesPrueba.size() + "\n" + "imagenesTest: " + imagenesTest.size());
               
               int porcentajeImagenesPrueba = adaboost.comprobarImagenes(clasificadoresDebiles,imagenesPrueba);
               int porcentajeImagenesTest = adaboost.comprobarImagenes(clasificadoresDebiles,imagenesTest);
                
                
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
