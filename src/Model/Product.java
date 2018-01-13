package Model;

/**
 * Created by angel on 14/12/2017.
 */
public class Product {

    /**
     * Identificador de producto
     */
    private int id;

    /**
     * Nombre del producto
     */
    private String name;

    /**
     * Construye un nuevo producto con el identificador y el nombre especificados
     * @param id Identificador de producto
     * @param name Nombre del producto
     */
    public Product(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
