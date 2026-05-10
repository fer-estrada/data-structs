// fernando estrada
// practica de arboles generales
// las 3 implementaciones de los ejercicios estan
// en el archivo 'Node.java'

public class Main {
    public static void main(String[] args) {
        NaryTree<Integer> tree = new NaryTree<>(10);
        Node<Integer> root = tree.get_root();

        Node<Integer> node_B = new Node<>(20);
        root.add_child(node_B);

        Node<Integer> node_C = new Node<>(30);
        root.add_child(node_C);

        Node<Integer> node_D = new Node<>(40);
        root.add_child(node_D);
        
        node_B.add_child(new Node<>(40));
        node_B.add_child(new Node<>(50));
        node_C.add_child(new Node<>(60));
        node_C.add_child(new Node<>(70));
        node_D.add_child(new Node<>(80));

        System.out.println("Grado del arbol: " + tree.grado());
        System.out.println("Escribe primogenitos: ");
        tree.escribePrimogenitos();
        System.out.println("Cuantas hojas: " + tree.cuantasHojas());
        System.out.println("El arbol entero: ");
        tree.print_tree(root, 0);
    }
}
