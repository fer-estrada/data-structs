// fernando estrada
// simulador de parking inteligente con plataformas elevadoras (pilas) por plaza,
// colas de entrada/salida, gestion de tickets y politica de asignacion inteligente

import java.time.LocalDateTime;
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
        if(ent_id < 0 || ent_id >= E) {
            System.err.println("Error: ID de entrada invalido");
            return;
        }

        cola_entradas[ent_id].offer(vehiculo);
        System.out.println("Vehiculo " + vehiculo.get_matricula() + " en cola de entrada " + (ent_id + 1));
    }

    static void procesar_entradas() {
        boolean procesada = false;

        for(int i = 0; i < E; i++) {
            while(!cola_entradas[i].isEmpty()) {
                Plaza plaza = asignar_plaza();

                if(plaza == null) {
                    System.out.println("Aviso: parking lleno, vehiculos en cola de entrada " + (i + 1) + " esperando");
                    break;
                }

                Vehiculo v = cola_entradas[i].poll();

                String tid = "T" + String.format("%03d", ticket_count++);
                LocalDateTime ahora = LocalDateTime.now();
                Ticket t = new Ticket(tid, v.get_matricula(), ahora);

                v.set_id_ticket(tid);
                v.set_hora_entrada(ahora);

                tickets.put(t.get_id(), t);
                vehiculo_ticket.put(v.get_matricula(), t.get_id());
                plaza.apilar_vehiculo(v);

                System.out.println("Entrada " + (i + 1) + " -> " + v.get_matricula()
                    + " asignado a Plaza " + plaza.get_id() + " | Ticket: " + tid);
                procesada = true;
            }
        }
        if(!procesada) { System.out.println("No hay vehiculos pendientes de entrada"); }
    }

    // plaza con menos vehiculos apilados == minimiza bloqueos futuros
    static Plaza asignar_plaza() {
        Plaza mejor = null;

        for(Plaza p : plazas) {
            if(mejor == null || p.cap_ocupada() < mejor.cap_ocupada()) {
                mejor = p;
            }
        }

        return mejor;
    }

    static void pagar_ticket(String tid) {
        Ticket t = tickets.get(tid);

        if(t == null) {
            System.err.println("Error: ticket " + tid + " no encontrado");
            return;
        }

        if(t.is_pagado()) {
            System.out.println("Aviso: el ticket " + tid + " ya estaba pagado");
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        long minutos = java.time.Duration.between(t.get_hora_entrada(), ahora).toMinutes();
        long horas = (minutos / 60) + (minutos % 60 > 0 ? 1 : 0);
        double importe = Math.max(1.0, horas * 2.0);

        t.set_pagado(true);
        t.set_hora_pago(ahora);
        tickets.put(tid, t);

        System.out.printf("Ticket %s pagado. Tiempo: %d min -> %.2f€%n", tid, minutos, importe);
    }

    static void solicitar_salida(int sal_id, String matricula) {
        if(sal_id < 0 || sal_id >= S) {
            System.err.println("Error: ID de salida invalido");
            return;
        }

        if(!vehiculo_ticket.containsKey(matricula)) {
            System.err.println("Error: matricula " + matricula + " no registrada en el parking");
            return;
        }

        cola_salidas[sal_id].offer(matricula);
        System.out.println("Matricula " + matricula + " en cola de salida " + (sal_id + 1));
    }

    static void procesar_salidas() {
        boolean procesada = false;

        for(int j = 0; j < S; j++) {
            if(cola_salidas[j].isEmpty()) { continue; }

            // no desencolar hasta confirmar que esta pagado
            String mat = cola_salidas[j].peek();
            String tid = vehiculo_ticket.get(mat);

            if(tid == null) {
                System.err.println("Error: no hay ticket para matricula " + mat);
                cola_salidas[j].poll();
                continue;
            }

            Ticket t = tickets.get(tid);

            if(!t.is_pagado()) {
                System.out.println("Salida " + (j + 1) + " bloqueada: " + mat + " tiene ticket " + tid + " sin pagar");
                continue;
            }

            cola_salidas[j].poll();
            Plaza plaza = localizar_plaza(mat);

            if(plaza == null) {
                System.err.println("Error: no se encontro la plaza del vehiculo " + mat);
                continue;
            }

            retirar_de_pila(plaza, mat);
            vehiculo_ticket.remove(mat);

            System.out.println("Salida " + (j + 1) + " -> " + mat
                + " salio de Plaza " + plaza.get_id() + " | Ticket: " + tid + " cerrado");
            procesada = true;
        }
        if(!procesada) { System.out.println("No se proceso ninguna salida"); }
    }

    static Plaza localizar_plaza(String matricula) {
        for(Plaza p : plazas) {
            for(Vehiculo v : p.get_plataforma()) {
                if(v.get_matricula().equalsIgnoreCase(matricula)) { return p; }
            }
        }

        return null;
    }

    static void retirar_de_pila(Plaza plaza, String matricula) {
        Stack<Vehiculo> aux = new Stack<>();

        // mover vehiculos a aux hasta que el objetivo quede en cima
        while(!plaza.get_plataforma().isEmpty() && !plaza.vehiculo_arriba().get_matricula().equalsIgnoreCase(matricula)) {
            aux.push(plaza.retirar_vehiculo());
        }

        if(!plaza.get_plataforma().isEmpty()) {
            plaza.retirar_vehiculo();
        } else {
            System.err.println("Error: vehiculo " + matricula + " no estaba en la plaza");
        }

        // restaurar el orden original de los vehiculos que estaban encima
        while(!aux.isEmpty()) {
            plaza.apilar_vehiculo(aux.pop());
        }
    }

    static void consultar_ticket(String matricula) {
        String tid = vehiculo_ticket.get(matricula);

        if(tid == null) {
            System.out.println("No hay vehiculo con matricula " + matricula + " en el parking");
            return;
        }

        System.out.println(tickets.get(tid));
    }

    static void ver_estado() {
        System.out.println("\n-- Plazas --");

        for(Plaza p : plazas) { System.out.println(p); }

        System.out.println("\n-- Colas de entrada --");

        for(int i = 0; i < E; i++) {
            System.out.println("Entrada " + (i + 1) + ": " + cola_entradas[i].size() + " vehiculo(s)");
        }

        System.out.println("\n-- Colas de salida --");

        for(int i = 0; i < S; i++) {
            System.out.println("Salida " + (i + 1) + ": " + cola_salidas[i].size() + " vehiculo(s)");
        }

        System.out.println("\n-- Tickets activos --");

        if(vehiculo_ticket.isEmpty()) {
            System.out.println("(ninguno)");
        } else {
            for(String mat : vehiculo_ticket.keySet()) {
                System.out.println(tickets.get(vehiculo_ticket.get(mat)));
            }
        }

        System.out.println();
    }

    public static void main(String[] args) {
        init();
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n=== SIMULADOR PARKING ===");
            System.out.println("1) Solicitar entrada (encolar vehiculo en una entrada)");
            System.out.println("2) Procesar entradas (meter vehiculos si procede)");
            System.out.println("3) Consultar ticket por matricula");
            System.out.println("4) Pagar ticket");
            System.out.println("5) Solicitar salida (encolar salida por una salida)");
            System.out.println("6) Procesar salidas (solo pagados)");
            System.out.println("7) Ver estado del parking");
            System.out.println("0) Salir");
            System.out.print("Opcion: ");
            opcion = leer_int(sc);

            switch(opcion) {
                case 1 -> {
                    System.out.print("Matricula del vehiculo: ");
                    String mat = sc.nextLine().trim().toUpperCase();
                    System.out.print("Numero de entrada (1-" + E + "): ");
                    int entrada = leer_int(sc) - 1;
                    solicitar_entrada(entrada, new Vehiculo(mat, null, null));
                }
                case 2 -> procesar_entradas();
                case 3 -> {
                    System.out.print("Matricula: ");
                    consultar_ticket(sc.nextLine().trim().toUpperCase());
                }
                case 4 -> {
                    System.out.print("ID del ticket (ej. T001): ");
                    pagar_ticket(sc.nextLine().trim().toUpperCase());
                }
                case 5 -> {
                    System.out.print("Matricula del vehiculo: ");
                    String mat = sc.nextLine().trim().toUpperCase();
                    System.out.print("Numero de salida (1-" + S + "): ");
                    int salida = leer_int(sc) - 1;
                    solicitar_salida(salida, mat);
                }
                case 6 -> procesar_salidas();
                case 7 -> ver_estado();
                case 0 -> System.out.println("Cerrando simulador");
                default -> System.err.println("Error: opcion no valida");
            }
        } while(opcion != 0);

        sc.close();
    }

    // i use nextLine() instead of nextInt() to consume the newline and avoid scanner bugs
    // parseInt then handles the conversion with a catch for non-numeric input
    // i am a genius (https://stackoverflow.com/questions/2942686/taking-integer-input-in-java)
    static int leer_int(Scanner sc) {
        int val = -1;

        try {
            val = Integer.parseInt(sc.nextLine().trim());
        } catch(NumberFormatException e) {
            System.err.println("Error: introduce un numero valido");
        }

        return val;
    }
}