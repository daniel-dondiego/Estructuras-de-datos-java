package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para diccionarios (<em>hash tables</em>). Un diccionario
 * generaliza el concepto de arreglo, permitiendo (en general,
 * dependiendo de qué tan buena sea su método para generar huellas
 * digitales) agregar, eliminar, y buscar valores en <i>O</i>(1) en
 * cada uno de estos casos.
 */
public class Diccionario<K, V> implements Iterable<V> {

    /* Clase privada para iteradores de diccionarios. */
    private class Iterador<V> implements Iterator<V> {
        /* En qué lista estamos. */
        private int indice;
        /* Diccionario. */
        private Diccionario<K,V> diccionario;
        /* Iterador auxiliar. */
        private Iterator<Diccionario<K,V>.Entrada<K,V>> iterador;

        /* Construye un nuevo iterador, auxiliándose de las listas
         * del diccionario. */
        public Iterador(Diccionario<K,V> diccionario) {
        this.diccionario = diccionario;
            while(indice < diccionario.entradas.length && diccionario.entradas[indice] == null)
                indice++;
            if(indice < diccionario.entradas.length)
                iterador = diccionario.entradas[indice].iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        public boolean hasNext() {
       if(iterador == null)
                return false;
            if(iterador.hasNext())
                return true;
            iterador = null;
            indice++;
            while(indice < diccionario.entradas.length && diccionario.entradas[indice] == null)
                indice++;
            if(indice < diccionario.entradas.length)
                iterador = diccionario.entradas[indice].iterator();
            return hasNext();
        }

        /* Regresa el siguiente elemento. */
        public V next() {
        if(iterador != null){
            Diccionario<K,V>.Entrada<K, V> d = iterador.next();
            return d.valor;
        }else
            throw new NoSuchElementException();
        }

        /* No lo implementamos: siempre lanza una excepción. */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /** Máxima carga permitida por el diccionario. */
    public static final double MAXIMA_CARGA = 0.72;

    /* Tamaño mínimo; decidido arbitrariamente a 2^6. */
    private static final int MIN_N = 64;

    /* Máscara para no usar módulo. */
    private int mascara;
    /* Huella digital. */
    private HuellaDigital<K> huella;
    /* Nuestro diccionario. */
    private Lista<Entrada<K, V>>[] entradas;
    /* Número de valores*/
    private int total;

    /* Clase para las entradas del diccionario. */
    private class Entrada<K, V> {
        public K llave;
        public V valor;
        public Entrada(K llave, V valor) {
            this.llave = llave;
            this.valor = valor;
        }
    }

    /* Truco para crear un arreglo genérico. Es necesario hacerlo
       así por cómo Java implementa sus genéricos; de otra forma
       obtenemos advertencias del compilador. */
    @SuppressWarnings("unchecked") private Lista<Entrada<K, V>>[] nuevoArreglo(int n) {
        Lista[] arreglo = new Lista[n];
        return (Lista<Entrada<K, V>>[])arreglo;
    }

    /**
     * Construye un diccionario con un tamaño inicial y huella
     * digital predeterminados.
     */
    public Diccionario() {
        this(MIN_N, new HuellaDigital<K>() {
            public int huellaDigital(K llave) {
                return llave.hashCode();
            }
        });
    }

    /**
     * Construye un diccionario con un tamaño inicial definido por
     * el usuario, y una huella digital predeterminada.
     * @param tam el tamaño a utilizar.
     */
    public Diccionario(int tam) {
        this(tam,new HuellaDigital<K>() {
            public int huellaDigital(K llave) {
                return llave.hashCode();
            }
        });    
    }

    /**
     * Construye un diccionario con un tamaño inicial
     * predeterminado, y una huella digital definida por el usuario.
     * @param huella la huella digital a utilizar.
     */
    public Diccionario(HuellaDigital<K> huella) {
        this.huella = huella;
        mascara = mascara(3*MIN_N);
        entradas = nuevoArreglo(mascara+1);
        total = 0;
    }

    /**
     * Construye un diccionario con un tamaño inicial, y un método
     * de huella digital definidos por el usuario.
     * @param tam el tamaño del diccionario.
     * @param huella la huella digital a utilizar.
     */
    public Diccionario(int tam, HuellaDigital<K> huella) {
        this.huella = huella;
        mascara = mascara(3*tam);
        entradas = nuevoArreglo(mascara+1);
        total = 0;
    }

    /**
     * Agrega un nuevo valor al diccionario, usando la llave
     * proporcionada. Si la llave ya había sido utilizada antes para
     * agregar un valor, el diccionario reemplaza ese valor con el
     * recibido aquí.
     * @param llave la llave para agregar el valor.
     * @param valor el valor a agregar.
     */
    public void agrega(K llave, V valor) {  
        int indice = indice(llave);
        Lista<Entrada<K,V>> lista = getLista(indice,true);
        Entrada<K,V> entrada = buscaEntrada(lista,llave);
        if(entrada != null)
            entrada.valor = valor;
        else{
            entrada = new Entrada<K,V>(llave,valor);
            lista.agregaFinal(entrada);
            total++;
        }
        if(carga() > MAXIMA_CARGA)  
            creceArreglo();
    }

    private int mascara(int n){
        int m = 1;
        while(m < n)
            m = (m << 1)|1;
        return m;
    }

    private int indice(K llave){
        return mascara & llave.hashCode();
    }

    private Lista<Entrada<K,V>> getLista(int indice,boolean crea){
        if(crea){
            if(entradas[indice] == null)
                entradas[indice] = new Lista<Entrada<K,V>>();
        }
        return entradas[indice];
    }

    private Entrada<K,V> buscaEntrada(Lista<Entrada<K,V>> lista , K llave){
        for(Entrada<K,V> e : lista){
            if(e.llave.equals(llave))
                return e;
        }
        return null;
    }

    public void creceArreglo(){
        Lista<K> l = llaves();
        Lista<V> v = valores();
        mascara = mascara(mascara<<1)|1;
        entradas = nuevoArreglo(mascara+1);
        for(K llave : l){
            for(V valor : v)
                agrega(llave,valor);
        } 
    }

    /**
     * Regresa el valor del diccionario asociado a la llave
     * proporcionada.
     * @param llave la llave para buscar el valor.
     * @return el valor correspondiente a la llave.
     * @throws <tt>NoSuchElementException</tt> si la llave no está
     *         en el diccionario.
     */
    public V get(K llave) {
        if(llave == null)
            return null;
        if(contiene(llave)){
             int i = indice(llave);
            for(Entrada<K,V> e : entradas[i]){
                if(llave.equals(e.llave))
                    return e.valor;
            }       
            return null;
        }else
             throw new NoSuchElementException();
    }

    /**
     * Nos dice si una llave se encuentra en el diccionario.
     * @param llave la llave que queremos ver si está en el diccionario.
     * @return <tt>true</tt> si la llave está en el diccionario,
     *         <tt>false</tt> en otro caso.
     */
    public boolean contiene(K llave) {
        int i = indice(llave);
        if(entradas[i] == null || i > entradas.length)
            return false;
        for(Entrada<K,V> e : entradas[i]){
            if(e.llave.equals(llave))
                return true;
        }   
        return false;
    }

    /**
     * Elimina el valor del diccionario asociado a la llave
     * proporcionada.
     * @param llave la llave para buscar el valor a eliminar.
     * @throws NoSuchElementException si la llave no se encuentra en
     *         el diccionario.
     */
    public void elimina(K llave) {
        if(contiene(llave)){
        int i = indice(llave);
        for(Entrada<K,V> e : entradas[i]){
            if(e.llave.equals(llave))
                entradas[i].elimina(e);
        }
        if(entradas[i].getLongitud() == 0)
            entradas[i] = null;
        total--;
        }else
            throw new NoSuchElementException();
    }

    /**
     * Regresa una lista con todas las llaves con valores asociados
     * en el diccionario. La lista no tiene ningún tipo de orden.
     * @return una lista con todas las llaves.
     */
    public Lista<K> llaves() {
        Lista<K> li = new Lista<K>();
        for(Lista<Entrada<K,V>> l : entradas){
            if(l != null){
                for(Entrada<K,V> e : l)
                    li.agregaFinal(e.llave);
            }
        }
        return li;
    }

    /**
     * Regresa una lista con todos los valores en el diccionario. La
     * lista no tiene ningún tipo de orden.
     * @return una lista con todos los valores.
     */
    public Lista<V> valores() {
        Lista<V> li = new Lista<V>();
        for(Lista<Entrada<K,V>> l : entradas){
            if(l != null){
                for(Entrada<K,V> e : l)
                    li.agregaFinal(e.valor);
            }
        }
        return li;
    }

    /**
     * Nos dice el máximo número de colisiones para una misma llave
     * que tenemos en el diccionario.
     * @return el máximo número de colisiones para una misma llave.
     */
    public int colisionMaxima() {
        int max = 0 , j = 0;
        for(int i = 0; i < entradas.length; i++){
            if(entradas[i] != null ){
                j = i+1;
                if(j < entradas.length && entradas[j] != null){
                    if(entradas[i].getLongitud() < entradas[j].getLongitud())
                        max = entradas[j].getLongitud();
                    else
                        max = entradas[i].getLongitud();
                }else
                    max = entradas[i].getLongitud();
            }
        }
        return max-1;
    }

    /**
     * Nos dice cuántas colisiones hay en el diccionario.
     * @return cuántas colisiones hay en el diccionario.
     */
    public int colisiones() {
        int colision = 0;
        for(Lista<Entrada<K,V>> lista : entradas){
            if(lista != null)
                colision += lista.getLongitud() - 1;
        }
        return colision;
    }

    /**
     * Nos dice la carga del diccionario.
     * @return la carga del diccionario.
     */
    public double carga() {
        return total/(mascara + 1.0);
    }

    /**
     * Regresa el número de valores en el diccionario.
     * @return el número de valores en el diccionario.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Regresa un iterador para iterar los valores del
     * diccionario. El diccionario se itera sin ningún orden
     * específico.
     * @return un iterador para iterar el diccionario.
     */
    @Override public Iterator<V> iterator() {
        return new Iterador<V>(this);
    }
}
