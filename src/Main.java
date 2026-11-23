import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * Programa de consola para usar el árbol B+.
 * El usuario elige el orden del árbol al iniciar y después trabaja desde el menú:
 * insertar, eliminar, buscar, recorrer un rango, ver la estructura, etc.
 * Cada elemento se compone de una clave (número entero) y un dato (texto).
 */
public class Main {

    // Orden que se usa si el usuario no escribe ninguno (el mismo del ejemplo original)
    private static final int ORDEN_POR_DEFECTO = 4;

    // Lector de consola compartido por todo el programa y árbol con el que se trabaja
    private static final Scanner entrada = new Scanner(System.in);
    private static ArbolBMas arbol;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   PRÁCTICA 2 - IMPLEMENTACIÓN DE UN ÁRBOL B+");
        System.out.println("==================================================");
        System.out.println("Cada elemento tiene una clave (número entero) y un dato (texto).");
        System.out.println("Los datos se guardan solo en las hojas y las hojas están enlazadas.");

        try {
            // 1. Se crea el árbol con el orden que indique el usuario
            arbol = new ArbolBMas(leerOrden());

            // 2. Se muestra el menú hasta que el usuario decida salir
            menu();
        } catch (NoSuchElementException e) {
            // Pasa si se cierra la entrada estándar (por ejemplo con Ctrl+D), así no se cae el programa
            System.out.println();
            System.out.println("Se cerró la entrada de datos.");
        }

