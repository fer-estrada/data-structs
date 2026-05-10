import java.time.LocalDateTime;

public class Ticket {
    private String id;
    private String matricula;
    private LocalDateTime hora_entrada;
    private LocalDateTime hora_pago;
    private boolean pagado;

    public Ticket(String id, String matricula, LocalDateTime hora_entrada) {
        this.id = id;
        this.matricula = matricula;
        this.hora_entrada = hora_entrada;
        this.hora_pago = null;
        this.pagado = false;
    }

    public String get_id() {
        return id;
    }

    public LocalDateTime get_hora_entrada() {
        return hora_entrada;
    }

    public boolean is_pagado() {
        return pagado;
    }

    public void set_pagado(boolean pagado) {
        this.pagado = pagado;
    }

    public void set_hora_pago(LocalDateTime hora_pago) {
        this.hora_pago = hora_pago;
    }

    @Override
    public String toString() {
        return "Ticket{ id: " + id + ", matricula: " + matricula + ", hora de entrada: " + hora_entrada 
        + ", hora de pago: " + (hora_pago == null ? "pendiente" : hora_pago) + ", pagado: " + pagado + " }";
    }
}