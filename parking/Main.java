import java.util.*;

public class Main {
    static final int E = 2;
    static final int S = 2;
    static final int P = 20;

    static Queue<Vehiculo>[] cola_entradas = new LinkedList[E];
    static Queue<String>[] cola_salidas = new LinkedList[S];
    static Plaza[] plazas = new Plaza[P];
    static Map<String, Ticket> tickets = new HashMap<>();
    static Map<String, String> vehiculo_ticket = new HashMap<>();
    static int ticket_count = 1;

    static void init() {
        for(int i = 0; i < E; i++) { cola_entradas[i] = new LinkedList<>(); }
        for(int i = 0; i < S; i++) { cola_salidas[i] = new LinkedList<>(); }
        for(int i = 0; i < P; i++) { plazas[i] = new Plaza(i + 1); }
    }

    static void solicitar_entrada(int ent_id, Vehiculo vehiculo) {
        if(ent_id < 0 || ent_id > E) {
            System.err.println("Error: ID invalido");
            return;
        }

        cola_entradas[ent_id].offer(vehiculo);
        System.out.println("Vehiculo " + vehiculo.get_matricula() + "en cola de entrada " + (ent_id + 1));
    }

    public static void main(String[] args) {
        
    }
}