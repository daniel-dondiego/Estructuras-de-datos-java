package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal
 * forma que el árbol siempre es lo más cercano posible a estar
 * lleno.<p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase privada para iteradores de árboles binarios
     * completos. */
    private class Iterador<T> implements Iterator<T> {
        Cola<VerticeArbolBinario<T>> c = new Cola<VerticeArbolBinario<T>>();

        /* Constructor que recibe la raíz del árbol. */
        public Iterador(ArbolBinario<T>.Vertice<T> raiz) {
            c.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            if(c.esVacia())
                return false;
                return true;
        }

        /* Regresa el elemento siguiente. */
        @Override public T next() {
            if(hasNext()){
                VerticeArbolBinario<T> v = c.saca();
                if(v.hayIzquierdo())
                    c.mete(v.getIzquierdo());
                if(v.hayDerecho())
                    c.mete(v.getDerecho());
                return v.get();
            }else
                throw new NoSuchElementException();
        }

        /* No lo implementamos: siempre lanza una excepción. */
        @Override public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Constructor sin parámetros. Sencillamente ejecuta el
     * constructor sin parámetros de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { 
        super(); 
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo
     * elemento se coloca a la derecha del último nivel, o a la
     * izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @return un iterador que apunta al vértice del árbol que
     *         contiene el elemento.
     */
    @Override public VerticeArbolBinario<T> agrega(T elemento) {
        Vertice<T> vT = new Vertice<T>(elemento);
        if(raiz == null){
            raiz = vT;
            elementos++;
            return raiz;
        }
        Cola<Vertice<T>> c = new Cola<Vertice<T>>();
        Vertice<T> v = raiz, v1 = null;
        c.mete(v);
        while(!c.esVacia()){
            v = c.saca();
            if(v.izquierdo == null){
                v.izquierdo = new Vertice<T>(elemento);
                v.izquierdo.padre = v;
                elementos++;
                v1 = v.izquierdo;
                break;
            }
            if(v.izquierdo != null && v.derecho == null){
                v.derecho = new Vertice<T>(elemento);
                v.derecho.padre = v;
                elementos++;
                v1= v.derecho;
                break;
            }
                c.mete(v.izquierdo);
                c.mete(v.derecho);
        }return v1;
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia
     * lugares con el último elemento del árbol al recorrerlo por
     * BFS, y entonces es eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        if(raiz == null)
            return;
        
        VerticeArbolBinario<T> vAB = busca(elemento);
        Vertice<T> v = null, v1 = vertice(vAB);
        Cola<Vertice<T>> c = new Cola<Vertice<T>>();
        c.mete(raiz);
        while(!c.esVacia()){
          v = c.saca();
          if(v.izquierdo != null)
          c.mete(v.izquierdo);
          if(v.derecho != null)
          c.mete(v.derecho);
        }
        if(v1 == null)
            return;
            if(v != raiz){
                v1.elemento = v.elemento;
            if(v.padre.izquierdo.equals(v))
                v.padre.izquierdo = null;
            else
                v.padre.derecho = null;
                v.padre = null;
        } else {
            raiz = null;
    }
    elementos--;
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera
     * en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador<T>(vertice(raiz));
    }
}
