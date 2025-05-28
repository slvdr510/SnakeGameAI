import java.awt.Point;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import uhu.snake.Agente;

public class salvadorbolancealu extends Agente {

    public salvadorbolancealu() {
        super();
    }

    String DERECHA = ACCIONES[0];
    String IZQUIERDA = ACCIONES[1];
    String ARRIBA = ACCIONES[2];
    String ABAJO = ACCIONES[3];

    int[] dx = {1, -1, 0, 0};
    int[] dy = {0, 0, 1, -1};
    String[] direcciones = {DERECHA, IZQUIERDA, ABAJO, ARRIBA};

    private static class NodoAStar {
        int x, y, g, h, f;
        NodoAStar padre;

        public NodoAStar(int x, int y, int g, int h, NodoAStar padre) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.padre = padre;
        }
    }

    /**
     * Verifica si un punto (x, y) está en el cuerpo de la serpiente.
     */
    private boolean estaEnCuerpo(int x, int y, List<Point> cuerpo) {
        return cuerpo.contains(new Point(x, y));
    }

    /**
     * Verifica si un punto (x, y) está dentro de los límites del mapa.
     */
    private boolean estaDentroDelMapa(int x, int y) {
        return x >= 0 && x < ANCHO && y >= 0 && y < ALTO;
    }

    /**
     * Verifica si un movimiento hacia un punto (x, y) es válido.
     */
    private boolean esMovimientoValido(int x, int y, List<Point> cuerpo) {
        return estaDentroDelMapa(x, y) && !estaEnCuerpo(x, y, cuerpo);
    }

    /**
     * Calcula la distancia de Manhattan entre dos puntos.
     */
    private int distanciaManhattan(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Determina la dirección (DERECHA, IZQUIERDA, ARRIBA, ABAJO) entre dos puntos consecutivos.
     */
    private String direccionDesdePuntos(Point puntoActual, Point siguiente) {
        if (siguiente.x > puntoActual.x) return DERECHA;
        if (siguiente.x < puntoActual.x) return IZQUIERDA;
        if (siguiente.y > puntoActual.y) return ABAJO;
        if (siguiente.y < puntoActual.y) return ARRIBA;
        return "";
    }

    /**
     * Reconstruye el camino desde el nodo final hasta el nodo inicial en el algoritmo A*.
     */
    private List<Point> reconstruirCaminoAStar(NodoAStar nodo) {
        LinkedList<Point> camino = new LinkedList<>();
        while (nodo != null) {
            camino.addFirst(new Point(nodo.x, nodo.y));
            nodo = nodo.padre;
        }
        return camino;
    }

    /**
     * Verifica si hay un camino de llegada y salida entre la cabeza de la serpiente y la comida.
     * 
     * Utiliza el algoritmo de búsqueda en anchura (BFS) para explorar el mapa y determinar
     * si existe un camino válido desde la cabeza de la serpiente hasta la comida.
     */
    private boolean hayCaminoDeLlegadaYSalida(List<Point> cuerpoEnVivo, Point comida) {
        LinkedList<Point> cola = new LinkedList<>();
        HashSet<Point> visitados = new HashSet<>();
        Point cabeza = cuerpoEnVivo.get(0);

        cola.add(cabeza);
        visitados.add(cabeza);

        while (!cola.isEmpty()) {
            Point actual = cola.poll();

            if (actual.equals(comida)) {
                return true;
            }

            for (int i = 0; i < 4; i++) {
                int nuevoX = actual.x + dx[i];
                int nuevoY = actual.y + dy[i];
                Point vecino = new Point(nuevoX, nuevoY);

                if (esMovimientoValido(nuevoX, nuevoY, cuerpoEnVivo) && !visitados.contains(vecino)) {
                    cola.add(vecino);
                    visitados.add(vecino);
                }
            }
        }

        return false;
    }

    /**
     * Verifica si un movimiento lleva a un callejón sin salida.
     */
    private boolean esCallejonSinSalida(Point nuevaCabeza, List<Point> cuerpoEnVivo) {
        LinkedList<Point> cola = new LinkedList<>();
        HashSet<Point> visitados = new HashSet<>();
        List<Point> cuerpoSimulado = new LinkedList<>(cuerpoEnVivo);
        cuerpoSimulado.add(0, nuevaCabeza);
        cuerpoSimulado.remove(cuerpoSimulado.size() - 1);

        cola.add(nuevaCabeza);
        visitados.add(nuevaCabeza);

        int espaciosLibres = 0;

        while (!cola.isEmpty()) {
            Point actual = cola.poll();
            espaciosLibres++;

            for (int i = 0; i < 4; i++) {
                int nuevoX = actual.x + dx[i];
                int nuevoY = actual.y + dy[i];
                Point vecino = new Point(nuevoX, nuevoY);

                if (esMovimientoValido(nuevoX, nuevoY, cuerpoSimulado) && !visitados.contains(vecino)) {
                    cola.add(vecino);
                    visitados.add(vecino);
                }
            }
        }

        // Si los espacios libres son menores que el tamaño del cuerpo, es un callejón sin salida.
        return espaciosLibres < cuerpoSimulado.size();
    }

    /**
     * Método principal que implementa la lógica del agente para decidir las acciones de la serpiente.
     */
    @Override
    public List<String> pensar() {
        List<String> acciones = new LinkedList<>();
        List<Point> cuerpoEnVivo = new LinkedList<>(CUERPO);
        Point cabeza = new Point(cuerpoEnVivo.get(0));

        // Si la comida está sobre la cabeza, realizar movimientos para liberarla.
        if (cuerpoEnVivo.get(0).equals(COMIDA)) {
            int movimientosRestantes = cuerpoEnVivo.size(); // Mover tantas veces como partes tiene el cuerpo.
            while (movimientosRestantes > 0) {
                boolean movimientoRealizado = false;

                for (int i = 0; i < 4; i++) {
                    int nuevoX = cabeza.x + dx[i];
                    int nuevoY = cabeza.y + dy[i];
                    Point nuevaCabeza = new Point(nuevoX, nuevoY);

                    if (esMovimientoValido(nuevoX, nuevoY, cuerpoEnVivo)) {
                        acciones.add(direcciones[i]);
                        cuerpoEnVivo.add(0, nuevaCabeza);
                        cuerpoEnVivo.remove(cuerpoEnVivo.size() - 1);
                        cabeza = nuevaCabeza;
                        movimientosRestantes--;
                        movimientoRealizado = true;
                        break; // Salir del bucle si se realiza un movimiento válido.
                    }
                }

                // Si no se pudo realizar un movimiento válido, forzar un movimiento.
                if (!movimientoRealizado) {
                    for (int i = 0; i < 4; i++) {
                        int nuevoX = cabeza.x + dx[i];
                        int nuevoY = cabeza.y + dy[i];
                        Point nuevaCabeza = new Point(nuevoX, nuevoY);

                        if (estaDentroDelMapa(nuevoX, nuevoY)) { // Solo verificar límites del mapa.
                            acciones.add(direcciones[i]);
                            cuerpoEnVivo.add(0, nuevaCabeza);
                            cuerpoEnVivo.remove(cuerpoEnVivo.size() - 1);
                            cabeza = nuevaCabeza;
                            movimientosRestantes--;
                            break;
                        }
                    }
                }
            }
        }

        // Primera fase: buscar el mejor movimiento basado en el espacio libre.
        while (!hayCaminoDeLlegadaYSalida(cuerpoEnVivo, COMIDA)) {
            int maxEspacioLibre = -1;
            String mejorMovimiento = null;
            Point mejorCabeza = null;

            // Evaluar los movimientos posibles y calcular el espacio libre.
            for (int i = 0; i < 4; i++) {
                int nuevoX = cuerpoEnVivo.get(0).x + dx[i];
                int nuevoY = cuerpoEnVivo.get(0).y + dy[i];
                Point nuevaCabeza = new Point(nuevoX, nuevoY);

                if (esMovimientoValido(nuevoX, nuevoY, cuerpoEnVivo) && !esCallejonSinSalida(nuevaCabeza, cuerpoEnVivo)) {
                    int espacioLibre = 0;
                    for (int j = 0; j < 4; j++) {
                        int vecinoX = nuevoX + dx[j];
                        int vecinoY = nuevoY + dy[j];
                        if (esMovimientoValido(vecinoX, vecinoY, cuerpoEnVivo)) {
                            espacioLibre++;
                        }
                    }

                    // Actualizar el mejor movimiento si se encuentra uno con más espacio libre.
                    if (espacioLibre > maxEspacioLibre) {
                        maxEspacioLibre = espacioLibre;
                        mejorMovimiento = direcciones[i];
                        mejorCabeza = nuevaCabeza;
                    }
                }
            }

            // Realizar el mejor movimiento encontrado.
            if (mejorMovimiento != null) {
                acciones.add(mejorMovimiento);
                cuerpoEnVivo.add(0, mejorCabeza);
                cuerpoEnVivo.remove(cuerpoEnVivo.size() - 1);
                cabeza = mejorCabeza;
            } else {
                break; // No hay movimientos válidos.
            }
        }

        // Segunda fase: usar el algoritmo A* para encontrar el camino óptimo hacia la comida.
        do {
            // Lista de nodos abiertos (por explorar), ordenados por el costo total (f = g + h).
            PriorityQueue<NodoAStar> listaAbiertos = new PriorityQueue<>(Comparator.comparingInt(nodo -> nodo.f));
            // Conjunto de nodos cerrados (ya explorados).
            HashSet<Point> listaCerrados = new HashSet<>();

            // Crear el nodo inicial (posición actual de la cabeza de la serpiente).
            NodoAStar inicio = new NodoAStar(cuerpoEnVivo.get(0).x, cuerpoEnVivo.get(0).y, 0, distanciaManhattan(cuerpoEnVivo.get(0), COMIDA), null);
            listaAbiertos.add(inicio);

            // Mientras haya nodos por explorar en la lista de abiertos.
            while (!listaAbiertos.isEmpty()) {
                // Extraer el nodo con el menor costo total (f) de la lista de abiertos.
                NodoAStar nodoActual = listaAbiertos.poll();
                Point puntoActual = new Point(nodoActual.x, nodoActual.y);

                // Si se alcanza la comida, reconstruir el camino y agregar las acciones.
                if (puntoActual.equals(COMIDA)) {
                    List<Point> camino = reconstruirCaminoAStar(nodoActual);
                    for (int i = 0; i < camino.size() - 1; i++) {
                        // Determinar la dirección entre puntos consecutivos del camino.
                        String sigMovimiento = direccionDesdePuntos(camino.get(i), camino.get(i + 1));
                        acciones.add(sigMovimiento);

                        // Actualizar el cuerpo de la serpiente simulando el movimiento.
                        Point nuevaCabeza = camino.get(i + 1);
                        cuerpoEnVivo.add(0, nuevaCabeza);
                        cuerpoEnVivo.remove(cuerpoEnVivo.size() - 1);
                    }
                    break; // Salir del bucle si se encuentra el camino a la comida.
                }

                // Mover el nodo actual a la lista de cerrados.
                listaCerrados.add(puntoActual);

                // Explorar los vecinos del nodo actual.
                for (int i = 0; i < 4; i++) {
                    int nuevoX = nodoActual.x + dx[i];
                    int nuevoY = nodoActual.y + dy[i];
                    Point vecino = new Point(nuevoX, nuevoY);

                    // Verificar si el vecino es un movimiento válido.
                    if (esMovimientoValido(nuevoX, nuevoY, cuerpoEnVivo)) {
                        // Calcular el costo g (distancia desde el inicio) y h (heurística).
                        int nuevoCostoG = nodoActual.g + 1;
                        int hVecino = distanciaManhattan(vecino, COMIDA);
                        NodoAStar nuevoNodo = new NodoAStar(nuevoX, nuevoY, nuevoCostoG, hVecino, nodoActual);

                        // Si el vecino no está en la lista de cerrados, procesarlo.
                        if (!listaCerrados.contains(vecino)) {
                            boolean enListaAbiertos = false;

                            // Verificar si el vecino ya está en la lista de abiertos.
                            for (NodoAStar nodoAbierto : listaAbiertos) {
                                if (nodoAbierto.x == nuevoX && nodoAbierto.y == nuevoY) {
                                    enListaAbiertos = true;
                                    // Si el nuevo camino es mejor, actualizar el nodo en la lista de abiertos.
                                    if (nuevoCostoG < nodoAbierto.g) {
                                        nodoAbierto.g = nuevoCostoG;
                                        nodoAbierto.h = hVecino;
                                        nodoAbierto.f = nuevoCostoG + hVecino;
                                        nodoAbierto.padre = nodoActual;
                                    }
                                    break;
                                }
                            }

                            // Si el vecino no está en la lista de abiertos, agregarlo.
                            if (!enListaAbiertos) {
                                listaAbiertos.add(nuevoNodo);
                            }
                        }
                    }
                }
            }
        } while (cabeza.equals(COMIDA)); // Repetir mientras la cabeza no alcance la comida.

        return acciones;
    }

}

