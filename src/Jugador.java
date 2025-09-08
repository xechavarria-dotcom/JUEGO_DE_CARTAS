import java.util.Random;
import javax.swing.JPanel;

public class Jugador {

    private final int TOTAL_CARTAS = 10;
    private final int MARGEN = 10;
    private final int SEPARACION = 40;
    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random();
    private Carta[] sobrantesGrupos = new Carta[TOTAL_CARTAS];
    private int totalSobrantesGrupos = 0;
    private Carta[] sobrantesEscaleras = new Carta[TOTAL_CARTAS];
    private int totalSobrantesEscaleras = 0;
    private Carta[] sobrantesFinales = new Carta[TOTAL_CARTAS];
    private int totalSobrantesFinales = 0;

    // Reparte cartas aleatorias
    public void repartir() {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i] = new Carta(r);
        }
    }

    // Dibuja las cartas en el panel
    public void mostrar(JPanel pnl) {
        pnl.removeAll();
        int posicion = MARGEN + TOTAL_CARTAS * SEPARACION;
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i].mostrar(pnl, posicion, MARGEN);
            posicion = posicion - SEPARACION;
        }
        pnl.repaint();
    }

    // Busca grupos (pares, tercia, etc.) y halla las cartas sobrantes que no forman un grupo.
    public String getGrupos() {
        String resultado = "No se encontraron grupos \n";
        int[] contadores = new int[NombreCarta.values().length];
        totalSobrantesGrupos = 0;

        for (int i = 0; i < TOTAL_CARTAS; i++) {
            Carta c = cartas[i];
            int pos = c.getNombre().ordinal();
            contadores[pos] = contadores[pos] + 1;
        }

        boolean hayGrupos = false;
        for (int i = 0; i < contadores.length; i++) {
            if (contadores[i] >= 2) {
                hayGrupos = true;
            }
        }

        if (hayGrupos) {
            resultado = "Se hallaron los siguientes grupos:\n";
            for (int i = 0; i < contadores.length; i++) {
                int cantidad = contadores[i];
                if (cantidad >= 2) {
                    resultado = resultado + Grupo.values()[cantidad] + " de " + NombreCarta.values()[i] + "\n";
                } else {
                    if (cantidad == 1) {
                        for (int j = 0; j < TOTAL_CARTAS; j++) {
                            if (cartas[j].getNombre().ordinal() == i) {
                                sobrantesGrupos[totalSobrantesGrupos] = cartas[j];
                                totalSobrantesGrupos = totalSobrantesGrupos + 1;
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < TOTAL_CARTAS; i++) {
                sobrantesGrupos[totalSobrantesGrupos] = cartas[i];
                totalSobrantesGrupos = totalSobrantesGrupos + 1;
            }
        }
        return resultado;
    }
// NUEV FUNCIONALIDAD: Busco escaleras desde 2 cartas o mas de la misma pinta consecutivas, suma las cartas que forman una escalera
    public String getEscaleras() {
        String resultado = "No se encontraron escaleras \n";
        String[] nombresCartas = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        boolean hayEscaleras = false;
        totalSobrantesEscaleras = 0; 

        for (int p = 0; p < Pinta.values().length; p++) {
            Pinta pinta = Pinta.values()[p];
            boolean[] numeros = new boolean[13];

            for (int i = 0; i < TOTAL_CARTAS; i++) {
                Carta c = cartas[i];
                if (c.getPinta() == pinta) {
                    int pos = c.getNombre().ordinal();
                    numeros[pos] = true;
                }
            }

            int contador = 0;
            int inicio = -1;
            for (int i = 0; i < numeros.length; i++) {
                if (numeros[i] == true) {
                    if (contador == 0) {
                        inicio = i;
                    }
                    contador = contador + 1;
                } else {
                    if (contador >= 2) {
                        if (hayEscaleras == false) {
                            resultado = "Se hallaron las siguientes escaleras:\n";
                            hayEscaleras = true;
                        }
                        String cartaInicio = nombresCartas[inicio];
                        String cartaFin = nombresCartas[inicio + contador - 1];
                        resultado = resultado + Grupo.values()[contador] + " de " + pinta + ": desde " + cartaInicio + " hasta " + cartaFin + "\n";

                        // Guarda las cartas que forman la escalera
                        for (int k = inicio; k <= inicio + contador - 1; k++) {
                            for (int j = 0; j < TOTAL_CARTAS; j++) {
                                Carta c = cartas[j];
                                if (c.getPinta() == pinta){
                                 if(c.getNombre().ordinal() == k) {
                                    sobrantesEscaleras[totalSobrantesEscaleras] = c;
                                    totalSobrantesEscaleras = totalSobrantesEscaleras + 1;
                                }
                              }
                            }
                        }
                    }
                    contador = 0;
                }
            }
           //Si la escalera termina en la ultima carta, esto asegura que se genere el texto correspondiente
            if (contador >= 2) {
                if (hayEscaleras == false) {
                    resultado = "Se hallaron las siguientes escaleras:\n";
                    hayEscaleras = true;
                }
                String cartaInicio = nombresCartas[inicio];
                String cartaFin = nombresCartas[inicio + contador - 1];
                resultado = resultado + Grupo.values()[contador] + " de " + pinta + ": desde " + cartaInicio + " hasta " + cartaFin + "\n";

                    }
                }
                return resultado;
            }
    
    //Compara los sobrantes de los grupos y las de las escaleras, retorna las cartas sobrantes, permite el calculo de el puntaje.
    public String getSobrantesFinales() {
        totalSobrantesFinales = 0;
        String resultado = "\n No hay cartas sobrantes \n";

        for (int i = 0; i < totalSobrantesGrupos; i++) {
            Carta cg = sobrantesGrupos[i];
            boolean encontrada = false;
            for (int j = 0; j < totalSobrantesEscaleras; j++) {
                Carta ce = sobrantesEscaleras[j];
                if (cg.getNombre() == ce.getNombre()) {
                    if (cg.getPinta() == ce.getPinta()) {
                        encontrada = true;
                    }
                }
            }
            if (encontrada == false) {
                sobrantesFinales[totalSobrantesFinales] = cg;
                totalSobrantesFinales = totalSobrantesFinales + 1;
            }
        }

        if (totalSobrantesFinales > 0) {
            resultado = "\n Cartas sobrantes:\n";
            for (int i = 0; i < totalSobrantesFinales; i++) {
                Carta c = sobrantesFinales[i];
                resultado = resultado + c.getNombre() + " de " + c.getPinta() + "\n";
            }
        }
        return resultado;
    }

// Calcula el puntaje final del jugador

public String getPuntosSobrantesFinales() {
    int total = 0;
    for (int i = 0; i < totalSobrantesFinales; i++) {
        Carta c = sobrantesFinales[i];
        total = total + c.getValor();  //Aquí usamos el método de la clase Carta getValor
    }
    return "\n Puntaje:\n" + total;
    }

}


