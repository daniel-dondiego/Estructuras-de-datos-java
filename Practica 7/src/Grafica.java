package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y
 * aristas, tales que las aristas son un subconjunto del producto
 * cruz de los vértices.
 */
public class Grafica<T> implements Iterable<T> {

    /* Clase privada para iteradores de gráficas. */
    private class Iterador<T> implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Grafica<T>.Vertice<T>> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de
         * vértices. */
        public Iterador(Grafica<T> grafica) {
            
            iterador = grafica.vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        public T next() {
            return iterador.next().elemento;
        }

        /* No lo implementamos: siempre lanza una excepción. */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* Vertices para gráficas; implementan la interfaz
     * VerticeGrafica */
    private class Vertice<T> implements VerticeGrafica<T> {

        public T elemento;
        public Color color;
        public Lista<Grafica<T>.Vertice<T>> vecinos;
        public IteradorLista<Grafica<T>.Vertice<T>> iterador;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
         this.elemento = elemento;
         color = Color.NINGUNO;
         vecinos = new Lista<Grafica<T>.Vertice<T>>();

        }

        /* Crea un nuevo iterador para los vecinos, si no existe, o
         * lo mueve al inico. */
        public void inicio() {
            if(iterador == null) 
                iterador = vecinos.iteradorLista(); 
            else 
                iterador.start();
        }

        /* Hay un vecino si el iterador tiene un siguiente. */
        public boolean hayVecino() {
            return  iterador.hasNext();
        }

        /* Regresa el siguiente vecino. */
        public VerticeGrafica<T> vecino() {
            Grafica<T>.Vertice<T> n = iterador.next();
             n.inicio(); 
             return n;
        }

        /* Regresa el elemento del vértice. */
        public T getElemento() {
                return elemento;
        }

        /* Regresa el grado del vértice. */
        public int getGrado() { 
            return vecinos.getLongitud();
       }

        /* Regresa el color del vértice. */
        public Color getColor() {
            return color;
        }

        /* Define el color del vértice. */
        public void setColor(Color color) {
            this.color = color;
        }
    }

    /* Vértices. */
    private Lista<Vertice<T>> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Lista<Vertice<T>>();
        aristas = 0;
    }

    /**
     * Regresa el número de vértices.
     * @return el número de vértices.
     */
    public int getVertices() {
        return vertices.getLongitud();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /* Método auxiliar para buscar vértices. */
    private Vertice<T> buscaVertice(T elemento) {
        for (Vertice<T> v: vertices){
            if(v.elemento.equals(elemento))
                return v;
        }
        return null;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido
     *         agregado a la gráfica.
     */
    public void agrega(T elemento) {
        if(contiene(elemento))
            throw new IllegalArgumentException();
        vertices.agregaFinal(new Vertice<T>(elemento));

    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben
     * estar en la gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     * @throws IllegalArgumentException si a o b ya están
     *         conectados, o si a es igual a b.
     */
    public void conecta(T a, T b) {
        if(!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if(sonVecinos(a,b) || a.equals(b))
            throw new IllegalArgumentException();
        Vertice<T> vA = buscaVertice(a);
        Vertice<T> vB = buscaVertice(b);
        vA.vecinos.agregaFinal(vB);
        vB.vecinos.agregaFinal(vA);
        aristas++;

    }


    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben
     * estar en la gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     * @throws IllegalArgumentException si a o b no están
     *         conectados.
     */
    public void desconecta(T a, T b) {
        if(!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if(!sonVecinos(a,b) || a.equals(b))
            throw new IllegalArgumentException();
        Vertice<T> vA = buscaVertice(a);
        Vertice<T> vB = buscaVertice(b);
        vA.vecinos.elimina(vB);
        vB.vecinos.elimina(vA);
        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <tt>true</tt> si el elemento está contenido en la
     *         gráfica, <tt>false</tt> en otro caso.
     */
    public boolean contiene(T elemento) {

        return buscaVertice(elemento) != null;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que
     * estar contenido en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está
     *         contenido en la gráfica.
     */
    public void elimina(T elemento) {
        Vertice<T> ve = buscaVertice(elemento);
            if(ve != null){
            for(Vertice<T> v : ve.vecinos)
                desconecta(v.elemento,ve.elemento);
            vertices.elimina(ve);
        }else
            throw new NoSuchElementException();

    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los
     * elementos deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <tt>true</tt> si a y b son vecinos, <tt>false</tt> en
     *         otro caso.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        if(!contiene(a) || !contiene(b))
        throw new NoSuchElementException();
    Vertice<T> vA = buscaVertice(a);
    Vertice<T> vB = buscaVertice(b);
    if(vA.vecinos.contiene(vB))
        return true;
    return false;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @throws NoSuchElementException si elemento no es elemento de
     *         la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        Vertice<T> v = buscaVertice(elemento); 
        if(v == null) 
            throw new NoSuchElementException(); 
        else
        v.inicio(); 
        return v;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la
     * gráfica, en el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
            for(Vertice<T> v : vertices)
            accion.actua(v);
    }

    private void recorridos(Vertice<T> v, AccionVerticeGrafica<T> accion, MeteSaca<Vertice<T>> pc){
        for(Vertice<T> v2 : vertices)
            v2.color = Color.NEGRO;
        pc.mete(v);
        v.color = Color.ROJO;
         while(!pc.esVacia()){
            v = pc.saca();
            accion.actua(v);
            /* Metemos a los vecinos del vértice actual y los marcamos.*/
            for(Vertice<T> i : v.vecinos){
                if(i.color == Color.NEGRO){
                    pc.mete(i);
                    i.color = Color.ROJO;
                }
            }
        }
        for(Vertice<T> v1 : vertices)
            v1.color = Color.NINGUNO;
    }

    /**
     * Realiza la acción recibida en todos los vértices de la
     * gráfica, en el orden determinado por BFS, comenzando por el
     * vértice correspondiente al elemento recibido. Al terminar el
     * método, todos los vértices tendrán color {@link
     * Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos
     *        comenzar el recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la
     *         gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        Vertice<T> v = buscaVertice(elemento);
        if(v == null)
            throw new NoSuchElementException();
        Cola<Vertice<T>> cola = new Cola<Vertice<T>>();
        recorridos(v,accion,cola);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la
     * gráfica, en el orden determinado por DFS, comenzando por el
     * vértice correspondiente al elemento recibido. Al terminar el
     * método, todos los vértices tendrán color {@link
     * Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos
     *        comenzar el recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la
     *         gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
         Vertice<T> v = buscaVertice(elemento);
        if(v == null)
            throw new NoSuchElementException();
        Pila<Vertice<T>> pila = new Pila<Vertice<T>>();
        recorridos(v,accion,pila);
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se
     * itera en el orden en que fueron agregados sus elementos.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
            return new Iterador<T>(this);
    }
}
