import java.util.Stack;

public class Plaza {
    private int id;
    private Stack<Vehiculo> plataforma;

    public Plaza(int id) {
        this.id = id;
        this.plataforma = new Stack<>();
    }

    public int get_id() {
        return id;
    }

    public Stack<Vehiculo> get_plataforma() {
        return plataforma;
    }

    public Vehiculo vehiculo_arriba() {
        return plataforma.isEmpty() ? null : plataforma.peek();
    }

    public void apilar_vehiculo(Vehiculo vehiculo) {
        plataforma.push(vehiculo);
    }

    public Vehiculo retirar_vehiculo() {
        return plataforma.isEmpty() ? null : plataforma.pop();
    }

    public int cap_ocupada() {
        return plataforma.size();
    }

    @Override
    public String toString() {
        return "Plaza{ id: " + id + ", cantidad de vehiculos: " + plataforma.size()
        + ", vehiculo arriba: " + (vehiculo_arriba() == null ? "ninguno" : vehiculo_arriba().get_matricula()) + " }";
    }
}