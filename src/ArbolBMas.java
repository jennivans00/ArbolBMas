import java.util.ArrayList;

/*
 * Árbol B+ de orden m.
 * - Cada nodo puede tener entre m/2 (redondeado hacia arriba) y m hijos, excepto la raíz,
 *   que puede tener entre 2 y m hijos.
 * - Por lo tanto cada nodo guarda como máximo m - 1 claves y como mínimo ceil(m/2) - 1
 *   (la raíz no tiene mínimo). Para las hojas se usan esos mismos límites.
 * - Los datos solo se guardan en las hojas y las hojas están enlazadas de izquierda a derecha.
 * - Las claves son únicas, igual que en un índice de una base de datos.
 */
public class ArbolBMas {

    // Atributos
    private final int orden;
    private NodoArbolBMas raiz;

    // Métodos
    // Constructor
    // Con orden menor que 3 el árbol no tiene sentido (un nodo podría quedar con 0 claves)
    public ArbolBMas(int orden) {
        if (orden < 3) {
            throw new IllegalArgumentException("El orden del árbol debe ser al menos 3.");
        }
        this.orden = orden;
        raiz = new NodoArbolBMas(true);
    }

    public int getOrden() {
        return orden;
    }

    // El árbol está vacío cuando la raíz es una hoja sin claves
    public boolean estaVacio() {
        return raiz.esHoja() && raiz.getClaves().isEmpty();
    }

    // Cantidad mínima de claves que debe tener un nodo que no es la raíz: ceil(m/2) - 1
    private int minimoClaves() {
        return (orden + 1) / 2 - 1;
    }

    // Devuelve la posición del hijo por el que hay que bajar para llegar a la clave.
    // Si la clave es igual a un separador se baja por la derecha, porque al dividir una hoja
    // el separador que sube es la primera clave de la hoja nueva (la de la derecha).
    private int indiceHijo(NodoArbolBMas nodo, int clave) {
        int i = 0;
        while (i < nodo.getClaves().size() && clave >= nodo.getClaves().get(i)) {
            i++;
        }
        return i;
    }

    // Baja desde la raíz hasta la hoja donde está (o debería estar) la clave
    private NodoArbolBMas buscarHoja(int clave) {
        NodoArbolBMas actual = raiz;
        while (!actual.esHoja()) {
            actual = actual.getHijos().get(indiceHijo(actual, clave));
        }
        return actual;
    }

    // Devuelve la hoja de más a la izquierda, que es donde empieza la lista enlazada de hojas
    private NodoArbolBMas hojaMasIzquierda() {
        NodoArbolBMas actual = raiz;
        while (!actual.esHoja()) {
            actual = actual.getHijos().getFirst();
        }
        return actual;
    }

    // ===================== INSERCIÓN =====================

