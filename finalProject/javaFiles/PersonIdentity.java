/**
 *
 * {@code PersonIdentity}
 *
 * This class is used to identify individuals from the family tree.
 * It contains two variable Int personId and String personName which help to create
 * the object of individual.This object helps use to identify the person from the family tree
 *
 *  @author Meet Patel (B00899516)
 *  Created on 2021-12-05
 *  @version 1.0.0
 *  @since 1.0,0
 */
public class PersonIdentity {

    private int id; // Stores id of the person
    private String name; // Stores the name of person

    // Constructor helps us to create the object
    public PersonIdentity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Method used to get person id
    public int getId() {
        return id;
    }

    // Method used to get person name
    public String getName() {
        return name;
    }

}







