import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * {@code Genealogy} trace and track the family realtions. This class only deals with biological
 * family relations. This class has three major component
 *
 * 1. Family Tree Management
 * In this component it will take information about the individual and store it in the database
 * This component also stores the relationship between the individuals.
 *
 * 2. Media archive Management
 * This component stores the metadata of the media in to the database. The media can be in \
 * any form like image or video.
 *
 * 3. Reporting
 * At last this component reports the information as required. It will answers some of the
 * genealodists questions
 *
 * @author Meet Patel (B00899516)
 * Created on 2021-12-05
 * @version 1.0.0
 * @since 1.0,0
 */

public class Genealogy {

/**
 * Tables And Columns for Family Tree Management
 */

    // Database (patel13) table with name "personNameInfo"
    private static final String TABLE_PERSON_NAME_INFO = "personNameInfo";

    // Database (patel13) table with name "personAttributeInfo"
    private static final String TABLE_PERSON_ATTRIBUTE_INFO = "personAttributeInfo";

    // Database (patel13) table with name "personReference"
    private static final String TABLE_PERSON_REFERENCE = "personReference";

    // Database (patel13) table with name "personNote"
    private static final String TABLE_PERSON_NOTE = "personNote";

    // Database (patel13) table with name "recordChild"
    private static final String TABLE_PARENT_CHILD = "recordChild";

    // Table (PersonNameInfo) Column with name "personId". Its a Primary Key
    private static final String COLUMN_PERSON_ID = "personId";

    // Table (PersonNameInfo) Column with name "personName"
    private static final String COLUMN_PERSON_NAME = "personName";

    // Table (personAttributeInfo) Column with name "personDOB"
    private static final String COLUMN_ATTRIBUTE_DOB = "personDOB";

    // Table (personAttributeInfo) Column with name "personDOD"
    private static final String COLUMN_ATTRIBUTE_DOD = "personDOD";

    // Table (personAttributeInfo) Column with name "personGender"
    private static final String COLUMN_ATTRIBUTE_GENDER = "personGender";

    // Table (personAttributeInfo) Column with name "personOccupation"
    private static final String COLUMN_ATTRIBUTE_OCCUPATION = "personOccupation";

    /** Table (personAttributeInfo) Column with name "personId". Its a Foreign Key from
     * Table personNameInfo with column name "personId"
     */
    private static final String COLUMN_ATTRIBUTE_FK_PERSONID = "personId";

    // Table (personNote) column with name "personNote"
    private static final String COLUMN_NOTE = "personNote";

    /** Table (personNote) Column with name "personId". Its a Foreign Key from
     * Table personNameInfo with column name "personId"
     */
    private static final String COLUMN_NOTE_FK_PERSONID = "personId";

    // Table (personNote) column with name "lastUpdated". It records the timestamps when data is added
    private static final String COLUMN_NOTE_LASTUPDATED = "lastUpdated";

    /** Table (personReference) column with name "personId".Its a Foreign Key from
     * Table personNameInfo with column name "personId"
     */
    private static final String COLUMN_REFERENCE_FK_PERSONID = "personId";

    // Table (personReference) column with name "personReference"
    private static final String COLUMN_REFERENCE = "personReference";

    // Table (personReference) column with name "lastUpdated". It records the timestamps when data is added
    private static final String COLUMN_REFERENCE_LASTUPDATED = "lastUpdated";

    private static final String COLUMN_PARENT = "parent";

    private static final String COLUMN_CHILD = "child";


/**
 * Tables And Columns for Media Archive Management
 */
    // Database (patel13) table with name "mediaNameInfo"
    private static final String TABLE_MEDIA_NAME_INFO = "mediaNameInfo";

    // Database (patel13) table with name "mediaAttributeInfo"
    private static final String TABLE_MEDIA_ATTRIBUTE_INFO = "mediaAttributeInfo";

    // Database (patel13) table with name "mediaPersonRelation"
    private static final String TABLE_MEDIA_PERSON_RELATION = "mediaPersonRelation";

    // Database (patel13) table with name "mediaTag"
    private static final String TABLE_MEDIA_TAG = "mediaTag";

    // Database (patel13) table with name "recordPartneringDissolution"
    private static final String TABLE_RECORD_RELATION = "recordPartneringDissolution";

    // Table (mediaNameInfo) column with name "mediaId". Its the primary key
    private static final String COLUMN_MEDIA_ID = "mediaId";

    // Table (mediaNameInfo) column with name "mediaName"
    private static final String COLUMN_MEDIA_NAME = "mediaName";

    // Table (mediaNameInfo) column with name "mediaLocation"
    private static final String COLUMN_MEDIA_LOCATION = "mediaLocation";

    // Table (mediaAttributeInfo) column with name "mediaYear".
    private static final String COLUMN_ATTRIBUTE_YEAR = "mediaYear";

    // Table (mediaAttributeInfo) column with name "mediaDate"
    private static final String COLUMN_ATTRIBUTE_DATE = "mediaDate";

    // Table (mediaAttributeInfo) column with name "mediaCity"
    private static final String COLUMN_ATTRIBUTE_CITY = "mediaCity";

    // Table (mediaAttributeInfo) column with name "mediaYear"
    private static final String COLUMN_ATTRIBUTE_FK_MEDIAID = "mediaId";

    /** Table (mediaPersonRelation) column with name "mediaId".Its a Foreign Key from
     * Table mediaNameInfo with column name "mediaId"
     */
    private static final String COLUMN_FK_MEDIA_ID = "mediaId";

    /** Table (mediaPersonRelation) column with name "personId".Its a Foreign Key from
     * Table personNameInfo with column name "personId"
     */
    private static final String COLUMN_FK_PERSON_ID = "personId";

    // Table (mediaTag) column with name "mediaTag"
    private static final String COLUMN_MEDIA_TAG = "mediaTag";

    /** Table (mediaTag) column with name "mediaId".Its a Foreign Key from
     * Table mediaNameInfo with column name "mediaId"
     */
    private static final String COLUMN_TAG_FK_MEDIA_ID = "mediaId";

    // Table (mediaPersonRelation) column with name "partner_1"
    private static final String COLUMN_PARTNER_1 = "partner_1";

    // Table (mediaPersonRelation) column with name "partner_2"
    private static final String COLUMN_PARTNER_2 = "partner_2";

    // Table (mediaPersonRelation) column with name "relationship"
    private static final String COLUMN_RELATIONSHIP = "relationship";

    // This Map stores the Ids of the person in Keys and Object of the Person in Values
    public Map<Integer, PersonIdentity> personMap = new HashMap<>();

    // This Map stares the ids of the file in keys and Objects of the File in Values
    public Map<Integer, FileIdentifier> fileMap = new HashMap<>();

    // Connection object
    private Connection conn;

    // Used for showing the status of two individuals
    private final String partnering = "Partnering";
    private final String dissolution = "Dissolution";

    // Genealogy Constructor
    // Used to create the object of data that is already present in Database
    public Genealogy() {
        addDatabasePerson();
        addDatabaseMedia();
    }

    /**
     * {@code open} opens the connection with the database.
     * It will loads the login.properties file which contains the Username, Password and Database Name
     *
     * @throws IOException
     *         If unable to laod login.properties file
     *
     * @throws SQLException
     *         If there is any error while connectiong with the database
     */
    public void open() {
        Properties properties = new Properties();

        try {
            // it will load the login.properties file
            properties.load(new FileInputStream("Final_Project/src/login.properties"));
        } catch (IOException e) {
            System.out.println("Error Message - " + e.getMessage());
        }
        //fetch the username of the databse from login.properties file
        String USER = properties.getProperty("USERNAME");

        // fetch the password of the database from login.properties file
        String PASSWORD = properties.getProperty("PASSWORD");

        // fetch the database name from login.properties file
        String DATABASE_NAME = properties.getProperty("DATABASE_NAME");

        // Database Path
        String CONNECTION_STRING = "jdbc:mysql://db.cs.dal.ca:3306/" + DATABASE_NAME + "?serverTimezone=UTC";
        try {

            /** Connection Instance that uses Driver Manager to make connection with dataabse
             */
            conn = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
        } catch (SQLException e) {
            // prints the error message if unable to connect with the database
            System.out.println("Couldn't connect to database; " + e.getMessage());
        }
    }

