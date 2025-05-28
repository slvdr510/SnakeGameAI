import uhu.snake.Agente;
import java.awt.Point;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class salvadorbolancealu extends Agente {
	
	public salvadorbolancealu() {
        super();
    }
	
    public List<Point> generarCicloHamiltoniano(int ancho, int alto) {
		// OJO!! Solo sirve cuando ANCHO y ALTO son pares
		
    	List<Point> cicloHamiltoniano = new ArrayList<>();
    	
    	int x=0;
        int y=0;
        
        cicloHamiltoniano.add(new Point(x, y));
        
        while (x<alto-1) {
                x++;
                cicloHamiltoniano.add(new Point(x, y));
        }
        
        while (y < ancho - 1) {
                y++;
                cicloHamiltoniano.add(new Point(x, y));
        }
        
        while (!(x==0&&y==1)){ // while not casillaFinal
            if ((x%2==0)){
                if (y!=1){
                    y--; // moverse a la izq
                } else {
                    x--; // moverse pa arriba
                }
                
            } else { // fila impar
                if (y!=ancho-1){
                    y++; // moverse a la derecha
                } else {
                    x--; // moverse pa arriba
                } 
            }
            cicloHamiltoniano.add(new Point(x, y));
        }

        return cicloHamiltoniano;
    }

    @Override
    public List<String> pensar() {
    	
        List<Point> cicloHamiltoniano = generarCicloHamiltoniano(ANCHO, ALTO);
        Map<Point, Integer> mapaIndicesCiclo;
    	
        mapaIndicesCiclo = new HashMap<>();

        for (int i = 0; i < cicloHamiltoniano.size(); i++) {
            Point p = cicloHamiltoniano.get(i);
            mapaIndicesCiclo.put(new Point(p), i);
        }
    	
    	List<String> acciones = new LinkedList<>();

        Point cabezaActual = new Point(CUERPO.get(0)); // Copia la cabeza para mantenerla actualizada
        Point comida = COMIDA;

        Integer indiceCabeza = mapaIndicesCiclo.get(cabezaActual);
        Integer indiceComida = mapaIndicesCiclo.get(comida);
        int tamanioCiclo = cicloHamiltoniano.size();

        // Calcular la distancia siguiendo el ciclo hamiltoniano en un único sentido (siempre avanzando)
        int distancia;
        if (indiceComida >= indiceCabeza) {
            distancia = indiceComida - indiceCabeza;
        } else {
            // Si la comida está antes que la cabeza en el ciclo, tenemos que dar la vuelta completa
            distancia = tamanioCiclo - indiceCabeza + indiceComida;
        }

        // Generar todos los movimientos necesarios para llegar a la comida siguiendo el ciclo
        for (int i = 1; i <= distancia; i++) {
            int indice = (indiceCabeza + i) % tamanioCiclo;
            Point siguientePunto = cicloHamiltoniano.get(indice);
            
            // Calculamos el movimiento con la posición actual de la cabeza
            String movimiento = calcularMovimiento(cabezaActual, siguientePunto);
            acciones.add(movimiento);
            
            // Actualizamos la posición de la cabeza para el siguiente movimiento
            cabezaActual.setLocation(siguientePunto);
        }

        return acciones;
    }
    
    // Nuevo método para calcular el movimiento entre dos puntos
    private String calcularMovimiento(Point origen, Point destino) {
        if (destino.x > origen.x) {
			return "DERECHA";
		}
        if (destino.x < origen.x) {
			return "IZQUIERDA";
		}
        if (destino.y > origen.y) {
			return "ABAJO";
		}
        if (destino.y < origen.y) {
			return "ARRIBA";
		}
        return "";
    }
}