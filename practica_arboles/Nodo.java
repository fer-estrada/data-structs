// nodos para el arbol general
 
import java.util.ArrayList;
import java.util.List;
 
public class Nodo<T> {
    T dato;
    List<Nodo<T>> hijos;
 
    public Nodo(T dato) {
        this.dato = dato;
        this.hijos = new ArrayList<>();
    }
 
    public T obtener_dato() {
        return dato;
    }
 
    public void agregar_hijo(Nodo<T> hijo) {
        hijos.add(hijo);
    }
 
    public int contar_hijos() {
        return hijos.size();
    }
 
    // devuelve el grado máximo encontrado en el subarbol
    public int grado() {
        int maximo = hijos.size();
 
        for (Nodo<T> hijo : hijos) {
            maximo = Math.max(maximo, hijo.grado());
        }
 
        return maximo;
    }
 
    // imprime el primogénito de cada nodo que tenga hijos
    public void escribePrimogenitos() {
        if (!hijos.isEmpty()) {
            System.out.println(hijos.get(0).obtener_dato());
 
            for (Nodo<T> hijo : hijos) {
                hijo.escribePrimogenitos();
            }
        }
    }
 
    // cuenta las hojas (nodos sin hijos) en el subarbol
    public int cuantasHojas() {
        if (hijos.isEmpty()) {
            return 1;
        }
 
        int cuenta = 0;
 
        for (Nodo<T> hijo : hijos) {
            cuenta += hijo.cuantasHojas();
        }
 
        return cuenta;
    }
}