    /**
     * {@code addDatabasePerson} creates the object of the the data which is already present
     * in the database. It will create a object which consist of person id and name and then store
     * that object in the map.This method will be called when the object of this class is created
     *
     */
    private void addDatabasePerson() {
        open(); // opens the connection with connection with database
        try (Statement statement = conn.createStatement()) {

            //Columns of the table will be stored in the ResultSet
            ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_PERSON_NAME_INFO);

            // It will iterate the result set and fetch the value one by one and
            // then create create the object and stroe it in the map
            while (rs.next()) {
                int Id = rs.getInt(1);
                String Name = rs.getString(2);
                personMap.put(Id, new PersonIdentity(Id, Name));
            }
            //close the connection with database
            conn.close();
        } catch (SQLException e) {
            //throes the exception if tehre is any SQL error
            System.out.println("Error Message: " + e.getMessage());
        }
    }

    /**
     * {@code addDatabaseMedia} creates the object of the the data which is already present
     * in the database. It will create a object which consist of media id, file name and
     * file location and then store that object in the map.This method will be
     * called when the object of this class is created
     *
     */
    private void addDatabaseMedia() {
        open();// opens the connection with connection with database
        try (Statement statement = conn.createStatement()) {

            //Execute the query and stores set of rows based on query from the database
            ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_MEDIA_NAME_INFO);

            /** It will iterate the result set and fetch the value one by one and
             * then create create the object and store it in the map
             */
            while (rs.next()) {
                int id = rs.getInt(1); // stores the Media id
                String name = rs.getString(2); // Stores the filename
                String location = rs.getString(3); // stores the file location
                fileMap.put(id, new FileIdentifier(id, name, location)); // creates the object and stores it in the map
            }
            //close the connection with database
            conn.close();
        } catch (SQLException e) {
            //throes the exception if tehre is any SQL error
            System.out.println("Error Messsage - " + e.getMessage());
        }
    }

    /**
     *{@code PersonIdentity} Add an individual to the family tree.
     *
     * @param name takes the name of the person as parameter
     * @return PersonIdentity object.
     */
    public PersonIdentity addPerson(String name) {
        // Opensthe connection with the database
        open();

        // variable of PersonIdentity type to store PersonIdentity object
        PersonIdentity person;

        // retruns null if users enters null or an empty string
        if (name == null || name.equalsIgnoreCase("null") || name.isEmpty() || name.isBlank()){
            System.out.println("Name is Empty or Null");
            return null;
        }

        // Throws Exception if there is any error
        try (Statement statement = conn.createStatement()) {

            //Execute the query and stores set of rows based on query from the database
            ResultSet rs = statement.executeQuery("SELECT MAX(" + COLUMN_PERSON_ID + ") FROM " + TABLE_PERSON_NAME_INFO);

            // Moves the resultset pointer to the first row
            rs.next();
            int personId = rs.getInt(1) + 1;

            // It will insert the name into the table
            statement.execute("insert into " + TABLE_PERSON_NAME_INFO + " (" + COLUMN_PERSON_NAME + " ) " + "VALUES('" + name + "')");
            System.out.println(name + " added successfully with id " + personId);

            // Create the object containing personId and name
            person = new PersonIdentity(personId, name);

            // Store that object into the map
            personMap.put(personId, person);

            // Close the Statement
            statement.close();

            //Close the connection with database
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error Message: " + e.getMessage());
            // retrun null if there is any error
            return null;
        }

        //retrun object
        return person;
    }


    /**
     *{@code checkPersonId} It will check whether the personId exists or not
     *
     * @param id takes id od the person as input
     * @return False if personId does not exits else return true
     */
    private boolean checkPersonId(int id) {
        // Open the connection with the database
        open();

        // Throws Exception if there is any error
        try (Statement statement = conn.createStatement()) {
            //Execute the query and stores set of rows based on query from the database
            ResultSet rs = statement.executeQuery("SELECT " + COLUMN_ATTRIBUTE_FK_PERSONID +
                    " FROM " + TABLE_PERSON_ATTRIBUTE_INFO);

            //Iterate the resultset
            while (rs.next()) {
                /**
                 * It will compare the id with every record in the resuktset
                 * if the it matches then it will return true
                 */
                if (rs.getInt(1) == id) {
                    return true;
                }
            }
        } catch (SQLException e) {
            // retrun false if there is any error
            System.out.println("Error Message: " + e.getMessage());
            return false;
        }

        // If no match found then it will retrun false
        return false;
    }

    /**
     * {@code recordAttributes} It stores the person infromation in family tree database
     *
     * @param person takes PersonIdentity object as input
     * @param attributes takes map as input. The keys will be attribute name
     *                  or database columns and value is the data. It stores values as String
     * @return true if all the attributes were stored in database else return false
     */
    public boolean recordAttributes(PersonIdentity person, Map<String, String> attributes) {

        // Opens the connection with the database
        open();

        // Retruns null if person object is null
        if (person == null) {
            System.out.println("Entered person doesn't exists. Cannot able to add attributes");
            return false;
        }

        // If DOB is null or empty than it will store null in the attribute value
        if (attributes.get("personDOB") == null || attributes.get("personDOB").equals("null") || attributes.get("personDOB").isBlank() || attributes.get("personDOB").isEmpty()){
            attributes.put("personDOB", null);
        }

        // If Gender is null or empty than it will store null in the attribute value
        if (attributes.get("personGender") == null || attributes.get("personGender").equals("null") || attributes.get("personGender").isBlank() || attributes.get("personGender").isEmpty()){
            attributes.put("personGender", null);
        }

        // If Occupation is null or empty than it will store null in the attribute value
        if (attributes.get("personOccupation") == null || attributes.get("personOccupation").equals("null") || attributes.get("personOccupation").isBlank() || attributes.get("personOccupation").isEmpty()){
            attributes.put("personOccupation", null);
        }

        // Retruns false if person id does not exists
        if (checkPersonId(person.getId())) {
            try {
                // Query to Update the attributes
                String Update_Query = "UPDATE "+TABLE_PERSON_ATTRIBUTE_INFO+" SET "+COLUMN_ATTRIBUTE_DOB+
                        " = ?,"+COLUMN_ATTRIBUTE_DOD+" = ?,"+
                        COLUMN_ATTRIBUTE_GENDER+" = ?, "+COLUMN_ATTRIBUTE_OCCUPATION+
                        " = ? WHERE "+COLUMN_ATTRIBUTE_FK_PERSONID+" = ?";

                PreparedStatement preparedStatement = conn.prepareStatement(Update_Query);
                preparedStatement.setString(1, attributes.get("personDOB"));
                preparedStatement.setString(2, attributes.get("personDOD"));
                preparedStatement.setString(3, attributes.get("personGender"));
                preparedStatement.setString(4, attributes.get("personOccupation"));
                preparedStatement.setInt(5, person.getId());
                // Execute the query. It will update attribute information of specified mediaId in the database
                preparedStatement.executeUpdate();

                System.out.println("Data Updated successfully for person Id "+person.getId());
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error Message: "+e.getMessage());
                return false;
            }
        } else {
            // Throws Exception if there is any error
            try {

                // Query to Insert the attributes
                String Insert_Query = "INSERT INTO " + TABLE_PERSON_ATTRIBUTE_INFO + " ("
                        + COLUMN_ATTRIBUTE_DOB + ", " + COLUMN_ATTRIBUTE_DOD + " ," + COLUMN_ATTRIBUTE_GENDER + ", " +
                        "" + COLUMN_ATTRIBUTE_OCCUPATION + ", " + COLUMN_ATTRIBUTE_FK_PERSONID + " ) "
                        + "VALUES(?, ? , ?, ?, ?)";


                PreparedStatement preparedStatement = conn.prepareStatement(Insert_Query);
                preparedStatement.setString(1, attributes.get("personDOB"));
                preparedStatement.setString(2, attributes.get("personDOD"));
                preparedStatement.setString(3, attributes.get("personGender"));
                preparedStatement.setString(4, attributes.get("personOccupation"));
                preparedStatement.setInt(5, person.getId());

                // Execute the query. It will store attribute information of specified mediaId in the database
                preparedStatement.executeUpdate();
                System.out.println("Data added successfully for person id " + person.getId());

                // prepared statement close
                preparedStatement.close();

                // close the connection
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error Message: " + e.getMessage());
                // retrun false if there is any error
                return false;
            }
        }

        // Retrns true if attribute information is added in the database
        return true;
    }

    /**
     * {@code recordReference} It stores the reference material for the individual in the database
     *
     * @param person takes PersonIdentity object as input
     * @param reference takes reference as an input. A person can have multiple references
     * @return true if the references was stored in the database else return false
     */
    public boolean recordReference(PersonIdentity person, String reference) {

        // Opens the connection with the database
        open();

        // Retruns false if the person object is null
        if (person == null) {
            System.out.println("Entered person doesn't exists. Cannot able to add reference");
            return false;
        }

        // Returns false if the references is null or empty
        if (reference == null || reference.equalsIgnoreCase("null") || reference.isEmpty() || reference.isBlank()){
            System.out.println("Reference is Empty or Null");
            return false;
        }

        // Throws Exception if there is any error
        try (Statement statement = conn.createStatement()) {

            // Execute the query. It will store references of specified personid in the database.
            statement.execute("insert into " + TABLE_PERSON_REFERENCE + " (" + COLUMN_REFERENCE + ", " + COLUMN_REFERENCE_FK_PERSONID + " ) " +
                    "VALUES('" + reference + "', '" + person.getId() + "')");
            System.out.println("Reference added successfully for id " + person.getId());

            // close the connection with database
            conn.close();
        } catch (Exception e) {
            System.out.println("Error Message: " + e.getMessage());
            // Returns false if there is any error
            return false;
        }

        // Returns true if  the reference was stored in the database
        return true;
    }


    /**
     * {@code recordReference} It stores the note for the individual in the database
     *
     * @param person takes PersonIdentity object as input
     * @param note takes note as an input. A person can have multiple references
     * @return true if the note was stored in the database else return false
     */
    public boolean recordNote(PersonIdentity person, String note) {
        // Opens the connection with the database
        open();

        // Retruns false if the person object is null
        if (person == null) {
            System.out.println("Entered person doesn't exists. Cannot able to add note");
            return false;
        }

        // Returns false if the references is null or empty
        if (note == null || note.equalsIgnoreCase("null") || note.isEmpty() || note.isBlank()){
            System.out.println("Note is Empty or Null");
            return false;
        }

        // Throws Exception if there is any error
        try (Statement statement = conn.createStatement()) {
            // Execute the query. It will store references of specified personid in the database.
            statement.execute("insert into " + TABLE_PERSON_NOTE + " (" + COLUMN_NOTE + ", " + COLUMN_NOTE_FK_PERSONID + " ) " +
                    "VALUES('" + note + "', '" + person.getId() + "')");
            System.out.println("Note added successfully for id " + person.getId());
            statement.close();

            // Close the connection with the database
            conn.close();
        } catch (Exception e) {
            System.out.println("Error Message: " + e.getMessage());
            // Returns false if there is any error
            return false;
        }
        // Returns true if the note was stored in the database
        return true;
    }

    /**
     * {@code isRecordChildPossible} This method is used to check whether parent-child
     * relation is allowed or not
     *
     * @param parent takes parent as input
     * @param child takes child as input
     * @return integer value based on conditions
     */
    private int isRecordChildPossible (int parent, int child){

        // opens the connection with the database
        open();

        // Throws the exception if there is any error
        try (Statement statement = conn.createStatement();
        Statement statement1 = conn.createStatement()) {

            // this result stores the set of rows from recordChild table
            ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_PARENT_CHILD);

            // This result stores the total number of child parents has
            ResultSet rs1 = statement1.executeQuery("SELECT "+COLUMN_CHILD+", COUNT(*) " +
                    "FROM "+TABLE_PARENT_CHILD+" where "+COLUMN_CHILD+" = "+child+" ;");

            // Iterate the result set
            while (rs.next()){

                // Return 0  if the relation is already exists
                if (rs.getInt(1)==parent && rs.getInt(2)==child){
                    return 0;
                } // Return 1 if user try to store child as parent if parent-child relation already exists
                if (rs.getInt(2)==parent && rs.getInt(1)==child){
                    return 1;
                }
            }

        while (rs1.next()){

            // Return 2 if child already have 2 parents
            if (rs1.getInt(2)>=2){
                return 2;
            }
        }
        } catch (SQLException e) {
            System.out.println("Error Message: "+e.getMessage());
        }

        // Retrun 5 if the relation is allowed to store in the database
        return 5;
    }

    /**
     * {@code recordChild} This method is used to  add parent-child relation in the database.
     *
     * @param parent takes PersonIdentity object as input
     * @param child takes PersonIdentity object as input
     * @return true if the relation was stored in the database else false
     */
    public boolean recordChild( PersonIdentity parent, PersonIdentity child ){

        // Open the connection with the database
        open();

        // Returns false if parent or child does not exists
        if (parent == null || child == null){
            System.out.println("Parent or Child does not exists");
            return false;
        }

        // Return false if parent and child are same
        if (parent.getId() == child.getId()){
            System.out.println("Parent cannot be child");
            return false;
        }

        // used to store value returned from isRecordChildPossible() method
        int value = isRecordChildPossible(parent.getId(), child.getId());

        //Return false if same relation already exists
        if (value == 0){
            System.out.println("Parent child relation already exists");
            return false;
        }

        //Retrun false if user try to store child as parent if parent-child relation already exists
        if (value == 1){
            System.out.println("Reverse not possible. As Parent Child Relation already Exists.");
            return false;
        }

        // Retrun false if child already have 2 parents
        if (value == 2){
            System.out.println("Child already have 2 Parents");
            return false;
        }

        // Allowed to record the relation
        if (value == 5) {
            // throws the exception if there is any error
            try (Statement statement = conn.createStatement()) {

                // Ececute the query. This query will insert the parent child relation in the database
                statement.execute("INSERT INTO " + TABLE_PARENT_CHILD + " (" + COLUMN_PARENT + ", " + COLUMN_CHILD + " ) " +
                        "VALUES('" + parent.getId() + "', '" + child.getId() + "')");

                System.out.println("Parent with Id " + parent.getId() + " and Child with Id " + child.getId() + " added Successfully");
                statement.close();

                //close the connection
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error Message: " + e.getMessage());
                // Return false if there is any error
                return false;
            }
        }

        // retrun true if the realtion was stroed in the database
        return true;
    }

    /**
     * {@code addMediaFile} It stores media file to the media archive database
      *
     * @param fileLocation takes file loaction as input. For Example: C:\\media\\images\\image1.png
     * @return FileIdentifier object
     */
    public FileIdentifier addMediaFile(String fileLocation) {
        // Opens the connections with the database
        open();

        // FileIdentifier isntanace used to store the object
        FileIdentifier file;

        // Retruns null if the file location is null or empty
        if (fileLocation == null || fileLocation.equalsIgnoreCase("null") ||
        fileLocation.isEmpty() || fileLocation.isBlank()){
            System.out.println("Media location is Empty or Null");
            return null;
        }

        // File Object takes file location as input
        File f = new File(fileLocation);

        // Stores the filename from the file location
        String fileName = f.getName();

        // Throws Exception if there is any error
        try (Statement statement = conn.createStatement()) {

            //Execute the query and stores set of rows based on query from the database
            ResultSet rs = statement.executeQuery("SELECT MAX(" + COLUMN_MEDIA_ID + ") FROM " + TABLE_MEDIA_NAME_INFO);

            // Moves the resultset pointer to the first row
            rs.next();

            int mediaId = rs.getInt(1) + 1;

            // It will insert the fileName and fileLocation into the table
            statement.execute("insert into " + TABLE_MEDIA_NAME_INFO + " ( " + COLUMN_MEDIA_NAME + ", " + COLUMN_MEDIA_LOCATION + " ) " +
                    "VALUES ( '" + fileName + "', '" + fileLocation + "')");
            System.out.println(fileName + " added successfully with id " + mediaId);

            // Create the object containing mediaId, fileName and fileLocation
            file = new FileIdentifier(mediaId, fileName, fileLocation);

            // Store that object into the map
            fileMap.put(mediaId, file);
            statement.close();

            // close the connection with the database
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error Message - " + e.getMessage());
            // Return null if there is ay eerror
            return null;
        }

        // retrun object
        return file;
    }


    /**
     * {@code checkMediaAttribute} it will checks whether the media id exists or not
     *
     * @param id takes of the media as input
     * @return true if the media id exists else retrun false
     */
    private boolean checkMediaAttribute(int id) {
        // Open the connection with the database
        open();

        // throws exception if there is any error
        try (Statement statement = conn.createStatement()) {

            // Execute the query and stores set of rows based on query from the database
            ResultSet rs = statement.executeQuery("SELECT " + COLUMN_ATTRIBUTE_FK_MEDIAID + " FROM " + TABLE_MEDIA_ATTRIBUTE_INFO);

            //Iterate the resultset
            while (rs.next()) {

                /**
                 * It will compare the id with every record in the resuktset
                 * if the it matches then it will return true
                 */
                if (rs.getInt(1) == id) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error Message: " + e.getMessage());
        }

        // Retruns false if no match found for the media id
        return false;
    }

    /**
     * {@code recordMediaAttributes} It stores the media infromation in the media archive database
     *
     * @param fileIdentifier takes FileIdentifier object as input
     * @param attributes takes map as input. The keys will be attribute name
     *                   or database columns such as "Year", "Date" and "City".
     *                   The values in the map is the data. It stores values as String
     * @return true if all the attributes were stored in the database
     */
    public boolean recordMediaAttributes(FileIdentifier fileIdentifier, Map<String, String> attributes) {

       // Opens the connection with the database
        open();


        // Returns false if the FileIdentifier object is null
        if (fileIdentifier == null) {
            System.out.println("Media doesn't exists. Cannot able to add media attributes");
            return false;
        }

        // If Year is null or empty than it will store null in the attribute value
        if (attributes.get("Year") == null || attributes.get("Year").equals("null") || attributes.get("Year").isBlank() || attributes.get("Year").isEmpty()){
            attributes.put("Year", null);
        }

        // If Date is null or empty than it will store null in the attribute value
        if (attributes.get("Date") == null || attributes.get("Date").equals("null") || attributes.get("Date").isBlank() || attributes.get("Date").isEmpty()){
            attributes.put("Date", null);
        }

        // If City is null or empty than it will store null in the attribute value
        if (attributes.get("City") == null || attributes.get("City").equals("null") || attributes.get("City").isBlank() || attributes.get("City").isEmpty()){
            attributes.put("City", null);
        }

        // Retruns false if media id does not exists
        if (checkMediaAttribute(fileIdentifier.getId())) {
            try{
                String Update_Query = "UPDATE "+TABLE_MEDIA_ATTRIBUTE_INFO+" SET "+COLUMN_ATTRIBUTE_YEAR+
                        " = ?,"+COLUMN_ATTRIBUTE_DATE+" = ?,"+
                        COLUMN_ATTRIBUTE_CITY+" = ? WHERE "+COLUMN_ATTRIBUTE_FK_MEDIAID+" = ?";

                PreparedStatement preparedStatement = conn.prepareStatement(Update_Query);
                preparedStatement.setString(1, attributes.get("Year"));
                preparedStatement.setString(2, attributes.get("Date"));
                preparedStatement.setString(3, attributes.get("City"));
                preparedStatement.setInt(4, fileIdentifier.getId());

                // Execute the query. It will store attribute information of specified mediaId in the database
                preparedStatement.executeUpdate();

                System.out.println("Data updated Successfully for media Id " + fileIdentifier.getId());
                preparedStatement.close();

                // Close the connection with database
                conn.close();

            } catch (SQLException e) {
                System.out.println("Error Message: "+e.getMessage());
            }
        }
        else {

            // throws exception if there is any error
            try {

                String Insert_Query = "INSERT INTO " + TABLE_MEDIA_ATTRIBUTE_INFO + " ("
                        + COLUMN_ATTRIBUTE_YEAR + ", " + COLUMN_ATTRIBUTE_DATE + ", " +
                        "" + COLUMN_ATTRIBUTE_CITY + ", " + COLUMN_ATTRIBUTE_FK_MEDIAID + " )"
                        + "VALUES(?,?,?,?)";

                PreparedStatement preparedStatement = conn.prepareStatement(Insert_Query);
                preparedStatement.setString(1, attributes.get("Year"));
                preparedStatement.setString(2, attributes.get("Date"));
                preparedStatement.setString(3, attributes.get("City"));
                preparedStatement.setInt(4, fileIdentifier.getId());

                // Execute the query. It will store attribute information of specified mediaId in the database
                preparedStatement.executeUpdate();

                System.out.println("Data Added Successfully for media Id " + fileIdentifier.getId());
                preparedStatement.close();

                // Close the connection with database
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error Message: " + e.getMessage());
                // retruns fales if there is any error
                return false;
            }
        }

        // Retrns true if attribute information is added in the database
        return true;
    }

    /**
     * {@code peopleInMedia} It will connect people with the given media file
     *
     * @param fileIdentifier takes FileIdentifier object as input
     * @param people takes list of people as input
     * @return true if people are connected to the media file in the system else retrun false
     */
    public boolean peopleInMedia(FileIdentifier fileIdentifier, List<PersonIdentity> people) {

        // Open the connection with database
        open();

        // Retrun false if the fileIdentifier null
        if (fileIdentifier == null) {
            System.out.println("Media doesn't exists. Cannot able to find people");
            return false;
        }

        // Retrun false if the people list is empty
        if(people.isEmpty()){
            System.out.println("List is Empty. No people found");
            return false;
        }

        // Retrun false if any of the person in the list does not exists
        for (int i = 0; i<people.size(); i++){
            // Iterate through each person in the list and checks if it is exists or not
            if(people.get(i) == null ){
                System.out.println("One of the Person from the People list does not exist");
                return false;
            }
        }

        // Throws exception if there is any error
        try (Statement statement = conn.createStatement()) {

            // It will connect each person one by one from the people list with the give media file
            for (int i = 0; i < people.size(); i++) {
                statement.execute("INSERT INTO " + TABLE_MEDIA_PERSON_RELATION + " (" + COLUMN_FK_MEDIA_ID + ", " + COLUMN_FK_PERSON_ID + ")"
                        + "VALUES ('" + fileIdentifier.getId() + "', '" + people.get(i).getId() + "')");
            }

            System.out.println("People added successfully for Media id " + fileIdentifier.getId());
            statement.close();

            //Close the connection with database
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error Message " + e.getMessage());
            // Retun false if there is error
            return false;
        }

        // Returns true if people in the list are now connected with media file
        return true;
    }

    /**
     * {@code tagMedia} It will stores the tags for the media file in the media archive database
     *
     * @param fileIdentifier takes fileIdentifier object as input
     * @param tag takes tags as input. A media file can have multiple tags
     * @return true if tags were stroed in the dataabse else false
     */
    public boolean tagMedia(FileIdentifier fileIdentifier, String tag) {
        //Opens the connection with the database
        open();

        //Retuns falss if fileIdentifier object does not exists
        if (fileIdentifier == null) {
            System.out.println("Media doesn't exists. Cannot able to ad tags");
            return false;
        }

        // Returns false if tag is null or empty
        if (tag == null || tag.equalsIgnoreCase("null") ||
                tag.isEmpty() || tag.isBlank()){
            System.out.println("Tag is Empty or Null");
            return false;
        }

        // Throws exceptions is there is any error
        try (Statement statement = conn.createStatement()) {

            // Execute the query. It will store tags of specified media file in the database
            statement.execute("INSERT INTO " + TABLE_MEDIA_TAG + " (" + COLUMN_MEDIA_TAG + ", " + COLUMN_TAG_FK_MEDIA_ID + ") " +
                    "VALUES ('" + tag + "', '" + fileIdentifier.getId() + "')");
            System.out.println("Tag Added successfully for id " + fileIdentifier.getId());
            statement.close();

            //close the connection with the database
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error Message: " + e.getMessage());
            //returns false if there is any error
            return false;
        }
        // Returns true if the tag was stored in the database
        return true;
    }

    /**
     * {@code checkPartnering} It will check whether partnering is possible between two partners
     *
     * @param partner1 Takes partner id as input
     * @param partner2 Takes partner id as input
     * @return Integer value based on the various conditions
     */
    private int checkPartnering(int partner1, int partner2) {
        //Opens the connection with the database
        open();

        // Throws exception is there is any error
        try (Statement statement = conn.createStatement()) {

            //Execute the query and stores set of rows based on query from the database
            ResultSet rs = statement.executeQuery("SELECT " + COLUMN_PARTNER_1 + ", " + COLUMN_PARTNER_2 + ", " + COLUMN_RELATIONSHIP + " FROM " + TABLE_RECORD_RELATION);

            // Iterate the resultset
            while (rs.next()) {

                // Returns 0 if partner 1 is in 1st column and partner 2 is in 2nd column and status is partnering
                if (rs.getInt(1) == partner1 && rs.getInt(2) == partner2 && rs.getString(3).equals(partnering)) {
                    return 0;
                }

                // Returns 1 if partner 2 is in 1st column and partner 1 is in 2nd column and status is partnering
                if (rs.getInt(1) == partner2 && rs.getInt(2) == partner1 && rs.getString(3).equals(partnering)) {
                    return 1;
                }

                // Returns 2 if partner 1 is in 1st column and status is partnering
                if ((rs.getInt(1) == partner1 && rs.getString(3).equals(partnering))) {
                    return 3;
                }

                // Returns 3 if partner 2 is in 1st column and status is partnering
                if (rs.getInt(1) == partner2 && rs.getString(3).equals(partnering)) {
                    return 4;
                }

                // Returns 4 if partner 1 is in 2nd column and status is partnering
                if (rs.getInt(2) == partner1 && rs.getString(3).equals(partnering)) {
                    return 5;
                }

                // Returns 5 if partner 2 is in 2nd column and status is partnering
                if (rs.getInt(2) == partner2 && rs.getString(3).equals(partnering)) {
                    return 6;
                }

                // Return 2 if partner 1 is in 1st column and partner 2 is in 2nd column and status is dissolution
                if (rs.getInt(1) == partner1 && rs.getInt(2) == partner2 && rs.getString(3).equals(dissolution)) {

                    // Returns 3 if partner 1 is in 1st column and status is partnering
                    if ((rs.getInt(1) == partner1 && rs.getString(3).equals(partnering))) {
                        return 3;
                    }

                    // Returns 4 if partner 2 is in 1st column and status is partnering
                    if (rs.getInt(1) == partner2 && rs.getString(3).equals(partnering)) {
                        return 4;
                    }
                    // throws exception if there is any error
                    try (Statement statement2 = conn.createStatement()) {
                        // Execute the query to update the status of two individual to dissolutions
                        statement2.execute("UPDATE " + TABLE_RECORD_RELATION + " SET " + COLUMN_RELATIONSHIP + " = '" + dissolution + "' WHERE " + COLUMN_PARTNER_1 + " = '" + partner1+ "' AND "
                                + COLUMN_PARTNER_2 + " = '" + partner2 + "' AND " + COLUMN_RELATIONSHIP + " = '" + partnering + "';");
                        System.out.println("Recorded");
                    } catch (SQLException e) {
                        System.out.println("Error Message: " + e.getMessage());
                    }
                }

                // Return 8 if partner 2 is in 1st column and partner 1 is in 2nd column and status is dissolution
                else if (rs.getInt(1) == partner2 && rs.getInt(2) == partner1 && rs.getString(3).equals(dissolution)) {

                    // Returns 5 if partner 1 is in 2nd column and status is partnering
                    if (rs.getInt(2) == partner1 && rs.getString(3).equals(partnering)) {
                        return 5;
                    }

                    // Returns 6 if partner 2 is in 2nd column and status is partnering
                    if (rs.getInt(2) == partner2 && rs.getString(3).equals(partnering)) {
                        return 6;
                    }

                    // throws exception if there is any error
                    try (Statement statement3 = conn.createStatement()) {

                        // Execute the query to update the status of two individual to dissolutions
                        statement3.execute("UPDATE " + TABLE_RECORD_RELATION + " SET " + COLUMN_RELATIONSHIP + " = '" + dissolution + "' WHERE " + COLUMN_PARTNER_1 + " = '" + partner2 + "' AND "
                                + COLUMN_PARTNER_2 + " = '" + partner1 + "' AND " + COLUMN_RELATIONSHIP + " = '" + partnering + "';");
                        System.out.println("Recorded");
                    } catch (SQLException e) {
                        System.out.println("Error Message: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error Message: " + e.getMessage());
        }
        //Return 11 if partner 1 and partner 2 does not statisfy with the above conditions
        return 11;
    }

    /**
     * {@code recordPartnering} It will record the symmetric partnering relation between two individuals
     *
     * @param partner1 takes PersonIdentity object as input
     * @param partner2 takes PersonIdentity object as input
     * @return true if the partnering between two individual is stored in the database else return false
     */
    public boolean recordPartnering(PersonIdentity partner1, PersonIdentity partner2) {
        // Open the connection with the database
        open();

        // Return false if Partner 1 and Partner 2 does not exists
        if (partner1 == null || partner2 == null) {
            System.out.println("Either Partner 1 or Partner 2 does not exists");
            return false;
        }

        // Return false if bot the partner are same
        if (partner1.getId() == partner2.getId()) {
            System.out.println("Partnering not possible with same partner");
            return false;
        }

        // Stores the Integer value returned by checkPartnering method
        int value = checkPartnering(partner1.getId(), partner2.getId());

        //Return false if partnering between two individual already exists in the database
        if (value == 0 || value == 1) {
            System.out.println("Id " + partner1.getId() + " And Id " + partner2.getId() + " are already partner");
            return false;
        }

        //Return false if Partner 1 is already partnered with someone
        if (value == 3 || value == 5) {
            System.out.println("Partner Id " + partner1.getId() + " is already partnered with someone");
            return false;
        }

        //Return false if Partner 2 is already partnered with someone
        if (value == 4 || value == 6 ) {
            System.out.println("Partner Id " + partner2.getId() + " is already partnered with someone");
            return false;
        }

        // If value == 11 or 2 or 8 then store the partnering between two individual in the database
        if (value == 11 ) {

            //Throws the exceptions if there is any error
            try (Statement statement = conn.createStatement()) {

                // Excecute the query to record the symmetric partnering between two individual
                statement.execute("INSERT INTO " + TABLE_RECORD_RELATION + " ( " + COLUMN_PARTNER_1 + "," + COLUMN_PARTNER_2 + "," + COLUMN_RELATIONSHIP + ")" +
                        "VALUES ('" + partner1.getId() + "', '" + partner2.getId() + "', '" + partnering + "')");
                System.out.println("Partnering between Id " + partner1.getId() + " and Id " + partner2.getId() + " is recorded");
            } catch (SQLException e) {
                System.out.println("Error Message: " + e.getMessage());
                // retrun false if tehre is any error
                return false;
            }
        }

        // retrun true is the relation between two individual is stored in the database
        return true;
    }


    /**
     * {@code recordPartnering} It will record the symmetric dissolution between two individuals
     *
     * @param partner1 takes PersonIdentity object as input
     * @param partner2 takes PersonIdentity object as input
     * @return true if the dissolution bewteen two individual is stored in the database else return false
     */
    public boolean recordDissolution(PersonIdentity partner1, PersonIdentity partner2) {
        // Open the connection with the database
        open();

        // Return false if Partner 1 and Partner 2 does not exists
        if (partner1 == null || partner2 == null) {
            System.out.println("Either 1st partner or 2nd Partner does not exists");
            return false;
        }

        // Stores the Integer value retruned  by checkPartnering method
        int value = checkPartnering(partner1.getId(), partner2.getId());

        // Return false if dissolution between two individuals already exsits in the database
        if (value == 2 || value == 8) {
            System.out.println("Id " + partner1.getId() + " And Id " + partner2.getId() + " were already dissolved");
            return false;
        }

        //If value = 0 then update the relationship status to dissolution
        if (value == 0) {
            // throws exception if there is any error
            try (Statement statement = conn.createStatement()) {
                // Execute the query to update the status of two individual to dissolutions
                statement.execute("UPDATE " + TABLE_RECORD_RELATION + " SET " + COLUMN_RELATIONSHIP + " = '" + dissolution + "' WHERE " + COLUMN_PARTNER_1 + " = '" + partner1.getId() + "' AND "
                        + COLUMN_PARTNER_2 + " = '" + partner2.getId() + "' AND " + COLUMN_RELATIONSHIP + " = '" + partnering + "';");
                System.out.println("Recorded");
            } catch (SQLException e) {
                System.out.println("Error Message: " + e.getMessage());
                // return false if there is any error
                return false;
            }
        } else if (value == 1) {

            // throws exception if there is any error
            try (Statement statement = conn.createStatement()) {

                // Execute the query to update the status of two individual to dissolutions
                statement.execute("UPDATE " + TABLE_RECORD_RELATION + " SET " + COLUMN_RELATIONSHIP + " = '" + dissolution + "' WHERE " + COLUMN_PARTNER_1 + " = '" + partner2.getId() + "' AND "
                        + COLUMN_PARTNER_2 + " = '" + partner1.getId() + "' AND " + COLUMN_RELATIONSHIP + " = '" + partnering + "';");
                System.out.println("Recorded");
            } catch (SQLException e) {
                System.out.println("Error Message: " + e.getMessage());
                // return false if there is any error
                return false;
            }
        }
        // Retrun false if both the individuals are not partners
        else {
            System.out.println("Partner Id " + partner1.getId() + " and Partner Id " + partner2.getId() + " are not partner. So, dissolution not possible");
            return false;
        }

        // Returns true if dissolution between two individual is stored in the database
        return true;
    }

    /**
     * {@code findPerson} find or locate the individual based on its name
     *
     * @param name take name of the person as input
     * @return PersonIdentity object
     */
    public PersonIdentity findPerson(String name) {

        // Open the conection with the database
        open();

        // Integer element to store index
        int index = 0;

        // Integer element to store the occurance of name in the database
        int count = 0;

        // List to store the personName which are already present in the database
        List<String> personNames = new ArrayList<>();

        // Returns null if person name is null or empty
        if (name == null || name.equalsIgnoreCase("null") ||
                name.isEmpty() || name.isBlank()){
            System.out.println("Name is Empty or Null");
            return null;
        }

        // It will loop over the personMap and add the person name in the list
        for (int i=1; i<personMap.size(); i++){
            personNames.add(personMap.get(i).getName());
        }

        // If name is present in the list then finds its occurances and the index at which it appears
        if(personNames.contains(name)){
            for (int i = 1; i <= personMap.size(); i++) {
                if (personMap.get(i).getName().equalsIgnoreCase(name)) {
                    count = count + 1;
                    index = i;
                }
            }
        }
        else {
            System.out.println("Person with name "+name+" does not exists");
            // retrun null if the name of the person is not present list
            return null;
        }

        // Retrun null if there are more than one person with same name
        if (count > 1) {
            System.out.println("Contains " + count + " records with name " + name);
            return null;
        }

        // Return PersonIdentity object based on the index from personMap
        return personMap.get(index);
    }

    /**
     * {@code FileIdentifier} find or locate the media file based on its name
     *
     * @param name Take file name as inout
     * @return FileIdentifier object
     */
    public FileIdentifier findMediaFile(String name) {
        // Open the conection with the database
        open();

        // Integer element to store index
        int index = 0;

        // Integer element to store the occurance of filename in the database
        int count = 0;

        // List to store the filename which are already present in the database
        List<FileIdentifier> fileNames = new ArrayList<>();

        // Returns null if filename is null or empty
        if (name == null || name.equalsIgnoreCase("null") ||
                name.isEmpty() || name.isBlank()) {
            System.out.println("Name is Empty or Null");
            return null;
        }

        // It will loop over the filenameMap and add the person name in the list
        for (int i = 1; i <= fileMap.size(); i++) {
            fileNames.add(fileMap.get(i));
        }

        // If filename is present in the list then finds its occurances and the index at which it appears
        if (fileNames.contains(name)) {
            for (int i = 1; i <= fileMap.size(); i++) {
                if (fileMap.get(i).getFileName().equalsIgnoreCase(name)) {
                    count = count + 1;
                    index = i;
                }
            }
        } else {
            System.out.println("Media with name " + name + " does not exists");
            // retrun null is the name os the person is not present list
            return null;
        }
        // Retrun null if there are more than one filename with same name
        if (count > 1) {
            System.out.println("Contains " + count + " records with media name " + name);
            return null;
        }

        // Return FileIdentifier object based on the index from fileMap
        return fileMap.get(index);
    }

    /**
     * {@code findName} This method is used to get name of the individual using PersonIdentity object
     *
     * @param id taks PersonIdentity object as input
     * @return name of the individual
     */
    public String findName(PersonIdentity id) {
        // Return null if PersonIdentity object does not exists
        if (id == null){
            System.out.println("Person does not exists");
            return null;
        }
        // Retrun the name of individual
        return id.getName();
    }

    /**
     * {@code findMediaFile} This method is used to get file name using FileIdentifier object
     *
     * @param fileId takes FileIdentifier as input
     * @return name of the file
     */
    public String findMediaFile(FileIdentifier fileId) {
        // Return null if FileIdentifier object does not exists
        if (fileId == null){
            System.out.println("Media does not exists");
            return null;
        }
        // Retrun the file name
        return fileId.getFileName();
    }

    /**
     * {@code notesAndReferences} This method is used to get nodes and references of the individual
     * in the same order in which they are added to the family tree
     *
     * @param person atkes input as PersonIdentity object
     * @return Combine list of nodes and references of given PersonIdentity object
     * in which they are added to the family tree
     */
    public List<String> notesAndReferences(PersonIdentity person) {
        // Opens the connection with the database
        open();

        // List in which nodes and references are stored based on their timestamps for particluar person
        List<String> nodesAndRefernces = new LinkedList<>();

        //Return null If PersonIdentity object doesnot exists
        if (person == null) {
            System.out.println("Person does not exists");
            return null;
        }

        // Throws exceptions if there is any error
        try (Statement statement = conn.createStatement()) {

            // Execute the query and stores set of rows based on query from the database
            // The query returns the rows that contains nodes and references in the order in which they are added to the family tree
            ResultSet rs = statement.executeQuery("SELECT " + COLUMN_NOTE + ", " + COLUMN_NOTE_FK_PERSONID + " , " + COLUMN_NOTE_LASTUPDATED + " FROM " + TABLE_PERSON_NOTE + " WHERE " +
                    COLUMN_NOTE_FK_PERSONID + " = " + person.getId() + " UNION SELECT " + COLUMN_REFERENCE + ", " + COLUMN_REFERENCE_FK_PERSONID + ", " + COLUMN_REFERENCE_LASTUPDATED + " FROM " +
                    TABLE_PERSON_REFERENCE + " WHERE " + COLUMN_REFERENCE_FK_PERSONID + " = " + person.getId() + " ORDER BY " + COLUMN_REFERENCE_LASTUPDATED);

            // Iterate the result set
            while (rs.next()) {
                //add nodes and references to the list one by one
                nodesAndRefernces.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Error Message: " + e.getMessage());
            // retrun null if there is any error
            return null;
        }

        // Return list that contains nodes and references  based on timestamps
        return nodesAndRefernces;
    }

    /**
     * {@code isTagExits} This method will checks whether tag entered by user exists or not
     *
     * @param tag takes tag as input
     * @return true if tag exists else retrun false
     */
    private boolean isTagExits(String tag){
        //Throws exceptions if there is any error
        try(Statement statement = conn.createStatement()) {
            // Execute the query and stores set of rows based on query from the database
            // the query retruns the set of rows that contains tags in the database
            ResultSet rs = statement.executeQuery("SELECT "+COLUMN_MEDIA_TAG+" FROM "+TABLE_MEDIA_TAG);

            // iterate the result set
            while (rs.next()){
                // iT will check compare the tag
                if (rs.getString(1).equalsIgnoreCase(tag)){
                    // Return true if it matches or already exists
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error Message: "+e.getMessage());
        }
        // Return false is tag does not esists
        return false;
    }

    /**
     * {@code findMediaByTag} This methods is used to find the set of media files that are linked to
     * the given tag and fall within the give date range
     *
     * @param tag takes tag as input
     * @param startDate takes startdate as inout
     * @param endDate takes enddate as
     * @return set of FileIdentifier objects that are linked to the give tags and falls within the give
     * date range
     */
    public Set<FileIdentifier> findMediaByTag(String tag, String startDate, String endDate) {

        // Open connection with the dataabse
        open();

        // Returns null if tag is null or empty
        if (tag == null || tag.equalsIgnoreCase("null") || tag.isEmpty() || tag.isBlank()){
            System.out.println("Tag is Empty or Null");
            return null;
        }

        // Returns null if the tag is not linked to any media
        if (!isTagExits(tag)){
            System.out.println("No media related to tag "+tag);
            return null;
        }

        // Consider start date = null if start date is empty or null
        if (startDate == null || startDate.equals("null") || startDate.isEmpty() || startDate.isBlank()){
            startDate = null;
        }

        // Consider end date = null if end date is empty or null
        if (endDate == null || endDate.equals("null") || endDate.isEmpty() || endDate.isBlank()){
            endDate = null;
        }

        // Set to store the mediafile name
        Set<FileIdentifier> mediaFileName = new HashSet<>();

        //Throws exception if there is any error
        try (Statement statement = conn.createStatement(); Statement statement1 = conn.createStatement()) {

            // Query to find the media related to the tag
            String FIND_MEDIA  = "SELECT q1.mediaTag, q1.mediaId, q1.mediaName FROM mediaAttributeInfo INNER JOIN\n" +
                    "(SELECT mediaTag.mediaTag, mediaNameInfo.mediaId, mediaNameInfo.mediaName FROM mediaNameInfo \n" +
                    "INNER JOIN mediaTag WHERE mediaNameInfo.mediaId = mediaTag.mediaId) AS q1 ON q1.mediaId =\n" +
                    "mediaAttributeInfo.mediaId AND q1.mediaTag = '" + tag + "'";

            String FIND_MEDIA_NULL  = "SELECT q1.mediaTag, q1.mediaId, q1.mediaName FROM mediaAttributeInfo INNER JOIN\n" +
                    "(SELECT mediaTag.mediaTag, mediaNameInfo.mediaId, mediaNameInfo.mediaName FROM mediaNameInfo \n" +
                    "INNER JOIN mediaTag WHERE mediaNameInfo.mediaId = mediaTag.mediaId) AS q1 ON q1.mediaId =\n" +
                    "mediaAttributeInfo.mediaId AND q1.mediaTag = '" + tag + "'";



            ResultSet rs = null;
            StringBuilder sb = new StringBuilder();
            sb.append(FIND_MEDIA);

            // Execute the query and store the set of rows returned from the dataabse in the result sets
            // in the case where startdate and enddate are not null.
            if (startDate != null && endDate != null){
                rs = statement.executeQuery(sb.append(" AND mediaAttributeInfo.mediaDate BETWEEN \"" + startDate + "\" AND \"" + endDate + "\"").toString());
            }

            // Execute the query and store the set of rows returned from the dataabse in the result sets
            // in the case where startdate is not null and enddate is null.
            if (startDate != null && endDate == null){
                rs = statement.executeQuery(sb.append(" AND mediaAttributeInfo.mediaDate >= \"" + startDate + "\"").toString());
            }

            // Execute the query and store the set of rows returned from the dataabse in the result sets
            // in the case where startdate is null and enddate is not null
            if (startDate == null && endDate != null){
                rs = statement.executeQuery(sb.append(" AND mediaAttributeInfo.mediaDate <= \"" + endDate + "\"").toString());
            }

            // Execute the query and store the set of rows returned from the dataabse in the result sets
            // in the case where startdate and enddate both are null
            if(startDate == null && endDate == null){
                rs = statement.executeQuery(sb.toString());
            }

            ResultSet rs1 = null;
            StringBuilder sb1 = new StringBuilder();
            sb1.append(FIND_MEDIA_NULL);
            rs1 = statement1.executeQuery(sb1.append(" AND mediaAttributeInfo.mediaDate is NULL").toString());


            // Itereate the resultset
            while (rs.next()) {
                for (int i = 1; i <= fileMap.size(); i++) {
                    // If related media object found then it will add it to the set
                    if (fileMap.get(i).getFileName().equals(rs.getString(3))) {
                        mediaFileName.add(fileMap.get(i));
                    }
                }
            }

            while (rs1.next()) {
                for (int i = 1; i <= fileMap.size(); i++) {
                    // If related media object found then it will add it to the set
                    if (fileMap.get(i).getFileName().equals(rs1.getString(3))) {
                        mediaFileName.add(fileMap.get(i));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error Message: " + e.getMessage());
            // Return null is there is any exception
            return null;
        }

        // Return the set that contains the list of media that is linked to given tag
        return mediaFileName;
    }

    /**
     * {@code isLocationExists} This method will checks whether location entered by user exists or not
     * @param location take location as input
     * @return true if location exists else retrun false
     */
    private boolean isLocationExists(String location){
        //Throws exceptions if there is any error
        try(Statement statement = conn.createStatement()) {
            // Execute the query and stores set of rows based on query from the database
            // the query retruns the set of rows that contains location in the database
            ResultSet rs = statement.executeQuery("SELECT "+COLUMN_ATTRIBUTE_CITY+" FROM "+TABLE_MEDIA_ATTRIBUTE_INFO);

            // iterate the result set
            while (rs.next()){
                // iT will check compare the tag
                if (rs.getString(1).equalsIgnoreCase(location)){
                    // Return true if it matches or already exists
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error Message: "+e.getMessage());
        }
        // Return false is tag does not esists
        return false;
    }

    /**
     * {@code findMediaByLocation} This methods is used to find the set of medis files that are linked to
     * the given location and fall within the give date range
     * @param location takes tag as input
     * @param startDate takes startdate as input
     * @param endDate takes enddate as inout
     * @return set of FileIdentifier objects that are linked to the give location and falls within the give\
     * date range
     */
    public Set<FileIdentifier> findMediaByLocation(String location, String startDate, String endDate) {
        // Open connection with the dataabse
        open();

        // Returns null if location is null or empty
        if (location == null || location.equalsIgnoreCase("null") || location.isEmpty() || location.isBlank()){
            System.out.println("Location is Empty or Null");
            return null;
        }

        // Returns null if the location is not linked to any media
        if (!isLocationExists(location)){
            System.out.println("No media related to location "+location);
            return null;
        }

        // Consider start date = null if start date is empty or null
        if (startDate == null || startDate.equals("null") || startDate.isEmpty() || startDate.isBlank()){
            startDate = null;
        }

        // consider end date = null if end date is empty or null
        if (endDate == null || endDate.equals("null") || endDate.isEmpty() || endDate.isBlank()){
            endDate = null;
        }

        // Set to store the mediafile name
        Set<FileIdentifier> mediaFileName = new HashSet<>();

        //Throws exception if there is any error
        try (Statement statement = conn.createStatement(); Statement statement1 = conn.createStatement()) {

            // Query to find the media related to the location
            String FIND_MEDIA = "SELECT mediaAttributeInfo.mediaCity, mediaNameInfo.mediaId, mediaNameInfo.mediaName FROM\n" +
                    "mediaNameInfo INNER JOIN mediaAttributeInfo ON mediaNameInfo.mediaId = \n" +
                    "mediaAttributeInfo.mediaId AND mediaAttributeInfo.mediaCity = '" + location + "'";

            String FIND_MEDIA_NULL = "SELECT mediaAttributeInfo.mediaCity, mediaNameInfo.mediaId, mediaNameInfo.mediaName FROM\n" +
                    "mediaNameInfo INNER JOIN mediaAttributeInfo ON mediaNameInfo.mediaId = \n" +
                    "mediaAttributeInfo.mediaId AND mediaAttributeInfo.mediaCity = '" + location + "'";

            ResultSet rs =null;
            StringBuilder sb = new StringBuilder();
            sb.append(FIND_MEDIA);

            // Execute the query and store the set of rows returned from the dataabse in the result sets
            // in the case where startdate and enddate are not null.
            if (startDate != null && endDate != null){
                rs = statement.executeQuery(sb.append(" AND mediaAttributeInfo.mediaDate BETWEEN \"" + startDate + "\" AND \"" + endDate + "\"").toString());
            }

            // Execute the query and store the set of rows returned from the dataabse in the result sets
            // in the case where startdate is not null and enddate is null.
            if (startDate != null && endDate == null){
                rs = statement.executeQuery(sb.append(" AND mediaAttributeInfo.mediaDate >= \"" + startDate + "\"").toString());
            }

            // Execute the query and store the set of rows returned from the dataabse in the result sets
            // in the case where startdate is null and enddate is not null
            if (startDate == null && endDate != null){
                rs = statement.executeQuery(sb.append(" AND mediaAttributeInfo.mediaDate <= \"" + endDate + "\"").toString());
            }

            // Execute the query and store the set of rows returned from the dataabse in the result sets
            // in the case where startdate and enddate both are null
            if(startDate == null && endDate == null){
                rs = statement.executeQuery(sb.toString());
            }


            ResultSet rs1 = null;
            StringBuilder sb1 = new StringBuilder();
            sb1.append(FIND_MEDIA_NULL);
            rs1 = statement1.executeQuery(sb1.append(" AND mediaAttributeInfo.mediaDate is NULL").toString());

            // Itereate the resultset
            while (rs.next()) {
                for (int i = 1; i <= fileMap.size(); i++) {
                    // If related media found then it will add it to the set
                    if (fileMap.get(i).getFileName().equalsIgnoreCase(rs.getString(3))) {
                        mediaFileName.add(fileMap.get(i));
                    }
                }
            }

            // Itereate the resultset
            while (rs1.next()) {
                for (int i = 1; i <= fileMap.size(); i++) {
                    // If related media found then it will add it to the set
                    if (fileMap.get(i).getFileName().equalsIgnoreCase(rs1.getString(3))) {
                        mediaFileName.add(fileMap.get(i));
                    }
                }
            }


        } catch (SQLException e) {
            System.out.println("Error Message: " + e.getMessage());
            // Return null is there is any exception
            return null;
        }
        // Return the set that contains the list of media that is linked to given Location
        return mediaFileName;
    }


    /**
     *{@code findIndividualsMedia} This method is used to find the set of media files that includes any
     * of individual from the given list and falls within given range
     *
         * @param people Takes the set of individuals as input
     * @param startDate takes the startdate as input
     * @param endDate takes the end date as inout
     * @return list of media files which inclues individuals given in the list and falls within the
     * given date range
     */
    public List<FileIdentifier> findIndividualsMedia(Set<PersonIdentity> people, String startDate, String endDate) {

        // Open the connection with the database
        open();

        // Returns null if set people is empty
        if (people.isEmpty()){
            return null;
        }

        // Consider start date = null if start date is empty or null
        if (startDate == null || startDate.equals("null") || startDate.isEmpty() || startDate.isBlank()){
            startDate = null;
        }

        // Consider end date = null if end date is empty or null
        if (endDate == null || endDate.equals("null") || endDate.isEmpty() || endDate.isBlank()){
            endDate = null;
        }

        // Return null if one of the person does not exists
        for (PersonIdentity pi: people){
            if (pi == null){
                System.out.println("One of the person in the set does not exist");
                return null;
            }
        }


        // List to store the media files
        List<FileIdentifier> mediaFileName = new LinkedList<>();

        // Throws exceptions if there is any error
        try (Statement statement = conn.createStatement(); Statement statement1 = conn.createStatement()) {

            /**
             * Executes the query and store the set of rows returned from the database in the resultset
             * in the case where startDate and endDate is Not null.
             * The query return mediafile that includes given individuals
             */
            if (startDate != null && endDate != null) {
                for (PersonIdentity persons : people) {
                    ResultSet rs = statement.executeQuery("SELECT q1.personId, q1.mediaId, q1.mediaName FROM mediaAttributeInfo INNER JOIN\n" +
                            "(SELECT  mediaPersonRelation.personId, mediaPersonRelation.mediaId, mediaNameInfo.mediaName\n" +
                            "FROM mediaPersonRelation INNER JOIN mediaNameInfo ON mediaPersonRelation.mediaId = \n" +
                            "mediaNameInfo.mediaId) AS q1 ON q1.mediaId =  mediaAttributeInfo.mediaId WHERE \n" +
                            "mediaAttributeInfo.mediaDate BETWEEN \"" + startDate + "\" AND \"" + endDate + "\" AND q1.personId = '" + persons.getId() + "' ORDER BY mediaAttributeInfo.mediaDate, q1.mediaName");

                    // Iterate the results set
                    while (rs.next()) {
                        // stores the data of resultset in set one by one in order to remove duplicate values
                        if (!mediaFileName.contains(fileMap.get(rs.getInt(2)))){
                            mediaFileName.add(fileMap.get(rs.getInt(2)));
                        }
                    }
                }
            }

            /**
             * Executes the query and store the set of rows returned from the database in the resultset
             * in the case where startDate is not null and endDate is null.
             * The query return mediafile that includes given individuals
             */
            if (startDate != null && endDate == null) {
                for (PersonIdentity persons : people) {
                    ResultSet rs = statement.executeQuery("SELECT q1.personId, q1.mediaId, q1.mediaName FROM mediaAttributeInfo INNER JOIN\n" +
                            "(SELECT  mediaPersonRelation.personId, mediaPersonRelation.mediaId, mediaNameInfo.mediaName\n" +
                            "FROM mediaPersonRelation INNER JOIN mediaNameInfo ON mediaPersonRelation.mediaId = \n" +
                            "mediaNameInfo.mediaId) AS q1 ON q1.mediaId =  mediaAttributeInfo.mediaId WHERE \n" +
                            "mediaAttributeInfo.mediaDate >= \"" + startDate + "\" AND q1.personId = '" + persons.getId() + "' ORDER BY mediaAttributeInfo.mediaDate, q1.mediaName");

                    // Iterate the results set
                    while (rs.next()) {
                        // stores the data of resultset in set one by one in order to remove duplicate values
                        if (!mediaFileName.contains(fileMap.get(rs.getInt(2)))){
                            mediaFileName.add(fileMap.get(rs.getInt(2)));
                        }
                    }
                }


            }

            /**
             * Executes the query and store the set of rows returned from the database in the resultset
             * in the case where startDate is null and endDate is not null.
             * The query return mediafile that includes given individuals
             */
            if (startDate == null && endDate != null) {
                for (PersonIdentity persons : people) {
                    ResultSet rs = statement.executeQuery("SELECT q1.personId, q1.mediaId, q1.mediaName FROM mediaAttributeInfo INNER JOIN\n" +
                            "(SELECT  mediaPersonRelation.personId, mediaPersonRelation.mediaId, mediaNameInfo.mediaName\n" +
                            "FROM mediaPersonRelation INNER JOIN mediaNameInfo ON mediaPersonRelation.mediaId = \n" +
                            "mediaNameInfo.mediaId) AS q1 ON q1.mediaId =  mediaAttributeInfo.mediaId WHERE \n" +
                            "mediaAttributeInfo.mediaDate <= \"" + endDate + "\" AND q1.personId = '" + persons.getId() + "' ORDER BY mediaAttributeInfo.mediaDate, q1.mediaName");

                    // Iterate the results set
                    while (rs.next()) {
                        // stores the data of resultset in set one by one in order to remove duplicate values
                        if (!mediaFileName.contains(fileMap.get(rs.getInt(2)))){
                            mediaFileName.add(fileMap.get(rs.getInt(2)));
                        }
                    }
                }
            }

            /**
             * Executes the query and store the set of rows returned from the database in the resultset
             * in the case where startDate and enddate both are null.
             * The query return mediafile that includes given individuals
             */
            if (startDate == null && endDate == null) {
                for (PersonIdentity persons : people) {
                    ResultSet rs = statement.executeQuery("SELECT q1.personId, q1.mediaId, q1.mediaName FROM mediaAttributeInfo INNER JOIN\n" +
                            "(SELECT  mediaPersonRelation.personId, mediaPersonRelation.mediaId, mediaNameInfo.mediaName\n" +
                            "FROM mediaPersonRelation INNER JOIN mediaNameInfo ON mediaPersonRelation.mediaId = \n" +
                            "mediaNameInfo.mediaId) AS q1 ON q1.mediaId =  mediaAttributeInfo.mediaId WHERE \n" +
                            "q1.personId = '" + persons.getId() + "' ORDER BY mediaAttributeInfo.mediaDate, q1.mediaName");

                    while (rs.next()) {
                        // stores the data of resultset in set one by one in order to remove duplicate values
                        if (!mediaFileName.contains(fileMap.get(rs.getInt(2)))){
                            mediaFileName.add(fileMap.get(rs.getInt(2)));
                        }
                    }
                }

            }

            for (PersonIdentity persons : people) {
                ResultSet rs1 = statement.executeQuery("SELECT q1.personId, q1.mediaId, q1.mediaName FROM mediaAttributeInfo INNER JOIN\n" +
                        "(SELECT  mediaPersonRelation.personId, mediaPersonRelation.mediaId, mediaNameInfo.mediaName\n" +
                        "FROM mediaPersonRelation INNER JOIN mediaNameInfo ON mediaPersonRelation.mediaId = \n" +
                        "mediaNameInfo.mediaId) AS q1 ON q1.mediaId =  mediaAttributeInfo.mediaId WHERE \n" +
                        "mediaAttributeInfo.mediaDate is NULL  AND q1.personId = '" + persons.getId() + "' ORDER BY mediaAttributeInfo.mediaDate, q1.mediaName");

                while (rs1.next()) {
                    // stores the data of resultset in set one by one in order to remove duplicate values
                    if (!mediaFileName.contains(fileMap.get(rs1.getInt(2)))){
                        mediaFileName.add(fileMap.get(rs1.getInt(2)));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error Message: " + e.getMessage());
            // Retruns null if there is any error
            return null;
        }
        // Retruns list of media files which includes the individuals from the given list and are within the given range
        return mediaFileName;
    }

    /**
     * {@code findBiologicalFamilyMedia} This method if used to find the set of media files that includes
     * the specified person's immediate children
     *
     * @param person takes PersonIdentity object as input
     * @return set of media files
     */
    public List<FileIdentifier> findBiologicalFamilyMedia(PersonIdentity person){

        // Opens the connection with the database
        open();

        // Retrun null if person does not exists
        if (person == null){
            System.out.println("Person does not exists");
            return null;
        }

        // List that stores the media files
        List<FileIdentifier> mediaFiles = new LinkedList<>();

        // Throws the exception if there is any error
        try (Statement statement = conn.createStatement();
             Statement statement1 = conn.createStatement()){

            // ResultSet execute the the query and stores the result
            // The query returns the set of media files that includes the parents immediate children
            // This quesry return media in  ascending chronological order without null media having null values
            ResultSet mediaWithDate = statement.executeQuery("select q3.mediaId, mediaNameInfo.mediaName, q3.mediaDate from mediaNameInfo inner join \n" +
                    "(select mediaAttributeInfo.mediaId, mediaAttributeInfo.mediaDate\n" +
                    "from mediaAttributeInfo inner join\n" +
                    "(select mediaPersonRelation.mediaId, mediaPersonRelation.personId\n" +
                    "from mediaPersonRelation inner join\n" +
                    "(select child from recordChild where parent = '" + person.getId() + "') as q1 on\n" +
                    "mediaPersonRelation.personId = q1.child) as q2 on \n" +
                    "mediaAttributeInfo.mediaId = q2.mediaId where mediaDate is not null) \n" +
                    "as q3 on q3.mediaId = mediaNameInfo.mediaId order by q3.mediaDate,\n" +
                    "mediaNameInfo.mediaName;");

            // ResultSet execute the the query and stores the result
            // The query returns the set of media files that includes the parents immediate children
            // This quesry return media in  ascending chronological order without null media null values
            ResultSet mediaDateNull = statement1.executeQuery("select q3.mediaId, mediaNameInfo.mediaName, q3.mediaDate from mediaNameInfo inner join \n" +
                    "(select mediaAttributeInfo.mediaId, mediaAttributeInfo.mediaDate\n" +
                    "from mediaAttributeInfo inner join\n" +
                    "(select mediaPersonRelation.mediaId, mediaPersonRelation.personId\n" +
                    "from mediaPersonRelation inner join\n" +
                    "(select child from recordChild where parent = '" + person.getId() + "') as q1 on\n" +
                    "mediaPersonRelation.personId = q1.child) as q2 on \n" +
                    "mediaAttributeInfo.mediaId = q2.mediaId where mediaDate is null) \n" +
                    "as q3 on q3.mediaId = mediaNameInfo.mediaId order by q3.mediaDate,\n" +
                    "mediaNameInfo.mediaName;");


            // Iterate the result set
            while (mediaWithDate.next()) {
                for (int i = 1; i <= fileMap.size(); i++) {
                    // If related media found then it will add it to the list
                    if (fileMap.get(i).getFileName().equalsIgnoreCase(mediaWithDate.getString(2))) {
                        // Does not add media if it is already present in the list
                        if (!mediaFiles.contains(fileMap.get(i))){
                            mediaFiles.add(fileMap.get(i));
                        }
                    }
                }
            }

            // Iterate the result set
            while (mediaDateNull.next()) {
                for (int i = 1; i <= fileMap.size(); i++) {
                    // If related media found then it will add it to the set
                    if (fileMap.get(i).getFileName().equalsIgnoreCase(mediaDateNull.getString(2))) {
                        // Does not add media if it is already present in the list
                        if (!mediaFiles.contains(fileMap.get(i))){
                            mediaFiles.add(fileMap.get(i));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error Message: "+e.getMessage());
            // retrun null if there is any error
            return null;
        }

        // Retrun mediaFiles list
        return mediaFiles;
    }


    /**
     *{@code BiologicalRelation} This method shows how two individuals are related to each other
     *
     * @param person1 takes PersonIdentity object as input
     * @param person2 takes PersonIdentity object as inout
     * @return BiologicalRelation relation object which indicated how two individual are related
     * to each other
     */
    public BiologicalRelation findRelation( PersonIdentity person1, PersonIdentity person2 ){

        // Open the connection with the database
        open();

        // Return null
        if (person1 == null || person2 == null){
            System.out.println("Person 1 or Person 2 does not exists");
            return null;
        }

        // to store BiologicalRelation class object
        BiologicalRelation relation = null;

        // Array list to store the person1 ancestores
        List<Integer> parent1 = new LinkedList<>();

        // Array list to store the person2 ancestores
        List<Integer> parent2 = new LinkedList<>();

        // throws the exception if there is any error
        try(Statement statement = conn.createStatement();
            Statement statement1 = conn.createStatement()){

            // Resultset that stores person1 ancestores
            ResultSet rs = statement.executeQuery("SELECT "+COLUMN_PARENT+" FROM "+TABLE_PARENT_CHILD+
                    " WHERE CHILD = '"+person1.getId()+"';");

            // Resultset that stores person2 ancestores
            ResultSet rs1 = statement1.executeQuery("SELECT "+COLUMN_PARENT+" FROM "+TABLE_PARENT_CHILD+
                    " WHERE CHILD = '"+person2.getId()+"';");

            // Iterate the resultset to store ther data in list
            while (rs.next()){
                parent1.add(rs.getInt(1));
            }

            // Iterate the resultset to store the data in list
            while (rs1.next()){
                parent2.add(rs1.getInt(1));
            }

            // now this will find how two individuals are related to each other
            for (int i=0; i<parent1.size(); i++){
                for (int j=0; j<parent2.size(); j++){
                    if (parent1.get(i).equals(parent2.get(j))){
                        relation = new BiologicalRelation(0, 0);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error Message: "+e.getMessage());
            // retrun null if there is any error
            return null;
        }

        // retrun BiologicalRelation object which indicate how two person are related
        return relation;
    }


    /**
     * {@code descendents} This methods returns all the descendents of the family tree who are present
     * within the "generation". of the individual;
     * Here we are considering the children of "person" as 1 generation away
     *
     * @param person takes PersonIdentity object as input
     * @param generations takes generation of person as input
     * @return return set of all the descendents of the give person
     */
    public Set<PersonIdentity> descendents( PersonIdentity person, Integer generations ){

        //Opens the connection with the database
        open();

        // checks for the generation
        if(generations == -1 || generations == 0){
            System.out.println("Generation is invalid");
            return null;
        }

        // set to stores all the descendents
        Set<PersonIdentity> descendents = new HashSet<>();

        // Return null If the person does not exists
        if (person == null){
            System.out.println("Person does not exists");
            return null;
        }

        // throw exception if there is any error
        try(Statement statement = conn.createStatement()){

            // Resultset that stores sets of rows with persons descendents
            ResultSet rs = statement.executeQuery("SELECT "+COLUMN_CHILD+" FROM "+TABLE_PARENT_CHILD+
                    " WHERE "+COLUMN_PARENT+" = '"+person.getId()+"';");


            // Iterate the result set and stores the descendents in the set
            while (rs.next()){
                for (int i=1;i<= personMap.size(); i++){
                    // It will stores the descendents object in the set
                    if (personMap.get(i).getId() == rs.getInt(1)){
                        descendents.add(personMap.get(i));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error Message: "+e.getMessage());
            // Return null if there is any error
            return null;
        }

        // Return the list that consists descendents of the person
        // Return an empty list if no  descendents exists
        return descendents;
    }


    /**
     * {@code ancestores} This methods returns all the descendents of the family tree who are present
     * within the "generation". of the individuals.
     * Here we are considering the children of "person" as 1 generation away
     *
     * @param person takes PersonIdentity object as input
     * @param generations takes generation of person as input
     * @return return set of all the ancestores of the give person
     */
    public Set<PersonIdentity> ancestors( PersonIdentity person, Integer generations ){
        //Opens the connection with the database
        open();

        // set to stores all the ancestores
        Set<PersonIdentity> ancestores = new HashSet<>();

        // checks for the generation
        if(generations == -1 || generations == 0){
            System.out.println("Generation is invalid");
            return null;
        }

        // Return null If the person does not exists
        if (person == null){
            System.out.println("Person does not exists");
            return null;
        }


        // throw exception if there is any error
        try(Statement statement = conn.createStatement()){

            // Resultset that stores sets of rows with persons ancestores
            ResultSet rs = statement.executeQuery("SELECT "+COLUMN_PARENT+" FROM "+TABLE_PARENT_CHILD+
                    " WHERE "+COLUMN_CHILD+" = '"+person.getId()+"';");

            // Iterate the result set and stores the ancestores in the set
            while (rs.next()){
               for (int i=1;i<= personMap.size(); i++){
                   // It will stores the ancestores object in the set
                   if (personMap.get(i).getId() == rs.getInt(1)){
                       ancestores.add(personMap.get(i));
                   }
               }
            }
            System.out.println(ancestores);

        } catch (SQLException e) {
            System.out.println("Error Message: "+e.getMessage());
            // Return null if there is any error
            return null;
        }
        // Return the list that consists ancestores of the person
        // Return an empty list if no  ancestores exists
        return ancestores;
    }
}


