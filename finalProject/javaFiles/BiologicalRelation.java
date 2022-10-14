/**
 *{@code BiologicalRelation} This class specifies the cousinship and the level of removal in a relation
 *
 * @author Meet Patel (B00899516)
 * Created on 2021-12-05
 * @version 1.0.0
 * @since 1.0,0
 */

public class BiologicalRelation {

    // Stores the degree of removal
    private int degreeOfRemoval;

    // stores the degree of Cousinship
    private int degreeOfCousinship;

    // Constructor helps to create the object
    public BiologicalRelation(int degreeOfRemoval, int degreeOfCousinship) {
        this.degreeOfRemoval = degreeOfRemoval;
        this.degreeOfCousinship = degreeOfCousinship;
    }

    // Method to get degreeOfRemoval
    public int getDegreeOfRemoval() {
        return degreeOfRemoval;
    }

    // Method to set degreeOfRemoval
    public void setDegreeOfRemoval(int degreeOfRemoval) {
        this.degreeOfRemoval = degreeOfRemoval;
    }

    // method to get degreeOfCousinship
    public int getDegreeOfCousinship() {
        return degreeOfCousinship;
    }

    // Method to set degreeOfCousinship
    public void setDegreeOfCousinship(int degreeOfCousinship) {
        this.degreeOfCousinship = degreeOfCousinship;
    }
}