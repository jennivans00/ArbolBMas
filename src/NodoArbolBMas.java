import java.util.ArrayList;
import java.util.List;

/*
 * Nodo de un árbol B+.
 * Hay dos tipos de nodo:
 *  - Hoja: guarda las claves junto con sus datos (un dato por clave, en la misma posición)
 *    y una referencia a la hoja siguiente, para poder recorrer las hojas como una lista enlazada.
 *    Las hojas no tienen hijos.
 *  - Interno (incluye la raíz cuando no es hoja): solo guarda claves que sirven para guiar
 *    la búsqueda y la lista de hijos. No guarda datos ni enlace a la siguiente hoja.
 */
public class NodoArbolBMas {

    // Atributos
    private final boolean esHoja;
    private List<Integer> listaClaves;
    private List<NodoArbolBMas> listaHijos; // Siempre vacía si es una hoja
    private List<String> datos;             // Datos asociados a cada clave. Es null si es un nodo interno
    private NodoArbolBMas siguiente;        // Hoja que sigue a la derecha. Es null si es un nodo interno

    // Métodos
    // Constructor
    // Si el nodo es hoja se crea la lista de datos; si es interno, datos se queda en null.
    // El enlace siguiente arranca en null en los dos casos (en las hojas se asigna al dividir).
    public NodoArbolBMas(boolean esHoja) {
        this.esHoja = esHoja;
        this.listaClaves = new ArrayList<>();
        this.listaHijos = new ArrayList<>();
        this.datos = esHoja ? new ArrayList<>() : null;
        this.siguiente = null;
    }

    // Getters
    public boolean esHoja() {
        return esHoja;
    }

    public List<Integer> getClaves() {
        return listaClaves;
    }

    public List<NodoArbolBMas> getHijos() {
        return listaHijos;
    }

    public List<String> getDatos() {
        return datos;
    }

    public NodoArbolBMas getSiguiente() {
        return siguiente;
    }

    // Setters
    public void setClaves(List<Integer> claves) {
        this.listaClaves = claves;
    }

    public void setHijos(List<NodoArbolBMas> hijos) {
        this.listaHijos = hijos;
    }

    // Solo las hojas pueden tener datos, por eso se valida el tipo de nodo
    public void setDatos(List<String> datos) {
        if (!esHoja) {
            throw new IllegalStateException("Un nodo interno no puede almacenar datos.");
        }
        this.datos = datos;
    }

    // Solo las hojas se enlazan entre sí, por eso se valida el tipo de nodo
    public void setSiguiente(NodoArbolBMas siguiente) {
        if (!esHoja) {
            throw new IllegalStateException("Un nodo interno no tiene enlace a una hoja siguiente.");
        }
        this.siguiente = siguiente;
    }
}
