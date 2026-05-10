// entidad que representa un vehiculo

import java.time.LocalDateTime;

public class Vehiculo {
    private String matricula;
    private LocalDateTime hora_entrada;
    private String id_ticket;

    public Vehiculo(String matricula, LocalDateTime hora_entrada, String id_ticket) {
        this.matricula = matricula;
        this.hora_entrada = hora_entrada;
        this.id_ticket = id_ticket;
    }

    public String get_matricula() {
        return matricula;
    }

    public void set_hora_entrada(LocalDateTime hora_entrada) {
        this.hora_entrada = hora_entrada;
    }

    public void set_id_ticket(String id_ticket) {
        this.id_ticket = id_ticket;
    }

    @Override
    public String toString() {
        return "Vehiculo{ matricula: " + matricula + ", hora de entrada: " + hora_entrada + ", id de ticket: " + id_ticket + " }";
    }
}