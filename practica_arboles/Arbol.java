// implementacion y metodos del arbol

public class Arbol<T> {
    private Nodo<T> raiz;

    public Arbol(T dato_raiz) {
        this.raiz = new Nodo<>(dato_raiz);
    }

    public Nodo<T> obtener_raiz() {
        return raiz;
    }

    public int contar_hijos(Nodo<T> nodo) {
        return nodo.contar_hijos();
    }

    // devuelve el grado del árbol
    public int grado() {
        return raiz.grado();
    }

    // llama el metodo 'escribePrimogenitos' a la raiz del arbol
    public void escribePrimogenitos() {
        raiz.escribePrimogenitos();
    }

    // devuelva la cantidad de hojas del arbol
    public int cuantasHojas() {
        return raiz.cuantasHojas();
    }
}