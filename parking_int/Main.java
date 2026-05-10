// kevin rojas
// simulador de parking inteligente con plataformas elevadoras por plaza
// cada plaza usa una pila para gestionar los vehiculos apilados
// las entradas y salidas se gestionan con colas independientes

import java.time.LocalDateTime;
import java.util.*;

public class Main {
    // numero de entradas, salidas y plazas disponibles en el parking
    static final int NUM_ENTRADAS = 2;
    static final int NUM_SALIDAS = 2;
    static final int NUM_PLAZAS = 20;

    // colas de entrada y salida, una por carril
    static Queue<Vehiculo>[] colasEntrada = new LinkedList[NUM_ENTRADAS];
    static Queue<String>[] colasSalida = new LinkedList[NUM_SALIDAS];

    // array de plazas y mapas para relacionar tickets con vehiculos
    static Plaza[] plazas = new Plaza[NUM_PLAZAS];
    static Map<String, Ticket> tickets = new HashMap<>();
    static Map<String, String> vehiculoTicket = new HashMap<>();
    static int ticketCount = 1;

    // inicializa las estructuras de datos del parking
    static void init() {
        for (int i = 0; i < NUM_ENTRADAS; i++) { colasEntrada[i] = new LinkedList<>(); }
        for (int i = 0; i < NUM_SALIDAS; i++) { colasSalida[i] = new LinkedList<>(); }
        for (int i = 0; i < NUM_PLAZAS; i++) { plazas[i] = new Plaza(i + 1); }
    }

    // encola un vehiculo en la entrada indicada
    static void solicitarEntrada(int entradaId, Vehiculo vehiculo) {
        if (entradaId < 0 || entradaId >= NUM_ENTRADAS) {
            System.err.println("Error: ID de entrada invalido");
            return;
        }

        colasEntrada[entradaId].offer(vehiculo);
        System.out.println("Vehiculo " + vehiculo.getMatricula() + " en cola de entrada " + (entradaId + 1));
    }

    // procesa todos los vehiculos en cola de entrada y les asigna plaza si hay disponible
    static void procesarEntradas() {
        boolean procesada = false;

        for (int i = 0; i < NUM_ENTRADAS; i++) {
            while (!colasEntrada[i].isEmpty()) {
                Plaza plaza = asignarPlaza();

                // si no hay plazas libres, se deja el resto en cola
                if (plaza == null) {
                    System.out.println("Aviso: parking lleno, vehiculos en cola de entrada " + (i + 1) + " esperando");
                    break;
                }

                Vehiculo v = colasEntrada[i].poll();

                // genera el ticket y lo asocia al vehiculo
                String tid = "T" + String.format("%03d", ticketCount++);
                LocalDateTime ahora = LocalDateTime.now();
                Ticket t = new Ticket(tid, v.getMatricula(), ahora);

                v.setIdTicket(tid);
                v.setHoraEntrada(ahora);

                tickets.put(t.getId(), t);
                vehiculoTicket.put(v.getMatricula(), t.getId());
                plaza.apilarVehiculo(v);

                System.out.println("Entrada " + (i + 1) + " -> " + v.getMatricula()
                    + " asignado a Plaza " + plaza.getId() + " | Ticket: " + tid);
                procesada = true;
            }
        }
        if (!procesada) { System.out.println("No hay vehiculos pendientes de entrada"); }
    }

    // elige la plaza con menos vehiculos apilados para minimizar bloqueos futuros
    static Plaza asignarPlaza() {
        Plaza mejor = null;

        for (Plaza p : plazas) {
            if (mejor == null || p.getCapOcupada() < mejor.getCapOcupada()) {
                mejor = p;
            }
        }

        return mejor;
    }

    // calcula el importe y marca el ticket como pagado
    static void pagarTicket(String tid) {
        Ticket t = tickets.get(tid);

        if (t == null) {
            System.err.println("Error: ticket " + tid + " no encontrado");
            return;
        }

        if (t.isPagado()) {
            System.out.println("Aviso: el ticket " + tid + " ya estaba pagado");
            return;
        }

        // redondea hacia arriba al numero de horas completas, minimo 1 hora
        LocalDateTime ahora = LocalDateTime.now();
        long minutos = java.time.Duration.between(t.getHoraEntrada(), ahora).toMinutes();
        long horas = (minutos / 60) + (minutos % 60 > 0 ? 1 : 0);
        double importe = Math.max(1.0, horas * 2.0);

        t.setPagado(true);
        t.setHoraPago(ahora);
        tickets.put(tid, t);

        System.out.printf("Ticket %s pagado. Tiempo: %d min -> %.2f€%n", tid, minutos, importe);
    }

    // encola una matricula en la cola de salida indicada, siempre que este registrada
    static void solicitarSalida(int salidaId, String matricula) {
        if (salidaId < 0 || salidaId >= NUM_SALIDAS) {
            System.err.println("Error: ID de salida invalido");
            return;
        }

        if (!vehiculoTicket.containsKey(matricula)) {
            System.err.println("Error: matricula " + matricula + " no registrada en el parking");
            return;
        }

        colasSalida[salidaId].offer(matricula);
        System.out.println("Matricula " + matricula + " en cola de salida " + (salidaId + 1));
    }

