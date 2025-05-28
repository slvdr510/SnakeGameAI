
# Explicación del Código:

---


## Funciones usadas:

### 1. estaEnCuerpo(int x, int y, List `<Point>` cuerpo)

Verifica si un punto (x, y) está en el cuerpo de la serpiente.


### 2. estaDentroDelMapa(int x, int y)

Devuelve true si está dentro del ALTO y el ANCHO del mapa.


### 3. esMovimientoValido(int x, int y, List `<Point>` cuerpo)

Verifica si un movimiento hacia un punto (x, y) es válido. Usando las dos funciones anteriores de la siguiente manera: estaDentroDelMapa(x, y) && !estaEnCuerpo(x, y, cuerpo);


### 4. distanciaManhattan(Point a, Point b)

Calcula la distancia de Manhattan entre dos puntos.


### 5. direccionDesdePuntos(Point puntoActual, Point siguiente)

Determina la dirección (DERECHA, IZQUIERDA, ARRIBA, ABAJO) entre dos puntos consecutivos y devuelve el string del movimiento correspondiente.


### 6. reconstruirCaminoAStar(NodoAStar nodo)

Reconstruye el camino desde el nodo final hasta el nodo inicial en el algoritmo A*.


### 7. hayCaminoDeLlegadaYSalida(List `<Point>` cuerpoEnVivo, Point comida)

Utiliza el algoritmo de búsqueda en anchura (BFS) para explorar el mapa y determinar si existe un camino válido desde la cabeza de la serpiente hasta la comida.


### 8. esCallejonSinSalida(Point coordenadaAlaQueSeQuiereMover, List `<Point> cuerpo`)

**Simula el movimiento:**

1. Crea una copia del cuerpo de la serpiente, añade la nueva cabeza y elimina la última parte (como si avanzara).
2. **Explora el espacio libre:**
   Utiliza una búsqueda en anchura (BFS) desde la nueva cabeza para contar cuántos espacios libres puede alcanzar la serpiente sin chocar consigo misma ni con los bordes.
3. **Evalúa el resultado:**
   Si el número de espacios libres es menor que el tamaño del cuerpo de la serpiente, significa que no hay suficiente espacio para moverse y, por tanto, es un callejón sin salida.

**Devuelve `true`** si el movimiento lleva a un callejón sin salida.

**Devuelve `false`** si hay suficiente espacio para que la serpiente siga moviéndose.

---


## Explicación del método [pensar()](vscode-file://vscode-app/c:/Users/salva/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.html)

El método [pensar()](vscode-file://vscode-app/c:/Users/salva/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.html) es el núcleo de la inteligencia de la serpiente. Su objetivo es decidir la secuencia de movimientos óptimos para alcanzar la comida evitando colisiones y situaciones de callejón sin salida. El proceso se divide en varias fases:


### 1. Inicialización

* **movimientos**: Se crea una lista de acciones vacía.
* **cuerpoEnVivo**:Se hace una copia del cuerpo de la serpiente que será actualizada cada vez que hagamos un movimiento.
* **cabeza**: Posición de la cabeza en el momento en el que se llama a la función pensar().

### 2. Evalua si la comida está sobre la cabeza

* Si la comida está justo en la cabeza, la serpiente realiza los movimientos necesarios para liberarla (No uso ningún algoritmo, solo movimientos posibles).
* Intenta moverse en cualquier dirección válida, simulando el movimiento y actualizando el cuerpo virtual.
* Si no hay movimientos válidos, fuerza un movimiento dentro de los límites del mapa.

### 3. Primera fase: buscar espacio libre antes de ir a la comida si no hay camino de llegada y salida

* Mientras no exista un camino claro desde la cabeza hasta la comida ([hayCaminoDeLlegadaYSalida](vscode-file://vscode-app/c:/Users/salva/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.html)):
  * Evalúa los 4 movimientos posibles.
  * Calcula para cada uno el espacio libre alrededor y si lleva a un callejón sin salida.
  * Elige el movimiento que maximiza el espacio libre y no lleva a un callejón.
  * Simula el movimiento y actualiza el cuerpo virtual y la cabeza.

### 4. Segunda fase: buscar el camino óptimo a la comida (A*)

* Cuando ya hay un camino a la comida, utiliza el algoritmo A* para encontrar la ruta más corta y segura.
* Inicializa una cola de prioridad con nodos A* (cada nodo representa una posición y su coste).
* Explora nodos vecinos válidos, calculando costes y heurística (distancia Manhattan).
* Si llega a la comida, reconstruye el camino y añade los movimientos a la lista de acciones, simulando cada paso.
* Repite hasta que la cabeza alcance la comida o no haya más caminos.

### 5. Retorno de acciones

Devuelve la lista de movimientos necesarios para alcanzar la comida.


---



**Extra**: El ciclo hamiltoniano no lo explico en el readme.
