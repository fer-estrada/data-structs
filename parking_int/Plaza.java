// entidad que representa una plaza del parking, con una pila para gestionar los vehiculos apilados

import java.util.Stack;

public class Plaza {
    private int id;
    private Stack<Vehiculo> plataforma;

    public Plaza(int id) {
        this.id = id;
        this.plataforma = new Stack<>();
    }

    public int getId() {
        return id;
    }

    public Stack<Vehiculo> getPlataforma() {
        return plataforma;
    }

    // devuelve el vehiculo en la cima sin retirarlo, o null si la pila esta vacia
    public Vehiculo vehiculoArriba() {
        return plataforma.isEmpty() ? null : plataforma.peek();
    }

    public void apilarVehiculo(Vehiculo vehiculo) {
        plataforma.push(vehiculo);
    }

    // retira y devuelve el vehiculo de la cima, o null si no hay ninguno
    public Vehiculo retirarVehiculo() {
        return plataforma.isEmpty() ? null : plataforma.pop();
    }

    // numero de vehiculos actualmente apilados en esta plaza
    public int getCapOcupada() {
        return plataforma.size();
    }

    @Override
    public String toString() {
        return "Plaza{ id: " + id + ", cantidad de vehiculos: " + plataforma.size()
        + ", vehiculo arriba: " + (vehiculoArriba() == null ? "ninguno" : vehiculoArriba().getMatricula()) + " }";
    }
}
