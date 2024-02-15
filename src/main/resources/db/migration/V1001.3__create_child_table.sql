CREATE TABLE child
(
    id IDENTITY NOT NULL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    parent_id INT,
    school_id INT,

    FOREIGN KEY (parent_id) REFERENCES parent(id),
    FOREIGN KEY (school_id) REFERENCES school(id)
);