CREATE TABLE attendance
(
    id IDENTITY NOT NULL PRIMARY KEY,
    child_id INT,
    entry_date TIMESTAMP,
    exit_date TIMESTAMP,

    FOREIGN KEY (child_id) REFERENCES child(id)
);