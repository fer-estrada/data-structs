// Kevin Rojas
// arbol general con operaciones basicas
 
public class Main {
    public static void main(String[] args) {
        // crear el árbol con nodo raiz con valor '10'
        Arbol<Integer> arbol = new Arbol<>(10);
        Nodo<Integer> raiz = arbol.obtener_raiz();
 
        // agregar hijos directos de la raíz
        Nodo<Integer> nodo_b = new Nodo<>(20);
        raiz.agregar_hijo(nodo_b);
 
        Nodo<Integer> nodo_c = new Nodo<>(30);
        raiz.agregar_hijo(nodo_c);
 
        Nodo<Integer> nodo_d = new Nodo<>(40);
        raiz.agregar_hijo(nodo_d);
 
        // agregar hijos del segundo nivel
        nodo_b.agregar_hijo(new Nodo<>(40));
        nodo_b.agregar_hijo(new Nodo<>(50));
        nodo_c.agregar_hijo(new Nodo<>(60));
        nodo_c.agregar_hijo(new Nodo<>(70));
        nodo_d.agregar_hijo(new Nodo<>(80));
 
        System.out.println("Grado del árbol: " + arbol.grado());
        System.out.println("Primogénitos: ");
        arbol.escribePrimogenitos();
        System.out.println("Cantidad de hojas: " + arbol.cuantasHojas());
    }
}
