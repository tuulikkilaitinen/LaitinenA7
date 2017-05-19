package sdccd.edu.laitinena7;

/**
 * Created by Tuulikki Laitinen on 5/18/2017.
 */

public class Book {

    private String id;
    private String name;
    private String author;
    private String year;
    private String price;
    private String ownerId;

    public Book () {}

    public Book (String id,
                 String name,
                 String author,
                 String year,
                 String price,
                 String ownerId) {

        this.id = id;
        this.name = name;
        this.author = author;
        this.year = year;
        this.price = price;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
