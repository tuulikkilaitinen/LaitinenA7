package sdccd.edu.laitinena7.Utils;

import java.io.Serializable;

/**
 * Created by Tuulikki Laitinen on 5/18/2017.
 */

public class Book implements Serializable {

    private String id;
    private String name;
    private String author;
    private String year;
    private String price;
    private String ownerId;
    private String ownerName;
    private String imagePath;

    public Book () {}

    public Book (String id,
                 String name,
                 String author,
                 String year,
                 String price,
                 String ownerId,
                 String ownerName,
                 String imagePath) {

        this.id = id;
        this.name = name;
        this.author = author;
        this.year = year;
        this.price = price;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public String getYear() {
        return year;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getId() {
        return id;
    }

    public void setOwnderId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBookId() {
        return id;
    }
}
