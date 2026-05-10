// implementacion del arbol con metodos basicos de utilidad 

public class NaryTree<T> {
    private Node<T> root;

    public NaryTree(T root_data) {
        this.root = new Node<>(root_data);
    }

    public Node<T> get_root() {
        return root;
    }

    public int count_children(Node<T> node) {
        return node.count_children();
    }

    public void print_tree(Node<T> node, int indent) {
        // la idea es que empezara con indentacion '0' y luego
        // aumentara y bajara dependiendo si es un hijo del nodo
        // o el proximo en la lista, para poder imprimirlo y
        // realmente que se mire como un arbol (o intentarlo)
        System.out.println("    ".repeat(indent) + node.data);

        for(Node<T> child : node.children) {
            print_tree(child, indent + 1);
        }
    }

    public int grado() {
        return root.grado();
    }

    public void escribePrimogenitos() {
        root.escribePrimogenitos();
    }

    public int cuantasHojas() {
        return root.cuantasHojas();
    }
}