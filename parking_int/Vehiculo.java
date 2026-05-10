// entidad que representa un vehiculo que entra o sale del parking

import java.time.LocalDateTime;

public class Vehiculo {
    private String matricula;
    private LocalDateTime horaEntrada;
    private String idTicket;

    public Vehiculo(String matricula, LocalDateTime horaEntrada, String idTicket) {
        this.matricula = matricula;
        this.horaEntrada = horaEntrada;
        this.idTicket = idTicket;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public void setIdTicket(String idTicket) {
        this.idTicket = idTicket;
    }

    @Override
    public String toString() {
        return "Vehiculo{ matricula: " + matricula + ", hora de entrada: " + horaEntrada + ", id de ticket: " + idTicket + " }";
    }
}