    // procesa las salidas pendientes, bloqueando las que tengan ticket sin pagar
    static void procesarSalidas() {
        boolean procesada = false;

        for (int j = 0; j < NUM_SALIDAS; j++) {
            if (colasSalida[j].isEmpty()) { continue; }

            // peek en lugar de poll: no se desencola hasta confirmar que el ticket esta pagado
            String mat = colasSalida[j].peek();
            String tid = vehiculoTicket.get(mat);

            if (tid == null) {
                System.err.println("Error: no hay ticket para matricula " + mat);
                colasSalida[j].poll();
                continue;
            }

            Ticket t = tickets.get(tid);

            // salida bloqueada si no se ha pagado
            if (!t.isPagado()) {
                System.out.println("Salida " + (j + 1) + " bloqueada: " + mat + " tiene ticket " + tid + " sin pagar");
                continue;
            }

            colasSalida[j].poll();
            Plaza plaza = localizarPlaza(mat);

            if (plaza == null) {
                System.err.println("Error: no se encontro la plaza del vehiculo " + mat);
                continue;
            }

            // retira el vehiculo de su pila y limpia el registro
            retirarDePila(plaza, mat);
            vehiculoTicket.remove(mat);

            System.out.println("Salida " + (j + 1) + " -> " + mat
                + " salio de Plaza " + plaza.getId() + " | Ticket: " + tid + " cerrado");
            procesada = true;
        }
        if (!procesada) { System.out.println("No se proceso ninguna salida"); }
    }

    // busca en que plaza esta aparcado el vehiculo con esa matricula
    static Plaza localizarPlaza(String matricula) {
        for (Plaza p : plazas) {
            for (Vehiculo v : p.getPlataforma()) {
                if (v.getMatricula().equalsIgnoreCase(matricula)) { return p; }
            }
        }

        return null;
    }

    // extrae el vehiculo objetivo de la pila, moviendo temporalmente los que esten encima
    static void retirarDePila(Plaza plaza, String matricula) {
        Stack<Vehiculo> aux = new Stack<>();

        // desapila hacia aux hasta que el objetivo quede en la cima
        while (!plaza.getPlataforma().isEmpty() && !plaza.vehiculoArriba().getMatricula().equalsIgnoreCase(matricula)) {
            aux.push(plaza.retirarVehiculo());
        }

        if (!plaza.getPlataforma().isEmpty()) {
            plaza.retirarVehiculo();
        } else {
            System.err.println("Error: vehiculo " + matricula + " no estaba en la plaza");
        }

        // devuelve los vehiculos que estaban encima en el mismo orden
        while (!aux.isEmpty()) {
            plaza.apilarVehiculo(aux.pop());
        }
    }

    // muestra el ticket asociado a una matricula si existe
    static void consultarTicket(String matricula) {
        String tid = vehiculoTicket.get(matricula);

        if (tid == null) {
            System.out.println("No hay vehiculo con matricula " + matricula + " en el parking");
            return;
        }

        System.out.println(tickets.get(tid));
    }

    // imprime el estado completo del parking: plazas, colas y tickets activos
    static void verEstado() {
        System.out.println("\n-- Plazas --");

        for (Plaza p : plazas) { System.out.println(p); }

        System.out.println("\n-- Colas de entrada --");

        for (int i = 0; i < NUM_ENTRADAS; i++) {
            System.out.println("Entrada " + (i + 1) + ": " + colasEntrada[i].size() + " vehiculo(s)");
        }

        System.out.println("\n-- Colas de salida --");

        for (int i = 0; i < NUM_SALIDAS; i++) {
            System.out.println("Salida " + (i + 1) + ": " + colasSalida[i].size() + " vehiculo(s)");
        }

        System.out.println("\n-- Tickets activos --");

        if (vehiculoTicket.isEmpty()) {
            System.out.println("(ninguno)");
        } else {
            for (String mat : vehiculoTicket.keySet()) {
                System.out.println(tickets.get(vehiculoTicket.get(mat)));
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
            opcion = leerInt(sc);

            switch (opcion) {
                case 1 -> {
                    System.out.print("Matricula del vehiculo: ");
                    String mat = sc.nextLine().trim().toUpperCase();
                    System.out.print("Numero de entrada (1-" + NUM_ENTRADAS + "): ");
                    int entrada = leerInt(sc) - 1;
                    solicitarEntrada(entrada, new Vehiculo(mat, null, null));
                }
                case 2 -> procesarEntradas();
                case 3 -> {
                    System.out.print("Matricula: ");
                    consultarTicket(sc.nextLine().trim().toUpperCase());
                }
                case 4 -> {
                    System.out.print("ID del ticket (ej. T001): ");
                    pagarTicket(sc.nextLine().trim().toUpperCase());
                }
                case 5 -> {
                    System.out.print("Matricula del vehiculo: ");
                    String mat = sc.nextLine().trim().toUpperCase();
                    System.out.print("Numero de salida (1-" + NUM_SALIDAS + "): ");
                    int salida = leerInt(sc) - 1;
                    solicitarSalida(salida, mat);
                }
                case 6 -> procesarSalidas();
                case 7 -> verEstado();
                case 0 -> System.out.println("Cerrando simulador");
                default -> System.err.println("Error: opcion no valida");
            }
        } while (opcion != 0);

        sc.close();
    }

    // fernando me enseño esto y dijo que confiara
    static int leerInt(Scanner sc) {
        int val = -1;

        try {
            val = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Error: introduce un numero valido");
        }

        return val;
    }
}
