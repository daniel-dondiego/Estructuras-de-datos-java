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

    /* Aristas para gráficas; para poder guardar el peso de las
     * aristas. */
    private class Arista<T> {

        /* El vecino del vértice. */
        public Grafica<T>.Vertice<T> vecino;
        /* El peso de arista conectando al vértice con el vecino. */
        public double peso;

        public Arista(Grafica<T>.Vertice<T> vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }
    }

    /* Vertices para gráficas; implementan la interfaz
     * ComparableIndexable y VerticeGrafica */
    private class Vertice<T> implements ComparableIndexable<Vertice<T>>,
        VerticeGrafica<T> {

        /* Iterador para las vecinos del vértice. */
        private class IteradorVecinos implements Iterator<VerticeGrafica<T>> {

            /* Iterador auxiliar. */
            private Iterator<Grafica<T>.Arista<T>> iterador;
            
            /* Construye un nuevo iterador, auxiliándose de la lista
             * de vecinos. */
            public IteradorVecinos(Iterator<Grafica<T>.Arista<T>> iterador) {
                this.iterador = iterador;
            }

            /* Nos dice si hay un siguiente vecino. */
            public boolean hasNext() {
                return iterador.hasNext();
            }

            /* Regresa el siguiente vecino. La audición es
             * inevitable. */
            public VerticeGrafica<T> next() {
                Grafica<T>.Arista<T> arista = iterador.next();
                return (VerticeGrafica<T>)arista.vecino;
            }

            /* No lo implementamos: siempre lanza una excepción. */
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La distancia del vértice. */
        public double distancia;
        /* El índice del vértice. */
        public int indice;
        /* La lista de aristas que conectan al vértice con sus
         * vecinos. */
        public Lista<Grafica<T>.Arista<T>> aristas;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento;
            color = Color.NINGUNO;
            aristas = new Lista<Grafica<T>.Arista<T>>();
        }

        /* Regresa el elemento del vértice. */
        public T getElemento() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        public int getGrado() {
            return aristas.getLongitud();
        }

        /* Regresa el color del vértice. */
        public Color getColor() {
            return color;
        }

        /* Define el color del vértice. */
        public void setColor(Color color) {
            this.color = color;
        }

        /* Regresa un iterador para los vecinos. */
        public Iterator<VerticeGrafica<T>> iterator() {
            return new IteradorVecinos(aristas.iterator());
        }

        /* Define el índice del vértice. */
        public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        public int getIndice() {
            return indice;
        }

        /* Compara dos vértices por distancia. */
        public int compareTo(Vertice<T> vertice) {
            if(distancia == vertice.distancia)
                return 0;
            if(distancia > vertice.distancia)
                return 1;
                return -1;

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
     * estar en la gráfica. El peso de la arista que conecte a los
     * elementos será 1.
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
        Vertice<T> verticeA = buscaVertice(a);
        Vertice<T> verticeB = buscaVertice(b);
        verticeA.aristas.agregaFinal(new Arista<T>(verticeB, 1));
        verticeB.aristas.agregaFinal(new Arista<T>(verticeA, 1));
        aristas++;
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben
     * estar en la gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva arista.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     * @throws IllegalArgumentException si a o b ya están
     *         conectados, o si a es igual a b.
     */
    public void conecta(T a, T b, double peso) {
        if(!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if(sonVecinos(a,b) || a.equals(b))
            throw new IllegalArgumentException();
        Vertice<T> verticeA = buscaVertice(a);
        Vertice<T> verticeB = buscaVertice(b);
        verticeA.aristas.agregaFinal(new Arista<T>(verticeB, peso));
        verticeB.aristas.agregaFinal(new Arista<T>(verticeA, peso));
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
        if(!sonVecinos(a,b))
            throw new IllegalArgumentException();
        Vertice<T> verticeA = buscaVertice(a);
        Vertice<T> verticeB = buscaVertice(b);
        for (Arista<T> arista : verticeA.aristas) {
            if(arista.vecino == verticeB)
                verticeA.aristas.elimina(arista);
        }
        
        for (Arista<T> arista : verticeB.aristas) {
            if(arista.vecino == verticeA)
                verticeB.aristas.elimina(arista);
        }
        
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
        Vertice<T> v = buscaVertice(elemento); 
        if(v == null) throw new NoSuchElementException(); 
        for(Arista<T> arista : v.aristas)
            desconecta(arista.vecino.elemento , elemento); 
        vertices.elimina(v);
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
        Vertice<T> verticeA = buscaVertice(a);
        Vertice<T> verticeB = buscaVertice(b);
        for (Arista<T> arista : verticeA.aristas) {
            if(arista.vecino == verticeB)
                return true;
        }
        return false;
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que
     * contienen a los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que
     *         contienen a los elementos recibidos, o -1 si los
     *         elementos no están conectados.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     */
    public double getPeso(T a, T b) {
        if(!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if(sonVecinos(a,b)){
        Vertice<T> verticeA = buscaVertice(a);
        Vertice<T> verticeB = buscaVertice(b);
        for (Arista<T> arista : verticeA.aristas) {
            if(arista.vecino == verticeB)
                return arista.peso;
        }
    }
return -1;
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
        
        //v.inicio(); 
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

    private void recorridos(Vertice<T> v,AccionVerticeGrafica<T> accion,MeteSaca<Vertice<T>> pc){ 
    for(Vertice<T> v2 : vertices) 
        v2.color = Color.NEGRO; 
    pc.mete(v); v.color = Color.ROJO; 
    while(!pc.esVacia()){ 
        v = pc.saca(); accion.actua(v); 
         for(Arista<T> a : v.aristas){ 
            if(a.vecino.color == Color.NEGRO){ 
                pc.mete(a.vecino); a.vecino.color = Color.ROJO; 
            } } } 
            for(Vertice<T> v1 : vertices)
                v1.color = Color.ROJO;
                } 

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se
     * itera en el orden en que fueron agregados sus elementos.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador<T>(this);
    }

    /**
     * Calcula una trayectoria de distancia mínima entre dos
     * vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman
     *         una trayectoria de distancia mínima entre los
     *         vértices <tt>a</tt> y <tt>b</tt>. Si los elementos se
     *         encuentran en componentes conexos distintos, el
     *         algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos
     *         no está en la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        Lista<VerticeGrafica<T>> lista = new Lista<>();
        /* nunca se sabe.... */
        if (origen.equals(destino)) 
            return lista;
        Vertice<T> vOrigen = buscaVertice(origen);
        Vertice<T> vDestino = buscaVertice(destino);
        if(vOrigen == null || vDestino == null)
            throw new NoSuchElementException();
       actualizarDistancia(vOrigen, vDestino);
       vDestino.distancia = 0;
       MonticuloMinimo<Vertice<T>> heap = new MonticuloMinimo<Vertice<T>>(vertices);
       return generarTrayecto(vDestino, heap);
    }

    private Lista<VerticeGrafica<T>> generarTrayecto( Vertice<T> destino, MonticuloMinimo<Vertice<T>> heap) {
        Lista<VerticeGrafica<T>> lista =  new Lista<>();
        int ultimaDistancia = -1;
        while(!heap.esVacio()) {
            Vertice<T> v = heap.elimina();
            if(v.distancia > ultimaDistancia) {
                lista.agregaInicio(v);
                ultimaDistancia = (int) v.distancia;
            }
        }
        return lista;
    }

 private void actualizarDistancia(Vertice<T> origen, Vertice<T> destino) {
        final Vertice<T> d = destino;
        bfs(origen.elemento, new AccionVerticeGrafica<T>() {
            public void actua(VerticeGrafica<T> v) {
                Vertice<T> a = (Vertice<T>) v;
                a.distancia = distancia(a, d);
            }
        });
    }
private int distancia(Vertice<T> origen, Vertice<T> destino) {
        for (Arista<T> a: origen.aristas) 
            if (a.vecino.equals(destino))
                return 1;
            else
                origen = a.vecino;
 
       return 1 + distancia(origen,destino);
    }
    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y
     * el elemento de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice
     *         <tt>origen</tt> y el vértice <tt>destino</tt>. Si los
     *         vértices están en componentes conexas distintas,
     *         regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos
     *         no está en la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
        Vertice<T> v1 = buscaVertice(origen); 
Vertice<T> v2 = buscaVertice(destino); 
Lista<VerticeGrafica<T>> l = new Lista<VerticeGrafica<T>>(); 
if(v1 != null && v2 != null){ 
    for(Vertice<T> v:vertices){ 
        v.distancia = Double.POSITIVE_INFINITY; 
    } 
    v1.distancia = 0; 
    MonticuloMinimo<Vertice<T>> m = new MonticuloMinimo<Vertice<T>>(vertices); 
    while(!m.esVacio()){ 
        Vertice<T> actual = m.elimina(); 
        for(Arista<T> i:actual.aristas){ 
            if(actual.distancia+i.peso < i.vecino.distancia){ 
                i.vecino.distancia = actual.distancia+i.peso; m.reordena(i.vecino); 
            } } } 
            for(Vertice<T> v:vertices){ 
                if(v.distancia == Double.POSITIVE_INFINITY &&v.elemento.equals(destino)) return l; 
            } regreso(l,v2); 
            return l; 
        } else throw new NoSuchElementException(); 
    }
    
    private void regreso(Lista<VerticeGrafica<T>> l,Vertice<T> v){ 
        l.agregaInicio(v); 
        if(v.distancia == 0) 
            return; 
        for(Arista<T> a:v.aristas){ 
            if(v.distancia-a.peso == a.vecino.distancia) regreso(l,a.vecino); }
        }
}
