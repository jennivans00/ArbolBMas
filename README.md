# Árbol B+ en Java

Segunda práctica de implementación de estructuras de datos.
Universidad CENFOTEC - SOFT-10 Estructuras de Datos - Sección SCV5 - C3-2026.

El programa es una implementación sencilla de un árbol B+, parecido a los que usan las bases de datos y los sistemas de archivos para manejar índices. Se trabaja desde un menú de consola donde se pueden insertar, eliminar, buscar y recorrer elementos, y ver cómo va quedando el árbol después de cada operación.

## Opciones del menú

1. Insertar un elemento (clave y dato)
2. Eliminar un elemento
3. Buscar un elemento
4. Recorrer un rango (desde una clave, n elementos)
5. Mostrar la estructura del árbol
6. Mostrar las hojas enlazadas y todos los datos
7. Cargar datos de ejemplo (las mismas claves del Main original)
8. Reiniciar el árbol (vaciarlo o cambiar el orden)
0. Salir

Todas las entradas se validan, así que si se escribe una letra donde va un número, el programa vuelve a preguntar en vez de caerse. Después de insertar o eliminar se imprime el árbol para ver cómo se rebalanceó.

## Decisiones que se tomaron

- Con orden `m`, cada nodo tiene entre `ceil(m/2)` y `m` hijos (la raíz entre 2 y `m`). Eso significa que un nodo guarda como máximo `m - 1` claves y como mínimo `ceil(m/2) - 1`. Para las hojas se usaron los mismos límites. El orden mínimo permitido es 3, porque con 2 un nodo podría quedar sin claves.
- Las claves son únicas, como en el índice de una base de datos. Si se intenta insertar una que ya existe, se avisa y no se inserta.
- En el recorrido por rango la clave buscada cuenta como el primer elemento. Si quedan menos de `n` elementos, se muestran los que hay y se avisa.

## Pasos realizados

1. Se revisó el código base del docente para entender cómo funcionaban la inserción recursiva y la división de nodos.
2. Se agregaron a `NodoArbolBMas` los atributos `datos` y `siguiente`, y se cambió el constructor para que las hojas creen su lista de datos.
3. Se modificó la inserción para guardar el dato junto con la clave y para enlazar la hoja nueva cuando se divide una hoja.
4. Se corrigió la búsqueda para que siempre termine en una hoja.
5. Se implementó `eliminar()` siguiendo la misma idea de la inserción: se elimina de forma recursiva y, al regresar de cada llamada, se revisa si el hijo quedó con muy pocas claves para pedir prestado o fusionar.
6. Se implementó `recorrerRango()` usando los enlaces entre hojas.
7. Se hizo el menú de consola con validación de entradas.
8. Se probó insertando y eliminando muchas claves con distintos órdenes (de 3 a 10), revisando que el árbol siguiera balanceado, que ningún nodo tuviera más o menos claves de las permitidas, que las hojas siguieran bien enlazadas y que los datos correspondieran a sus claves.
