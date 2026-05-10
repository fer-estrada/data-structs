// entidad que representa el ticket generado al entrar un vehiculo al parking

import java.time.LocalDateTime;

public class Ticket {
    private String id;
    private String matricula;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaPago;
    private boolean pagado;

    public Ticket(String id, String matricula, LocalDateTime horaEntrada) {
        this.id = id;
        this.matricula = matricula;
        this.horaEntrada = horaEntrada;
        this.horaPago = null;
        this.pagado = false;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public void setHoraPago(LocalDateTime horaPago) {
        this.horaPago = horaPago;
    }

    @Override
    public String toString() {
        return "Ticket{ id: " + id + ", matricula: " + matricula + ", hora de entrada: " + horaEntrada
        + ", hora de pago: " + (horaPago == null ? "pendiente" : horaPago) + ", pagado: " + pagado + " }";
    }
}
