
/*Projects Table

Meet Patel
B00899516

*/

/*Tables Required for Family Tree Management */

CREATE TABLE personNameInfo (
    personId int NOT NULL AUTO_INCREMENT,
    personName varchar(255) NOT NULL,
    PRIMARY KEY (personId)
);

CREATE TABLE personAttributeInfo (
    personDOB varchar(255),
    personDOD varchar(255),
    personGender varchar(255),
    personOccupation varchar(255),
    personId int,
    FOREIGN KEY (personId) REFERENCES personNameInfo(personId)
);

CREATE TABLE personNote (
    personNote varchar(255),
    personId int,
    lastUpdated TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (personId) REFERENCES personNameInfo(personId)	
);

CREATE TABLE personReference (
    personReference varchar(255),
    personId int,
    lastUpdated TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (personId) REFERENCES personNameInfo(personId)
);

CREATE TABLE recordPartneringDissolution (
    partner_1 int NOT NULL,
    partner_2 int NOT NULL,
    relationship varchar(255) NOT NULL,
    FOREIGN KEY (partner_1) REFERENCES personNameInfo(personId),
    FOREIGN KEY (partner_2) REFERENCES personNameInfo(personId)
);

CREATE TABLE recordChild (
    parent int NOT NULL,
    child int NOT NULL,
    FOREIGN KEY (parent) REFERENCES personNameInfo(personId),
    FOREIGN KEY (child) REFERENCES personNameInfo(personId)
);

/*Tables required for Media Archive Management*/

CREATE TABLE mediaNameInfo (
    mediaId int NOT NULL AUTO_INCREMENT,
    mediaName varchar(255) NOT NULL,
    mediaLocation varchar(255) NOT NULL,
    PRIMARY KEY (mediaId)
);

CREATE TABLE mediaAttributeInfo (
    mediaYear varchar(255),
    mediaDate varchar(255),
    mediaCity varchar(255),
	mediaId int NOT NULL,
    FOREIGN KEY (mediaId) REFERENCES mediaNameInfo(mediaId)
);

CREATE TABLE mediaTag (
    mediaTag varchar(255),
    mediaId int,
    FOREIGN KEY (mediaId) REFERENCES mediaNameInfo(mediaId)
);

CREATE TABLE mediaPersonRelation (
	mediaId int,
    personId int,
    FOREIGN KEY (mediaId) REFERENCES mediaNameInfo(mediaId),
    FOREIGN KEY (personId) REFERENCES personNameInfo(personId)
);