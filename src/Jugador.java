import java.util.Random;
import javax.swing.JPanel;

public class Jugador {

    private final int TOTAL_CARTAS = 10;
    private final int MARGEN = 10;
    private final int SEPARACION = 40;
    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random();

    public void repartir() {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i] = new Carta(r);
        }
    }

    public void mostrar(JPanel pnl) {
        pnl.removeAll();
        /*
         * for (int i = 0; i < TOTAL_CARTAS; i++) {
         * Carta carta=cartas[i];
         * carta.mostrar(pnl, , );
         * }
         */
        int posicion = MARGEN + TOTAL_CARTAS * SEPARACION;
        for (Carta carta : cartas) {
            carta.mostrar(pnl, posicion, MARGEN);
            posicion -= SEPARACION;
        }
        pnl.repaint();
    }

    public String getGrupos() {
        String resultado = "No se encontraron grupos";
        int[] contadores = new int[NombreCarta.values().length];
        for (Carta carta : cartas) {
            contadores[carta.getNombre().ordinal()]++;
        }

        boolean hayGrupos = false;
        for (int contador : contadores) {
            if (contador >= 2) {
                hayGrupos = true;
                break;
            }
        }

        if (hayGrupos) {
            resultado = "Se hallaron los siguientes grupos:\n";
            for (int i = 0; i < contadores.length; i++) {
                int contador = contadores[i];
                if (contador >= 2) {
                    resultado += Grupo.values()[contador] + " de " + NombreCarta.values()[i] + "\n";
                }
            }
        }

        return resultado;
    }
//NUEVA FUNCIONALIDAD: Escaleras desde 2 cartas del mismo palo consecutivas
    public String getEscaleras() {
    String resultado = "No se encontraron escaleras";
    String escaleras = "";
    // Nombres de las cartas en orden
    String[] nombresCartas = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    // Arreglo con los grupos posibles PAR, TERNA, etc.
    Grupo[] grupos = Grupo.values();
    // Recorremos cada pinta: CORAZÓN, PICA, etc.
    for (Pinta pinta : Pinta.values()) {

        // Arreglo para marcar qué valores de carta existen en esta pinta
        boolean[] numeros = new boolean[13];
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            if (cartas[i].getPinta() == pinta) {
                int posicion = cartas[i].getNombre().ordinal();
                numeros[posicion] = true;
        }
        }
        // Detectar secuencias de cartas consecutivas en esta pinta
        int inicio = -1;
        int longitud = 0;
        boolean escaleraPendiente = false;

        for (int i = 0; i < numeros.length; i++) {
            if (numeros[i]) {
                if (inicio == -1) {
                    inicio = i;
                }
                longitud++;
                escaleraPendiente = true;
            } else {
                if (escaleraPendiente) {
                    // Terminó una escalera,entonces procesarla
                    escaleras = escaleras + procesarEscalera(nombresCartas, grupos, inicio, longitud, pinta);
                    // Reinicio para buscar nueva
                    escaleraPendiente = false;
                    inicio = -1;
                    longitud = 0;
            }
        }
        }
        // Por si terminó en una escalera (última carta)
        if (escaleraPendiente) {
            escaleras = escaleras + procesarEscalera(nombresCartas, grupos, inicio, longitud, pinta);
        }
    }

    // Si se encontró al menos una escalera, se actualiza el mensaje
    if (escaleras != "") {
        resultado = "Se hallaron las siguientes escaleras:\n" + escaleras;
    }
    return resultado;
}
//Encapsulo el metodo procesarEscalera que es una funcion de apoyo
private String procesarEscalera(String[] nombresCartas, Grupo[] grupos, int inicio, int longitud, Pinta pinta) {
    if (longitud < 2) {
        return ""; // No es válida si tiene menos de dos cartas
    }
    String desde = nombresCartas[inicio];
    String hasta = nombresCartas[inicio + longitud - 1];
    Grupo grupoEncontrado;

    // Si la longitud excede los grupos disponibles, usar el último (DECIMA)
    if (longitud < grupos.length) {
        grupoEncontrado = grupos[longitud];
    } else {
        grupoEncontrado = Grupo.DECIMA;
    }
    return grupoEncontrado + " de " + pinta + ": de " + desde + " a " + hasta + "\n";
}

    // NUEVA FUNCIONALIDAD: Obtener puntaje de los jugadores.
    public int getPuntajeCartasSolas() {
    boolean[] usadas = new boolean[TOTAL_CARTAS]; // Marcar cartas ya agrupadas

    // 1. Marcar las que forman grupos (pares, ternas, cuartas)
    int[] contadores = new int[NombreCarta.values().length];
    for (Carta carta : cartas) {
        contadores[carta.getNombre().ordinal()]++;
    }
    for (int i = 0; i < TOTAL_CARTAS; i++) {
        Carta carta = cartas[i];
        if (contadores[carta.getNombre().ordinal()] >= 2) {
            usadas[i] = true;
        }
    }

    // 2. Marcar las que forman escaleras
    for (Pinta pinta : Pinta.values()) {
        boolean[] numeros = new boolean[13];
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            if (cartas[i].getPinta() == pinta) {
                numeros[cartas[i].getNombre().ordinal()] = true;
            }
        }

        int inicio = -1, longitud = 0;
        for (int i = 0; i < numeros.length; i++) {
            if (numeros[i]) {
                if (inicio == -1) inicio = i;
                longitud++;
            } else {
                if (longitud >= 2) {
                    marcarEscalera(usadas, pinta, inicio, longitud);
                }
                inicio = -1;
                longitud = 0;
            }
        }
        if (longitud >= 2) {
            marcarEscalera(usadas, pinta, inicio, longitud);
        }
    }
    

    // 3. Sumar solo las no usadas
    int puntaje = 0;
    for (int i = 0; i < TOTAL_CARTAS; i++) {
        if (!usadas[i]) {
            puntaje += cartas[i].getValor();
        }
    }
    return puntaje;
}

// Método de apoyo: marca las cartas de una escalera como usadas
private void marcarEscalera(boolean[] usadas, Pinta pinta, int inicio, int longitud) {
    for (int i = 0; i < TOTAL_CARTAS; i++) {
        if (cartas[i].getPinta() == pinta) {
            int pos = cartas[i].getNombre().ordinal();
            if (pos >= inicio && pos < inicio + longitud) {
                usadas[i] = true;
            }
        }
    }
}
    }
