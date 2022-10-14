/**
 *
 * {@code FileIdentifier}
 *
 * This class is used to identify file using fileLocation from media archive
 * (Here we are taking file location instead of file name because name of two file
 * can be same but location of two file is to be different)
 * It contains three variable Int Id, String filename and String fileLocation which help to
 * create the object of each file. This object helps use to identify the file from the media archive.
 *
 * @author Meet Patel (B00899516)
 * Created on 2021-12-05
 * @version 1.0.0
 * @since 1.0,0
 */
public class FileIdentifier {

    private int id; // Stores id of the media
    private String fileName; //  Stores filename of the media
    private String fileLocation; // Stores filelocation of the media

    // Constructor helps us to create the object
    public FileIdentifier(int id, String fileName, String fileLocation) {
        this.id = id;
        this.fileName = fileName;
        this.fileLocation = fileLocation;
    }

    // Method used to get media id
    public int getId() {
        return id;
    }

    // Method used to get file name
    public String getFileName() {
        return fileName;
    }

    // Method used to get file location
    public String getFileLocation() {
        return fileLocation;
    }

}