// 1. Definición de la clase y utilidades básicas
// ------------------------------------------------
// - Definición de la clase salvadorbolancealu que extiende Agente.
// - Definición de constantes de dirección y arrays de desplazamiento.
// - Clase interna NodoAStar para nodos del algoritmo A*.
// - Métodos utilitarios para comprobar si una posición está en el cuerpo, dentro del mapa, es válida, calcular distancias, reconstruir caminos, etc.

// 2. Métodos de comprobación de caminos y seguridad
// -------------------------------------------------
// - hayCaminoDeLlegadaYSalida: Comprueba si existe un camino desde la cabeza hasta la comida usando BFS.
// - esCallejonSinSalida: Comprueba si un movimiento lleva a un callejón sin salida simulando el movimiento y contando los espacios libres.

// 3. Método principal pensar()
// ----------------------------
// - Inicializa la lista de acciones y una copia virtual del cuerpo de la serpiente.
// - Si la comida está sobre la cabeza, realiza movimientos para liberar la comida de la cabeza, evitando quedarse bloqueado.
// - Primera fase: Mientras no haya camino a la comida, busca el mejor movimiento que maximice el espacio libre y evite callejones, actualizando el cuerpo virtual y acumulando las acciones.
// - Segunda fase: Cuando ya hay camino a la comida, utiliza el algoritmo A* para encontrar el camino óptimo hasta la comida, simulando cada movimiento y acumulando las acciones necesarias para llegar a la comida. El bucle termina cuando la cabeza alcanza la comida o no hay más caminos posibles.