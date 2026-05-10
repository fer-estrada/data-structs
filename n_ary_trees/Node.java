// nodos para el arbol general

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    T data;
    List<Node<T>> children;

    public Node(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public T get_data() {
        return data;
    }

    public void add_child(Node<T> child) {
        children.add(child);
    }

    public int count_children() {
        return children.size();
    }

    // ejercicio 1:
    public int grado() {
        int max = children.size(); // default al tamaño del primer hijo que miramos

        for(Node<T> child : children) {
            max = Math.max(max, child.grado()); // comparacion con los otros nodos
        }

        return max;
    }

    // ejercicio 2:
    public void escribePrimogenitos() {
        if(!children.isEmpty()) {
            System.out.println(children.get(0).get_data()); // imprime el primogenito actual

            for(Node<T> child : children) {
                child.escribePrimogenitos(); // recursividad
            }
        }
    }

    // ejercicio 3:
    public int cuantasHojas() {
        if(children.isEmpty()) {
            return 1; // este nodo es una hoja
        }

        int count = 0;

        for(Node<T> child : children) {
            count += child.cuantasHojas(); // el contador aumenta con cada 'hoja' que encontramos
        }

        return count; 
    }
}