    // Inserción de una clave con su dato en el árbol B+
    // Devuelve false si la clave ya existía (no se permiten claves repetidas)
    public boolean insertar(int clave, String dato) {
        if (buscar(clave)) {
            return false;
        }

        // Se inserta primero de forma recursiva
        insertarRecursivo(this.raiz, clave, dato);

        // Se verifica si la raíz se desbordó. Si pasa, se crea una raíz nueva y el árbol crece un nivel
        if (this.raiz.getClaves().size() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
        return true;
    }

    // Inserción recursiva de una clave
    // Permite que los nodos se desborden temporalmente y los divide al regresar de la recursión
    private void insertarRecursivo(NodoArbolBMas nodo, int clave, String dato) {
        if (nodo.esHoja()) {
            // Se busca la posición para que la hoja siga ordenada.
            // La clave y su dato se guardan en la misma posición de sus listas.
            int posicion = 0;
            while (posicion < nodo.getClaves().size() && clave > nodo.getClaves().get(posicion)) {
                posicion++;
            }
            nodo.getClaves().add(posicion, clave);
            nodo.getDatos().add(posicion, dato);
        } else {
            int i = indiceHijo(nodo, clave);
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            // Llamada recursiva para insertar la clave primero en el hijo correspondiente
            insertarRecursivo(hijo, clave, dato);

            // Evaluación de desbordamiento al retornar de la recursividad
            if (hijo.getClaves().size() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    // Dividir un nodo que está lleno
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;

        if (nodoLleno.esHoja()) {
            // En nodos hoja, la clave media se queda en la hoja nueva y una copia sube al padre.
            // Los datos se reparten igual que las claves para no perder la correspondencia.
            int total = nodoLleno.getClaves().size();
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, total)));
            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, total)));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            // La hoja nueva queda entre la hoja dividida y la que antes era su siguiente
            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            // El padre recibe como separador la primera clave del nuevo nodo
            int claveMedia = nuevoNodo.getClaves().getFirst();
            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        } else {
            // En nodos internos, la clave media sube y se elimina del hijo
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            // Mover los hijos del nodo dividido
            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
    }

    // ===================== BÚSQUEDA =====================

    // Búsqueda de una clave como parte de la interfaz pública del árbol.
    // En un B+ siempre hay que llegar a la hoja, porque las claves de los nodos internos
    // son solo guías y no garantizan que el elemento exista.
    public boolean buscar(int clave) {
        return buscarHoja(clave).getClaves().contains(clave);
    }

    // Devuelve el dato asociado a una clave, o null si la clave no está en el árbol
    public String obtenerDato(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int posicion = hoja.getClaves().indexOf(clave);
        return posicion == -1 ? null : hoja.getDatos().get(posicion);
    }

    // ===================== ELIMINACIÓN =====================

    // Eliminación de una clave (y su dato) como parte de la interfaz pública del árbol
    // Devuelve false si la clave no existe
    public boolean eliminar(int clave) {
        if (!buscar(clave)) {
            return false;
        }

        // Se elimina de forma recursiva, rebalanceando al regresar de cada llamada
        eliminarRecursivo(this.raiz, clave);

        // Si la raíz es interna y se quedó sin claves, tiene un solo hijo.
        // Ese hijo pasa a ser la nueva raíz y el árbol baja un nivel.
        if (!this.raiz.esHoja() && this.raiz.getClaves().isEmpty()) {
            this.raiz = this.raiz.getHijos().getFirst();
        }

        // La clave borrada pudo haber quedado como separador en algún nodo interno
        actualizarSeparador(clave);
        return true;
    }

    // Eliminación recursiva de una clave
    // Se baja hasta la hoja, se borra ahí y al regresar se revisa si el hijo quedó con menos
    // claves de las permitidas (subdesbordamiento). En ese caso se rebalancea.
    private void eliminarRecursivo(NodoArbolBMas nodo, int clave) {
        if (nodo.esHoja()) {
            int posicion = nodo.getClaves().indexOf(clave);
            nodo.getClaves().remove(posicion);
            nodo.getDatos().remove(posicion);
            return;
        }

        int i = indiceHijo(nodo, clave);
        NodoArbolBMas hijo = nodo.getHijos().get(i);

        // Llamada recursiva para eliminar primero en el hijo correspondiente
        eliminarRecursivo(hijo, clave);

        // Evaluación de subdesbordamiento al retornar de la recursividad
        if (hijo.getClaves().size() < minimoClaves()) {
            rebalancearHijo(nodo, i);
        }
    }

    // Si la clave borrada se seguía usando como separador en un nodo interno, se reemplaza por
    // la clave más pequeña de su subárbol derecho. Así las claves internas siempre corresponden
    // a elementos que sí existen en las hojas.
    // Se hace al final (con el árbol ya rebalanceado) porque una rotación o una fusión puede
    // bajar el separador viejo a un nivel inferior. Ese separador siempre queda en el camino
    // de búsqueda de la clave y aparece una sola vez, por eso basta con recorrer ese camino.
    private void actualizarSeparador(int claveEliminada) {
        NodoArbolBMas actual = this.raiz;
        while (!actual.esHoja()) {
            int posicion = actual.getClaves().indexOf(claveEliminada);
            if (posicion != -1) {
                actual.getClaves().set(posicion, menorClave(actual.getHijos().get(posicion + 1)));
                return;
            }
            actual = actual.getHijos().get(indiceHijo(actual, claveEliminada));
        }
    }

    // Devuelve la clave más pequeña de un subárbol (la primera de su hoja de más a la izquierda)
    private int menorClave(NodoArbolBMas nodo) {
        NodoArbolBMas actual = nodo;
        while (!actual.esHoja()) {
            actual = actual.getHijos().getFirst();
        }
        return actual.getClaves().getFirst();
    }

    // Corrige un hijo que quedó con menos claves del mínimo.
    // Primero intenta pedir prestada una clave a un hermano que tenga de sobra;
    // si ninguno puede prestar, fusiona el hijo con un hermano.
    private void rebalancearHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hermanoIzquierdo = indice > 0 ? padre.getHijos().get(indice - 1) : null;
        NodoArbolBMas hermanoDerecho = indice < padre.getHijos().size() - 1 ? padre.getHijos().get(indice + 1) : null;

        if (hermanoIzquierdo != null && hermanoIzquierdo.getClaves().size() > minimoClaves()) {
            prestarDeIzquierdo(padre, indice);
        } else if (hermanoDerecho != null && hermanoDerecho.getClaves().size() > minimoClaves()) {
            prestarDeDerecho(padre, indice);
        } else if (hermanoIzquierdo != null) {
            fusionar(padre, indice - 1);
        } else {
            fusionar(padre, indice);
        }
    }

    // El hijo en la posición indice recibe la última clave de su hermano izquierdo
    private void prestarDeIzquierdo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas izquierdo = padre.getHijos().get(indice - 1);

        if (hijo.esHoja()) {
            // Se pasa la última clave (con su dato) del hermano al inicio del hijo
            hijo.getClaves().addFirst(izquierdo.getClaves().removeLast());
            hijo.getDatos().addFirst(izquierdo.getDatos().removeLast());
            // El separador del padre pasa a ser la nueva primera clave del hijo
            padre.getClaves().set(indice - 1, hijo.getClaves().getFirst());
        } else {
            // En nodos internos se hace una rotación: el separador baja al hijo,
            // la última clave del hermano sube al padre y el último hijo del hermano se mueve
            hijo.getClaves().addFirst(padre.getClaves().get(indice - 1));
            padre.getClaves().set(indice - 1, izquierdo.getClaves().removeLast());
            hijo.getHijos().addFirst(izquierdo.getHijos().removeLast());
        }
    }

    // El hijo en la posición indice recibe la primera clave de su hermano derecho
    private void prestarDeDerecho(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if (hijo.esHoja()) {
            // Se pasa la primera clave (con su dato) del hermano al final del hijo
            hijo.getClaves().addLast(derecho.getClaves().removeFirst());
            hijo.getDatos().addLast(derecho.getDatos().removeFirst());
            // El separador entre los dos pasa a ser la nueva primera clave del hermano
            padre.getClaves().set(indice, derecho.getClaves().getFirst());
        } else {
            // Rotación hacia la izquierda: el separador baja al hijo,
            // la primera clave del hermano sube al padre y el primer hijo del hermano se mueve
            hijo.getClaves().addLast(padre.getClaves().get(indice));
            padre.getClaves().set(indice, derecho.getClaves().removeFirst());
            hijo.getHijos().addLast(derecho.getHijos().removeFirst());
        }
    }

    // Fusiona el hijo en la posición indice con el que está a su derecha (indice + 1).
    // El resultado queda en el de la izquierda y el padre pierde un separador y un hijo.
    private void fusionar(NodoArbolBMas padre, int indice) {
        NodoArbolBMas izquierdo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if (izquierdo.esHoja()) {
            // En hojas el separador no se baja, porque la clave ya está en la hoja derecha
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            // Se salta la hoja derecha en la lista enlazada
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            // En nodos internos el separador sí baja para unir las dos mitades
            izquierdo.getClaves().add(padre.getClaves().get(indice));
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }

        padre.getClaves().remove(indice);
        padre.getHijos().remove(indice + 1);
    }

    // ===================== RECORRIDO POR RANGO =====================

    // Recorrido por rango: busca la clave de inicio e imprime los siguientes n elementos
    // (clave y dato), empezando por esa clave. Si la clave no existe, empieza en la siguiente
    // clave mayor. Para avanzar se usan los enlaces entre hojas, sin volver a bajar por el árbol.
    public void recorrerRango(int claveInicio, int n) {
        if (n <= 0) {
            System.out.println("La cantidad de elementos a recorrer debe ser mayor que 0.");
            return;
        }
        if (estaVacio()) {
            System.out.println("(El árbol está vacío)");
            return;
        }

        // Búsqueda de la hoja y de la posición donde empieza el recorrido
        NodoArbolBMas hoja = buscarHoja(claveInicio);
        int posicion = 0;
        while (posicion < hoja.getClaves().size() && hoja.getClaves().get(posicion) < claveInicio) {
            posicion++;
        }

        if (!buscar(claveInicio)) {
            System.out.println("La clave " + claveInicio + " no está en el árbol, el recorrido empieza en la siguiente clave mayor.");
        }

        int impresos = 0;
        while (hoja != null && impresos < n) {
            while (posicion < hoja.getClaves().size() && impresos < n) {
                impresos++;
                System.out.println("  " + impresos + ") Clave " + hoja.getClaves().get(posicion)
                        + " -> " + hoja.getDatos().get(posicion));
                posicion++;
            }
            // Se pasa a la hoja siguiente y se empieza desde su primera clave
            hoja = hoja.getSiguiente();
            posicion = 0;
        }

        if (impresos == 0) {
            System.out.println("No hay elementos con clave mayor o igual a " + claveInicio + ".");
        } else if (impresos < n) {
            System.out.println("Solo había " + impresos + " elemento(s) a partir de esa clave.");
        }
    }

    // ===================== IMPRESIÓN =====================

    // Impresión del árbol como parte de la interfaz pública del árbol
    public void imprimirArbol() {
        if (estaVacio()) {
            System.out.println("(El árbol está vacío)");
            return;
        }
        imprimirNodo(raiz, "", true);
    }

    // Impresión recursiva privada de los nodos a partir de uno inicial
    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for (int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }

    // Muestra las hojas en el orden de la lista enlazada y luego todos los elementos con sus datos.
    // Sirve para comprobar que los enlaces siguiente están bien después de insertar y eliminar.
    public void imprimirHojas() {
        if (estaVacio()) {
            System.out.println("(El árbol está vacío)");
            return;
        }

        StringBuilder cadena = new StringBuilder("Hojas enlazadas: ");
        NodoArbolBMas hoja = hojaMasIzquierda();
        while (hoja != null) {
            cadena.append(hoja.getClaves());
            if (hoja.getSiguiente() != null) {
                cadena.append(" -> ");
            }
            hoja = hoja.getSiguiente();
        }
        System.out.println(cadena.append(" -> null"));

        System.out.println("Elementos almacenados:");
        hoja = hojaMasIzquierda();
        while (hoja != null) {
            for (int i = 0; i < hoja.getClaves().size(); i++) {
                System.out.println("  Clave " + hoja.getClaves().get(i) + " -> " + hoja.getDatos().get(i));
            }
            hoja = hoja.getSiguiente();
        }
    }
}
