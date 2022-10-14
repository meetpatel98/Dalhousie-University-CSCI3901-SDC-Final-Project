import java.util.*;

public class Main {
    public static void main(String[] args) {

        String addPersonCommand = "1";
        String addAttributeCommand = "2";
        String addReferenceCommand = "3";
        String addNoteCommand = "4";
        String recordChild = "5";
        String recordPartnering = "6";
        String recordDissolution = "7";

        String addMediaFile = "8";
        String addMediaAttribute = "9";
        String addPeopleInMedia = "10";
        String tagMedia = "11";

        String findPerson = "12";
        String findMedia = "13";
        String findName = "14";
        String findMediaFile = "15";
        String findRelation = "16";
        String descendents = "17";
        String ancestores = "18";
        String nodesAndRef = "19";
        String findMediaByTag = "20";
        String findMediaByLocation = "21";
        String findIndividualsMedia = "22";
        String findBiologicalFamilyMedia = "23";
        String quitCommand = "quit";

        String userCommand = "";
        Scanner userInput = new Scanner(System.in);

        Genealogy genealogy = new Genealogy();

        Map<String, String> personAttributes = new HashMap<>();
        Map<String, String> mediaAttributes = new HashMap<>();

        System.out.println("\nCommands available");
        System.out.println();
        System.out.println("-------Family Tree Management--------");
        System.out.println();
        System.out.println("Press "+addPersonCommand + " to add Person");
        System.out.println("Press "+addAttributeCommand + " to add Person Attribute");
        System.out.println("Press "+addReferenceCommand + " to add Person Reference");
        System.out.println("Press "+addNoteCommand + " to add Person Note");
        System.out.println("Press "+recordChild+" to record Parent Child Relation");
        System.out.println("Press "+recordPartnering+ " to record Partnering");
        System.out.println("Press "+recordDissolution+ " to record Dissolution");
        System.out.println();
        System.out.println("-------Media Archive Management--------");
        System.out.println();
        System.out.println("Press "+addMediaFile+ " to add Media File");
        System.out.println("Press "+addMediaAttribute+" to add Media Attribute");
        System.out.println("Press "+addPeopleInMedia + " to add People in Media");
        System.out.println("Press "+tagMedia + " to add Tag in Media");
        System.out.println();
        System.out.println("-----------Reporting Function------------");
        System.out.println();
        System.out.println("Press "+findPerson+" to Find Person using Name");
        System.out.println("Press "+findMedia+" to Find Media using Name");
        System.out.println("Press "+findName+" to Find Person using Id");
        System.out.println("Press "+findMediaFile+" to Find Media using Id");
        System.out.println("Press "+nodesAndRef+" to Find Nodes and References of Media");
        System.out.println("Press "+findRelation+" to find the Relation");
        System.out.println("Press "+descendents+" to find the descendents");
        System.out.println("Press "+ancestores+" to find the ancestores");
        System.out.println("Press "+findMediaByTag+" to Find Media using Tag");
        System.out.println("Press "+findMediaByLocation+" to Find Media using Location");
        System.out.println("Press "+findIndividualsMedia+" to Find People in Media ");
        System.out.println("Press "+findBiologicalFamilyMedia+" to find Biological Family Media");

        System.out.println();
        System.out.println("Press "+quitCommand+" to Quit");
        System.out.println();
        do {
            userCommand = userInput.nextLine();
            if (userCommand.equalsIgnoreCase(addPersonCommand)) {
                System.out.println("Add Person");
                System.out.println();
                System.out.println("Enter Person's Name");
                String personName = userInput.nextLine();

                genealogy.addPerson(personName);
                System.out.println();

            } else if (userCommand.equalsIgnoreCase(addAttributeCommand)) {
                System.out.println("Add Attributes");
                System.out.println();
                System.out.println("The attributes data can be updated later");
                System.out.println();
                System.out.println("Enter Person's Id ");
                int personId = userInput.nextInt();
                userInput.nextLine();
                System.out.println("Entre Person's Date of Birth (Must be in YYYY-MM-DD format)");
                String dateOfBirth = userInput.nextLine();
                System.out.println("Enter Person's Date of Death (Must be in YYYY-MM-DD format)");
                String dateofDeath = userInput.nextLine();
                System.out.println("Entre Person's Gender");
                String gender = userInput.nextLine();
                System.out.println("Enter Person's Occupation");
                String occupation = userInput.nextLine();

                personAttributes.put("personDOB", dateOfBirth);
                personAttributes.put("personDOD", dateofDeath);
                personAttributes.put("personGender", gender);
                personAttributes.put("personOccupation", occupation);

                genealogy.recordAttributes(genealogy.personMap.get(personId), personAttributes);
                System.out.println();

            } else if (userCommand.equalsIgnoreCase(addReferenceCommand)) {
                System.out.println("Add Reference");
                System.out.println();
                System.out.println("Enter Person's Id");
                int personId = userInput.nextInt();
                userInput.nextLine();
                System.out.println("Entre Person's Reference");
                String reference = userInput.nextLine();

                genealogy.recordReference(genealogy.personMap.get(personId), reference);
                System.out.println();

            } else if (userCommand.equalsIgnoreCase(addNoteCommand)) {
                System.out.println("Add Note");
                System.out.println();
                System.out.println("Enter Person's Id");
                int personId = userInput.nextInt();
                userInput.nextLine();
                System.out.println("Enter Person's Note");
                String note = userInput.nextLine();
                genealogy.recordNote(genealogy.personMap.get(personId), note);
                System.out.println();
            }else if (userCommand.equalsIgnoreCase(recordPartnering)) {
                System.out.println("Record Partnering");
                System.out.println();
                System.out.println("Enter Id Of 1st Partner");
                int partner_1 = userInput.nextInt();
                System.out.println("Enter Id of 2nd Partner");
                int partner_2 = userInput.nextInt();
                userInput.nextLine();
                genealogy.recordPartnering(genealogy.personMap.get(partner_1), genealogy.personMap.get(partner_2));
                System.out.println();
            }
            else if (userCommand.equalsIgnoreCase(recordDissolution)) {
                System.out.println("Record Dissolution");
                System.out.println();
                System.out.println("Enter Id Of 1st Partner");
                int partner_1 = userInput.nextInt();
                System.out.println("Enter Id of 2nd Partner");
                int partner_2 = userInput.nextInt();
                userInput.nextLine();
                genealogy.recordDissolution(genealogy.personMap.get(partner_1), genealogy.personMap.get(partner_2));
                System.out.println();
            }else if (userCommand.equalsIgnoreCase(recordChild)) {
                System.out.println("Record Child");
                System.out.println();
                System.out.println("Enter Id of Parent");
                int parent = userInput.nextInt();
                System.out.println("Enter Id of Child");
                int child = userInput.nextInt();
                userInput.nextLine();
                genealogy.recordChild(genealogy.personMap.get(parent), genealogy.personMap.get(child));
                System.out.println();
            } else if (userCommand.equalsIgnoreCase(addMediaFile)) {
                System.out.println("Record Media File");
                System.out.println();
                System.out.println("Enter Media Location on HardDisk");
                String mediaLoaction = userInput.nextLine();
                genealogy.addMediaFile(mediaLoaction);
                System.out.println();
            } else if (userCommand.equalsIgnoreCase(addMediaAttribute)){
                System.out.println("Record Media Attribute");
                System.out.println();
                System.out.println("The attributes data can be updated later");
                System.out.println();
                System.out.println("Enter Media Id");
                int mediaId = userInput.nextInt();
                userInput.nextLine();
                System.out.println("Enter Media Year ");
                String year = userInput.nextLine();
                System.out.println("Enter Media Date (Must be in YYYY-MM-DD format)");
                String date = userInput.nextLine();
                System.out.println("Entre Media City");
                String city = userInput.nextLine();

                mediaAttributes.put("Year", year);
                mediaAttributes.put("Date", date);
                mediaAttributes.put("City", city);

                genealogy.recordMediaAttributes(genealogy.fileMap.get(mediaId), mediaAttributes);
                System.out.println();
            }
            else if (userCommand.equalsIgnoreCase(addPeopleInMedia)){
                List<PersonIdentity> people = new LinkedList<>();
                System.out.println("Add People In Media");
                System.out.println();
                System.out.println("Enter Media Id");
                int mediaId = userInput.nextInt();
                System.out.println("How many total people you want to add in Media");
                int totalPeople = userInput.nextInt();
                System.out.println("Enter the Id of the Person");
                for (int i=0; i<totalPeople; i++){
                    int peopleId = userInput.nextInt();
                    people.add(genealogy.personMap.get(peopleId));
                }
                userInput.nextLine();

                genealogy.peopleInMedia(genealogy.fileMap.get(mediaId), people);
                System.out.println();
            }
            else if (userCommand.equalsIgnoreCase(tagMedia)) {
                System.out.println("Add Tag to Media");
                System.out.println();
                System.out.println("Entre Media Id");
                int mediaId = userInput.nextInt();
                userInput.nextLine();
                System.out.println("Enter Media Tag");
                String tag = userInput.nextLine();

                genealogy.tagMedia(genealogy.fileMap.get(mediaId), tag);
                System.out.println();

            }else if (userCommand.equalsIgnoreCase(findPerson)) {
                System.out.println("Find Person");
                System.out.println();
                System.out.println("Enter Person's Name");
                String name = userInput.nextLine();
                System.out.println(genealogy.findPerson(name));
                System.out.println();
            }
            else if (userCommand.equalsIgnoreCase(findMedia)) {
                System.out.println("Find Media");
                System.out.println();
                System.out.println("Enter Media Name (With file format)");
                String name = userInput.nextLine();
                System.out.println(genealogy.findMediaFile(name));
                System.out.println();

            }else if (userCommand.equalsIgnoreCase(findName)) {
                System.out.println("Find Name");
                System.out.println();
                System.out.println("Enter Person's Id");
                int id = userInput.nextInt();
                userInput.nextLine();
                System.out.println(genealogy.findName(genealogy.personMap.get(id)));
                System.out.println();
            }
            else if (userCommand.equalsIgnoreCase(findMediaFile)) {
                System.out.println("Find Media File");
                System.out.println();
                System.out.println("Enter Media Id");
                int id = userInput.nextInt();
                userInput.nextLine();
                System.out.println(genealogy.findMediaFile(genealogy.fileMap.get(id)));
                System.out.println();

            }
            else if (userCommand.equalsIgnoreCase(nodesAndRef)) {
                System.out.println("Find Node and References");
                System.out.println();
                System.out.println("Enter Person's Id");
                int id = userInput.nextInt();
                userInput.nextLine();
                System.out.println(genealogy.notesAndReferences(genealogy.personMap.get(id)));
                System.out.println();
            }
            else if (userCommand.equalsIgnoreCase(findMediaByTag)) {
                System.out.println("Find Media by Tag");
                System.out.println();
                System.out.println("Enter Media Tag");
                String tag = userInput.nextLine();
                System.out.println("Entre Media Start Date (Must be in YYYY-MM-DD format)");
                String startDate = userInput.nextLine();
                System.out.println("Entre Media End Date (Must be in YYYY-MM-DD format)");
                String endDate = userInput.nextLine();
                System.out.println(genealogy.findMediaByTag(tag, startDate, endDate));
                System.out.println();
            }else if (userCommand.equalsIgnoreCase(findMediaByLocation)) {
                System.out.println("Find Media by Location");
                System.out.println();
                System.out.println("Enter Media location ");
                String location = userInput.nextLine();
                System.out.println("Enter Media Start Date (Must be in YYYY-MM-DD format)");
                String startDate = userInput.nextLine();
                System.out.println("Enter Media End Date (Must be in YYYY-MM-DD format)");
                String endDate = userInput.nextLine();
                System.out.println(genealogy.findMediaByLocation(location, startDate, endDate));
                System.out.println();
            }else if (userCommand.equalsIgnoreCase(findIndividualsMedia)) {
                System.out.println("Find Individual In Media");
                System.out.println();
                Set<PersonIdentity> people = new HashSet<>();
                System.out.println("Enter the number of person you want to find in from media");
                int total_people = userInput.nextInt();
                System.out.println("Enter "+total_people+ " people");
                for (int i=0; i<total_people; i++){
                    int personId = userInput.nextInt();
                    people.add(genealogy.personMap.get(personId));
                }
                userInput.nextLine();
                System.out.println("Enter Media Start Date (Must be in YYYY-MM-DD format)");
                String startDate = userInput.nextLine();
                System.out.println("Enter Media End Date (Must be in YYYY-MM-DD format)");
                String endDate = userInput.nextLine();
                System.out.println(genealogy.findIndividualsMedia(people, startDate, endDate));
                System.out.println();
            }else if (userCommand.equalsIgnoreCase(findBiologicalFamilyMedia)) {
                System.out.println("Find Biological Family Medi");
                System.out.println();
                System.out.println("Enter person id");
                int id = userInput.nextInt();
                userInput.nextLine();

                System.out.println(genealogy.findBiologicalFamilyMedia(genealogy.personMap.get(id)));

                System.out.println();
            }else if (userCommand.equalsIgnoreCase(findRelation)) {
                System.out.println("Find relation between two individual");
                System.out.println();
                System.out.println("Enter Person 1");
                int id1 = userInput.nextInt();
                System.out.println("Entrer Person 2");
                int id2 = userInput.nextInt();
                userInput.nextLine();

                System.out.println(genealogy.findRelation(genealogy.personMap.get(id1),
                        genealogy.personMap.get(id2)));

                System.out.println();
            }else if (userCommand.equalsIgnoreCase(descendents)) {
                System.out.println("Find descendents");
                System.out.println();
                System.out.println("Enter person id to find descendents");
                int id = userInput.nextInt();
                System.out.println("Enter Generations");
                int generation = userInput.nextInt();
                userInput.nextLine();

                System.out.println(genealogy.descendents(genealogy.personMap.get(id), generation));
                System.out.println();

            }else if (userCommand.equalsIgnoreCase(ancestores)) {
                System.out.println("Find ancestores");
                System.out.println();
                System.out.println("Enter person id to find ancestores");
                int id = userInput.nextInt();
                System.out.println("Enter Generations");
                int generation = userInput.nextInt();
                userInput.nextLine();
                System.out.println(genealogy.ancestors(genealogy.personMap.get(id), generation));
                System.out.println();
            }
            else if (userCommand.equalsIgnoreCase(quitCommand)) {
                System.out.println(userCommand);
            } else {
                System.out.println("Bad command: " + userCommand);
            }
        } while (!userCommand.equalsIgnoreCase("quit"));
    }
}
