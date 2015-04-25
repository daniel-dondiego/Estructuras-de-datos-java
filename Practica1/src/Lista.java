package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de
 * la lista, eliminar elementos de la lista, comprobar si un
 * elemento está o no en la lista, y otras operaciones básicas.</p>
 *
 * <p>Las instancias de la clase Lista implementan la interfaz
 * {@link Iterator}, por lo que el recorrerlas es muy sencillo:</p>
 *
<pre>
    for (String s : l)
        System.out.println(s);
</pre>
 *
 * <p>Además, se le puede pedir a una lista una instancia de {@link
 * IteradorLista} para recorrerla en ambas direcciones.</p>
 */
public class Lista<T> implements Iterable<T> {

    /* Clase Nodo privada para uso interno de la clase Lista. */
    private class Nodo<T> {
        public T elemento;
        public Nodo<T> anterior;
        public Nodo<T> siguiente;

        public Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    /* Clase Iterador privada para iteradores. */
    private class Iterador<T> implements IteradorLista<T> {

        /* La lista a iterar. */
        Lista<T> lista;
        /* Elemento anterior. */
        private Lista<T>.Nodo<T> anterior;
        /* Elemento siguiente. */
        private Lista<T>.Nodo<T> siguiente;

        /* El constructor recibe una lista para inicializar su
         * siguiente con la cabeza. */
        public Iterador(Lista<T> lista) {
        	this.lista = lista;
        	siguiente = lista.cabeza;
        	anterior =null;
        
        }

        /* Existe un siguiente elemento, si el siguiente no es
         * nulo. */
        @Override public boolean hasNext() {
            
            	return siguiente != null;
            
        }

        /* Regresa el elemento del siguiente, a menos que sea nulo,
         * en cuyo caso lanza la excepción
         * NoSuchElementException. */
        @Override public T next() {
            if(siguiente != null){
            	T t = siguiente.elemento;
            	anterior= siguiente;
            	siguiente= siguiente.siguiente;
            	return t;
            }
            else
            	throw new NoSuchElementException();
            
        }

        /* Existe un elemento anterior, si el anterior no es
         * nulo. */
        @Override public boolean hasPrevious() {
            
            	return anterior != null;
            
        }

        /* Regresa el elemento del anterior, a menos que sea nulo,
         * en cuyo caso lanza la excepción
         * NoSuchElementException. */
        @Override public T previous() {
            if(anterior != null){
            	T t = anterior.elemento;
            	siguiente = anterior;
            	anterior = anterior.anterior;

            return t;
        }
        else
        	throw new NoSuchElementException();

        }

        /* No implementamos el método remove(); sencillamente
         * lanzamos la excepción UnsupportedOperationException. */
        @Override public void remove() {
            throw new UnsupportedOperationException();
        }

        /* Mueve el iterador al inicio de la lista; después de
         * llamar este método, y si la lista no es vacía, hasNext()
         * regresa verdadero y next() regresa el primer elemento. */
        @Override public void start() {
           siguiente = lista.cabeza;
           anterior = null;
        }

