package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>). Podemos crear
 * un montículo mínimo con <em>n</em> elementos en tiempo
 * <em>O</em>(<em>n</em>), y podemos agregar y actualizar elementos
 * en tiempo <em>O</em>(log <em>n</em>). Eliminar el elemento mínimo
 * también nos toma tiempo <em>O</em>(log <em>n</em>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Iterable<T> {

    /* Clase privada para iteradores de montículos. */
    private class Iterador<T extends ComparableIndexable<T>> implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;
        private MonticuloMinimo<T> monticulo;

        /* Construye un nuevo iterador, auxiliándose del montículo
         * mínimo. */
        public Iterador(MonticuloMinimo<T> monticulo) {
            this.monticulo = monticulo;
            indice = 0;   
        }

        /* Nos dice si hay un siguiente elemento. */
        public boolean hasNext() {
            return indice < monticulo.siguiente;
        }

        /* Regresa el siguiente elemento. */
        public T next() {
            if(!hasNext())
            	return null;

                return monticulo.arbol[indice++];
            
        }

        /* No lo implementamos: siempre lanza una excepción. */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private int siguiente;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así
       por cómo Java implementa sus genéricos; de otra forma
       obtenemos advertencias del compilador. */
    @SuppressWarnings("unchecked") private T[] creaArregloGenerico(int n) {
        return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Lista)}, pero se ofrece este constructor por
     * completez.
     */
    public MonticuloMinimo() {
    	siguiente = 0;
        arbol = creaArregloGenerico(1);
    }

    /**
     * Constructor para montículo mínimo que recibe una lista. Es
     * más barato construir un montículo con todos sus elementos de
     * antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos
     * uno por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     */
    public MonticuloMinimo(Lista<T> lista) {
        arbol = creaArregloGenerico(lista.getLongitud());
        siguiente = 0;
        for(T n : lista){

        	 n.setIndice(siguiente);
        	arbol[siguiente++] = n;
            }
        aux(siguiente/2);

    }

    private int obtenerMinimo(int i, int izq, int der) {
        
        if(siguiente > der) {
            if(arbol[der].compareTo(arbol[izq]) < 0) {
                if(arbol[der].compareTo(arbol[i]) < 0)
                    return der;
                return i;
            }
            else if(arbol[izq].compareTo(arbol[i]) < 0)
                    return izq;
            return i;

        } 
        else if(siguiente > izq)
            if(arbol[izq].compareTo(arbol[i]) < 0)
                return izq;
        return i;
    }

    private void intercambia(int i, int j) {
        T n = arbol[i];
        arbol[i] = arbol[j];
        arbol[i].setIndice(i);
        arbol[j] = n;
        arbol[j].setIndice(j);
    }


private void aux(int i) {
        int min = obtenerMinimo(i,(2*i)+1,(2*i)+2);
        if(min == i) {
            if(i-1 >= 0)
                aux(i-1);
            return;
        } 
        intercambia(i,min);
        aux(min);
    }

    private void auxiliar(int i) {
        int min = obtenerMinimo(i,(2*i)+1,(2*i)+2);
        if(min == i) {
            return;
        } 
        intercambia(i,min);
        auxiliar(min);
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    public void agrega(T elemento) {
        if(siguiente+1 > arbol.length) {
        T[] nuevoA = creaArregloGenerico(arbol.length*2);
            for(int i = 0; i < siguiente; i++) 
                nuevoA[i] = arbol[i];
            	arbol = nuevoA;
        } 
        elemento.setIndice(siguiente);  
        arbol[siguiente] = elemento;
        siguiente++;
        reordena(elemento);
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    public T elimina() {
        	if(esVacio())
            throw new IllegalStateException();
        T n = arbol[0];
        arbol[0] = arbol[siguiente-1];
        arbol[0].setIndice(0);
        auxiliar(0);
        siguiente--;
        return n;
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <tt>true</tt> si ya no hay elementos en el montículo,
     *         <tt>false</tt> en otro caso.
     */
    public boolean esVacio() {
        return siguiente == 0;
    }

   /**
     * Reordena un elemento en el árbol.
     * @param elemento el elemento que hay que reordenar.
     */
    public void reordena(T elemento) {
        int n = elemento.getIndice();
        int p = (n-1)/2;
        if(n == 0 || p < 0)
            return;
        if(arbol[p].compareTo(arbol[n]) > 0) {
            intercambia(p,n);
            reordena(arbol[p]);
        }
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    public int getElementos() {
        return siguiente;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o
     *         mayor o igual que el número de elementos.
     */
    public T get(int i) {
        if(i >= siguiente||i < 0)
            throw new NoSuchElementException();
        return arbol[i];
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El
     * montículo se itera en orden BFS.
     * @return un iterador para iterar el montículo mínimo.
     */
    public Iterator<T> iterator() {
        return new Iterador<T>(this);
    }
}
