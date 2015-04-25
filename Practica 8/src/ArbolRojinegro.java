package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las
 * siguientes propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<tt>null</tt>) son NEGRAS (al igual que la
 *      raíz).
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguno de sus descendientes tiene
 *      el mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros son autobalanceados, y por lo tanto las
 * operaciones de inserción, eliminación y búsqueda pueden
 * realizarse en <i>O</i>(log <i>n</i>).
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método
     * {@link ArbolBinarioOrdenado#agrega}, y después balancea el
     * árbol recoloreando vértices y girando el árbol como sea
     * necesario.
     * @param elemento el elemento a agregar.
     * @return un vértice que contiene al nuevo elemento.
     */
    @Override public VerticeArbolBinario<T> agrega(T elemento) {

        Vertice<T> v = vertice(super.agrega(elemento));
        v.color = Color.ROJO;
        rebalanceoAgrega(v);
        return busca(elemento);
    }

    /**
     *  Método auxiliar para rebalancear el vértice recién agregado 
     *  y mantener las condiciones de un arbol RojiNegro válido
     */
    public void rebalanceoAgrega(Vertice<T> vertice) {
        if(vertice.padre == null) {
            vertice.color = Color.NEGRO;
            return;
        }
        if(vertice.padre.color == Color.NEGRO)
            return;

        Vertice<T> t = tio(vertice);
        if(t != null && t.color == Color.ROJO) {
            Vertice<T> a = abuelo(vertice);
            vertice.padre.color = Color.NEGRO;
            t.color = Color.NEGRO;
            a.color = Color.ROJO;
            rebalanceoAgrega(a);
            return;
        }

        Vertice<T> a = abuelo(vertice);
        if(vertice == vertice.padre.derecho && a.izquierdo == vertice.padre) {
            giraIzquierda(vertice.padre);
            vertice = vertice.izquierdo;
        }
        else if(vertice == vertice.padre.izquierdo && a.derecho == vertice.padre) {
            giraDerecha(vertice.padre);
            vertice = vertice.derecho;
        }

        a = abuelo(vertice);
        vertice.padre.color = Color.NEGRO;
        a.color = Color.ROJO;
        if(vertice.padre.derecho == vertice && a.derecho == vertice.padre)
            giraIzquierda(a);
        else if(vertice.padre.izquierdo == vertice && a.izquierdo == vertice.padre)
            giraDerecha(a);


    }



    private Vertice<T> tio(Vertice<T> vertice) {
        if(vertice == null) 
            return null;
        Vertice<T> a = abuelo(vertice);
        Vertice<T> v = null;
        if(a != null) {
            if(a.izquierdo == vertice.padre)
                v = a.derecho;
            else
                v = a.izquierdo;
        }
        return v;
    }

     private Vertice<T> abuelo(Vertice<T> vertice) {
        if(vertice == null)
            return null;
        if(vertice.padre == null)
            return null;
        else
        return vertice.padre.padre;
    }
    /**
     * Elimina un elemento del árbol. El método elimina el vértice que
     * contiene el elemento, y recolorea y gira el árbol como sea
     * necesario para rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        Vertice<T> v = vertice(busca(elemento));
        if(v == null)
            return;
        elimina(v);
        elementos--;
    }

    private void elimina(Vertice<T> v) {
        
        Vertice<T> a = buscaVerticeAnterior(v);

        if(a != null) {
            intercambia(v,a);
            v = a;
        }
        Vertice<T> h = new Vertice<T>(null);
        if(v.derecho == null && v.izquierdo == null) {
            h.color = Color.NEGRO;
            v.izquierdo = h;
        } 
        else if(v.derecho != null)
            h = v.derecho;
        else
            h = v.izquierdo;
        h.padre = v.padre;
        if(v.padre != null) {
            if(v.padre.izquierdo == v)
                v.padre.izquierdo = h;
            else
                v.padre.derecho = h;
        }
    
        else
            raiz = h;
    
        if( h.color == Color.ROJO) {
            h.color = Color.NEGRO;
            return;
        }
        if (v.color == Color.NEGRO && h.elemento != null) {
            h.color = Color.NEGRO;
            return;
        }

        if(v.color == Color.NEGRO && h.color == Color.NEGRO){
            rebalanceoElimina1(h);
            if(h.elemento == null)
                if(raiz == h)
                    raiz = h = null;
                else {
                    if(v.padre.derecho == h)
                        v.padre.derecho = null;
                    else
                        v.padre.izquierdo = null;
                }
            return;
            
        }
            if(h.elemento == null) 
                if(raiz == h)
                    raiz = h = null;
                else {
                    if(v.padre.izquierdo == h)
                        v.padre.izquierdo = null;
                    else
                        v.padre.derecho = null;
        
            }
        
    
    }

    
    
    private void rebalanceoElimina1(Vertice<T> v) {
        if(v.padre == null) {
            raiz = v;
            return;
        }
        else
            rebalanceoElimina2(v);
    }

    private void rebalanceoElimina2(Vertice<T> v) {
        Vertice<T> hermano = hermano(v);
        VerticeArbolBinario<T> z = hermano.getPadre(); 
        if(hermano.color == Color.ROJO) {
            v.padre.color = Color.ROJO;
            hermano.color = Color.NEGRO;
            if(v.padre.derecho == v)
                giraDerecha(v.padre);
            else
                giraIzquierda(v.padre);
        }
        rebalanceoElimina3(v);
    }

    private void rebalanceoElimina3(Vertice<T> v) {
        Vertice<T> hermano = hermano(v);
        if(hermano != null)
        if(v.padre.color == Color.NEGRO && hermano.color == Color.NEGRO && colorNegro(hermano.izquierdo) &&
            colorNegro(hermano.derecho)) {
                hermano.color = Color.ROJO;
                rebalanceoElimina1(v.padre);
        } 
        else
            rebalanceoElimina4(v);
    }

    private void rebalanceoElimina4(Vertice<T> v) {
        Vertice<T> hermano = hermano(v);
        
        if(v.padre.color == Color.ROJO && 
            hermano.color == Color.NEGRO && 
            colorNegro(hermano.izquierdo) && colorNegro(hermano.derecho)) {
                hermano.color = Color.ROJO;
                v.padre.color = Color.NEGRO;
            
        }
        else
            rebalanceoElimina5(v);
    }

    private void rebalanceoElimina5(Vertice<T> v) {
        Vertice<T> hermano = hermano(v);
    
        if(v.padre.izquierdo == v && 
            hermano.color == Color.NEGRO &&
            !colorNegro(hermano.izquierdo) && colorNegro(hermano.derecho)) {
            hermano.color = Color.ROJO;
            hermano.izquierdo.color = Color.NEGRO;
            giraDerecha(hermano);
        }
        else if (v.padre.derecho == v &&
            hermano.color == Color.NEGRO &&
            !colorNegro(hermano.derecho) && colorNegro(hermano.izquierdo)) {
            hermano.color = Color.ROJO;
            hermano.derecho.color = Color.NEGRO;
            giraIzquierda(hermano);
        }
        
        rebalanceoElimina6(v);
    }

    private void rebalanceoElimina6(Vertice<T> v) {
        Vertice<T> hermano = hermano(v);
     
        hermano.color = v.padre.color;
        v.padre.color = Color.NEGRO;
        if(v.padre.derecho == v) {
            hermano.izquierdo.color = Color.NEGRO;
            giraDerecha(v.padre);
        }
        else {
            hermano.derecho.color = Color.NEGRO;
            giraIzquierda(v.padre);
        }

    }

    
    private Vertice<T> hermano(Vertice<T> v) {
        if(v.padre.derecho == v)
            return v.padre.izquierdo;
        else
            return v.padre.derecho;
    }

    private boolean colorNegro(Vertice<T> v) {
        if(v == null || v.color == Color.NEGRO)
            return true;
        else
            return false;
    }

}