        System.out.println("Programa finalizado. ¡Hasta luego!");
    }

    // Menú principal del programa
    private static void menu() {
        int opcion;
        do {
            mostrarOpciones();
            opcion = leerEntero("Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1 -> opcionInsertar();
                case 2 -> opcionEliminar();
                case 3 -> opcionBuscar();
                case 4 -> opcionRecorrerRango();
                case 5 -> opcionMostrarArbol();
                case 6 -> opcionMostrarHojas();
                case 7 -> opcionCargarEjemplo();
                case 8 -> opcionReiniciar();
                case 0 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción no válida. Escriba un número del 0 al 8.");
            }

            if (opcion != 0) {
                pausar();
            }
        } while (opcion != 0);
    }

    private static void mostrarOpciones() {
        System.out.println();
        System.out.println("---------------- MENÚ PRINCIPAL ----------------");
        System.out.println(" Árbol B+ de orden " + arbol.getOrden());
        System.out.println("------------------------------------------------");
        System.out.println(" 1. Insertar un elemento");
        System.out.println(" 2. Eliminar un elemento");
        System.out.println(" 3. Buscar un elemento");
        System.out.println(" 4. Recorrer un rango (desde una clave, n elementos)");
        System.out.println(" 5. Mostrar la estructura del árbol");
        System.out.println(" 6. Mostrar las hojas enlazadas y todos los datos");
        System.out.println(" 7. Cargar datos de ejemplo");
        System.out.println(" 8. Reiniciar el árbol (vaciarlo o cambiar el orden)");
        System.out.println(" 0. Salir");
        System.out.println("------------------------------------------------");
    }

    // ===================== OPCIONES DEL MENÚ =====================

    private static void opcionInsertar() {
        System.out.println("== Insertar un elemento ==");
        int clave = leerEntero("Clave (número entero): ");

        // Se revisa antes de pedir el dato para no hacer que el usuario lo escriba de gusto
        if (arbol.buscar(clave)) {
            System.out.println("La clave " + clave + " ya existe (dato: " + arbol.obtenerDato(clave)
                    + "). No se permiten claves repetidas.");
            return;
        }

        String dato = leerTexto("Dato asociado a la clave: ");
        arbol.insertar(clave, dato);
        System.out.println("Se insertó la clave " + clave + " con el dato \"" + dato + "\".");
        System.out.println("Así quedó el árbol:");
        arbol.imprimirArbol();
    }

    private static void opcionEliminar() {
        System.out.println("== Eliminar un elemento ==");
        if (avisarSiVacio()) {
            return;
        }

        int clave = leerEntero("Clave a eliminar: ");
        String dato = arbol.obtenerDato(clave);

        if (arbol.eliminar(clave)) {
            System.out.println("Se eliminó la clave " + clave + " (dato: " + dato + ").");
            System.out.println("Así quedó el árbol:");
            arbol.imprimirArbol();
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol, no se eliminó nada.");
        }
    }

    private static void opcionBuscar() {
        System.out.println("== Buscar un elemento ==");
        if (avisarSiVacio()) {
            return;
        }

        int clave = leerEntero("Clave a buscar: ");
        if (arbol.buscar(clave)) {
            System.out.println("Clave encontrada: " + clave + " -> " + arbol.obtenerDato(clave));
        } else {
            System.out.println("La clave " + clave + " no se encuentra en el árbol.");
        }
    }

    private static void opcionRecorrerRango() {
        System.out.println("== Recorrer un rango ==");
        if (avisarSiVacio()) {
            return;
        }

        int claveInicio = leerEntero("Clave desde la que se empieza: ");
        int n = leerEnteroPositivo("Cantidad de elementos a mostrar (n): ");
        System.out.println();
        System.out.println("Recorrido desde la clave " + claveInicio + " (" + n + " elemento(s) solicitados):");
        arbol.recorrerRango(claveInicio, n);
    }

    private static void opcionMostrarArbol() {
        System.out.println("== Estructura del árbol (orden " + arbol.getOrden() + ") ==");
        arbol.imprimirArbol();
    }

    private static void opcionMostrarHojas() {
        System.out.println("== Hojas enlazadas y datos almacenados ==");
        arbol.imprimirHojas();
    }

    // Inserta algunos elementos de prueba para no tener que escribirlos uno por uno.
    // Son las mismas claves del Main original, ahora con un dato asociado.
    private static void opcionCargarEjemplo() {
        int[] claves = {10, 20, 5, 6, 15, 30, 25, 40, 45, 50};
        String[] datos = {"Ana", "Bruno", "Carla", "Diego", "Elena",
                "Fabián", "Gabriela", "Héctor", "Isabel", "Jorge"};

        int insertados = 0;
        for (int i = 0; i < claves.length; i++) {
            if (arbol.insertar(claves[i], datos[i])) {
                insertados++;
            }
        }

        System.out.println("Se cargaron " + insertados + " elemento(s) de ejemplo.");
        if (insertados < claves.length) {
            System.out.println((claves.length - insertados) + " clave(s) ya existían y se omitieron.");
        }
        System.out.println("Así quedó el árbol:");
        arbol.imprimirArbol();
    }

    // Crea un árbol nuevo (vacío). Se pide confirmación porque se pierden los datos actuales.
    private static void opcionReiniciar() {
        System.out.println("== Reiniciar el árbol ==");
        String respuesta = leerTexto("Se borrarán todos los elementos actuales. ¿Desea continuar? (s/n): ");
        if (respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si") || respuesta.equalsIgnoreCase("sí")) {
            arbol = new ArbolBMas(leerOrden());
            System.out.println("Se creó un árbol vacío de orden " + arbol.getOrden() + ".");
        } else {
            System.out.println("Operación cancelada, el árbol se mantiene igual.");
        }
    }

    // ===================== MÉTODOS DE APOYO =====================

    // Si el árbol está vacío se le avisa al usuario y se devuelve true
    private static boolean avisarSiVacio() {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío. Inserte elementos o cargue los datos de ejemplo (opción 7).");
            return true;
        }
        return false;
    }

    // Pide el orden del árbol. Si el usuario solo presiona Enter se usa el orden por defecto.
    private static int leerOrden() {
        while (true) {
            System.out.print("Orden del árbol (mínimo 3, Enter para usar " + ORDEN_POR_DEFECTO + "): ");
            String linea = entrada.nextLine().trim();
            if (linea.isEmpty()) {
                return ORDEN_POR_DEFECTO;
            }
            try {
                int orden = Integer.parseInt(linea);
                if (orden >= 3) {
                    return orden;
                }
                System.out.println("El orden debe ser un número entero mayor o igual a 3.");
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número entero.");
            }
        }
    }

    // Pide un número entero y vuelve a preguntar hasta que el valor sea válido
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = entrada.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Valor no válido. Debe escribir un número entero.");
            }
        }
    }

    // Igual que leerEntero, pero solo acepta números mayores que 0
    private static int leerEnteroPositivo(String mensaje) {
        while (true) {
            int numero = leerEntero(mensaje);
            if (numero > 0) {
                return numero;
            }
            System.out.println("El número debe ser mayor que 0.");
        }
    }

    // Pide un texto que no esté vacío
    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = entrada.nextLine().trim();
            if (!linea.isEmpty()) {
                return linea;
            }
            System.out.println("El texto no puede estar vacío.");
        }
    }

    // Espera a que el usuario presione Enter antes de volver a mostrar el menú
    private static void pausar() {
        System.out.print("\nPresione Enter para volver al menú...");
        entrada.nextLine();
    }
}