        /* Mueve el iterador al final de la lista; después de llamar
         * este método, y si la lista no es vacía, hasPrevious()
         * regresa verdadero y previous() regresa el último
         * elemento. */
        @Override public void end() {
         anterior = lista.rabo;
         siguiente = null;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo<T> cabeza;
    /* Último elemento de la lista. */
    private Nodo<T> rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista.
     * @return la longitud de la lista, el número de elementos que
     * contiene.
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no
     * tiene elementos, el elemento a agregar será el primero y
     * último.
     * @param elemento el elemento a agregar.
     */
    public void agregaFinal(T elemento) {
       Nodo<T> n = new Nodo<T>(elemento);
       if(rabo==null){
       	cabeza = rabo = n;
       }
       else
       {
       	rabo.siguiente = n;
       	n.anterior =rabo;
       	rabo = n;
       }
       longitud++;
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no
     * tiene elementos, el elemento a agregar será el primero y
     * último.
     * @param elemento el elemento a agregar.
     */
    public void agregaInicio(T elemento) {
    	Nodo<T> n = new Nodo<T>(elemento);
        if(cabeza == null){
        	cabeza = rabo = n;

        }
        else
        {
        	cabeza.anterior = n;
        	n.siguiente = cabeza;
        	cabeza = n;
        }
        longitud++;
    }
    /**
    *Método auxiliar, busca un nodo
    *@param nodo 
    *@param elemento
    *@return Nodo
    */
    public Nodo<T> buscaNodo(Nodo<T> nodo, T elemento){
        if(nodo == null)
            return null;
        else
            if(nodo.elemento.equals(elemento))
                return nodo;
            else
            return buscaNodo(nodo.siguiente , elemento);
    }
    /**
     * Elimina un elemento de la lista. Si el elemento no está
     * contenido en la lista, el método no la modifica.
     * @param elemento el elemento a eliminar.
     */
    public void elimina(T elemento) {
        
        Nodo<T> n = buscaNodo(cabeza, elemento);
        if(n == null){
           return;
        }
        
        else  
            if(cabeza == rabo){
                cabeza = rabo = null;
            }
            else
                if(n == cabeza){
                    cabeza = cabeza.siguiente;
                    cabeza.anterior = null;
                }
                else
                    if(n == rabo){
                        rabo = rabo.anterior;
                        rabo.siguiente = null;
                    }
                    else{
                        n.anterior.siguiente = n.siguiente;
                        n.siguiente.anterior = n.anterior;

                    }
                    longitud--;
                    
    }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
        if(cabeza == null){
            throw new NoSuchElementException();
        }
        
        else 
            
            if(cabeza == rabo){
                T t = cabeza.elemento;
        longitud--;
                cabeza = rabo = null;
                return t;
            }
            
        else{
          T t = cabeza.elemento;
        longitud--;
            cabeza = cabeza.siguiente;
            cabeza.anterior = null;
            return t;
        }
    
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
        
        if(rabo == null){
            throw new NoSuchElementException();
        }
        
        else 
            
            if(cabeza == rabo){
                T t = rabo.elemento;
        longitud--;
                cabeza = rabo = null;
                return t;
            }
            
        else{
          T t = rabo.elemento;
        longitud--;
            rabo = rabo.anterior;
            rabo.siguiente = null;
            return t;
        }
        
    }

    /**
     * Nos dice si un elemento está en la lista.
     * @param elemento el elemento que queremos saber si está en la
     * lista.
     * @return <tt>true</tt> si <tt>elemento</tt> está en la lista,
     *         <tt>false</tt> en otro caso.
     */
    public boolean contiene(T elemento) {
      Nodo<T> n = buscaNodo(cabeza, elemento);
      
            return n!= null; 
        
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar
     *         el método.
     */
    public Lista<T> reversa() {
        Nodo<T> n = cabeza;
        Lista<T> l = new Lista<T>();
        if(n == null)
            return l;
 
        else
            while(n != null){
            l.agregaInicio(n.elemento);
            n = n.siguiente;
}
       return l;
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos
     * elementos que la lista que manda llamar el método, en el
     * mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
        Nodo<T> n = cabeza;
        Lista<T> l = new Lista<T>();
        if(n == null)
        return null;
    else
        while(n != null){
            l.agregaFinal(n.elemento);
            n = n.siguiente;
            
        }
        return l;
    }

    /**
     * Limpia la lista de elementos. El llamar este método es
     * equivalente a eliminar todos los elementos de la lista.
     */
    public void limpia() {
        cabeza = rabo = null;
        longitud = 0;
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
        if(cabeza == null)
            throw new NoSuchElementException();
        else
        return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el último elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
        if(rabo == null)
            throw new NoSuchElementException();

        else
        return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista, si
     *         <em>i</em> es mayor o igual que cero y menor que el
     *         número de elementos en la lista.
     * @throws ExcepcionIndiceInvalido si el índice recibido es
     *         menor que cero, o mayor que el número de elementos en
     *         la lista menos uno.
     */
    public T get(int i) {
        T t = null;
        Nodo<T> n = cabeza;
        int x = 0;
        if(i< 0 || i > getLongitud()-1)
            throw new ExcepcionIndiceInvalido();
        
        else
            if(i == 0){
                return cabeza.elemento;
            }
            else 
                if(i == getLongitud()-1){
                    return rabo.elemento;
                }
            
            while(x<i) {
            if(n != null)
                   n = n.siguiente;
                x++;
            }  
             t = n.elemento;

        return t;
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si
     *         el elemento no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
        int i = 0;
        Nodo<T> n = cabeza;
        if(n == null)
            return -1;

        while(n != null){
            if(n.elemento.equals(elemento)){
                return i;
            }
            else{
            i++;
                n = n.siguiente;
            }
        }
        return -1;
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param o el objeto con el que hay que comparar.
     * @return <tt>true</tt> si la lista es igual al objeto
     *         recibido; <tt>false</tt> en otro caso.
     */
    @Override public boolean equals(Object o) {
       if(o == null){
            return false;
        }
        else
            if(getClass() != o.getClass()){
                return false;
            }
            @SuppressWarnings("unchecked") Lista<T> l = (Lista<T>) o;
      
         return sonIguales(cabeza, l.cabeza);
    }

    private boolean sonIguales(Nodo<T> n1, Nodo<T> n2){
        if(n1 == null && n2 == null){
            return true;
        }
        else
            if(n1 == null || n2 == null){
            return false;
        }
        else
            return n1.elemento.equals(n2.elemento) && sonIguales(n1.siguiente, n2.siguiente);

    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
            
            if(cabeza == null)
                return "[]";
            Nodo<T> n = cabeza.siguiente;
            String cadena = "";
            while(n != null){
                cadena += ", " + n.elemento;
                n  = n.siguiente;
            }
            return "[" + cabeza.elemento + cadena + "]";
    }
   

    /**
     * Regresa un iterador para recorrer la lista.
     * @return un iterador para recorrer la lista.
     */
    @Override public Iterator<T> iterator() {
    
        return iteradorLista();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas
     * direcciones.
     * @return un iterador para recorrer la lista en ambas
     * direcciones.
     */
    public IteradorLista<T> iteradorLista() {
    
        return new Iterador<T>(this);
    }
}
