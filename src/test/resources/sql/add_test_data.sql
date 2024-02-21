INSERT INTO parent (firstname, lastname) VALUES
('John', 'Doe'),
('Jane', 'Smith');

INSERT INTO school (name, hour_price) VALUES
('School A', 10.50),
('School B', 12.75);

INSERT INTO child (firstname, lastname, parent_id, school_id) VALUES
('Alice', 'Doe', 1, 1),
('Sam', 'Doe', 1, 2),
('Bob', 'Smith', 2, 2);

-- Insert attendance data
INSERT INTO attendance (child_id, entry_date, exit_date) VALUES
(1, '2024-02-15 08:00:00', '2024-02-15 15:00:00'),
(1, '2024-02-16 08:30:00', '2024-02-16 15:30:00'),
(1, '2023-02-15 08:00:00', '2023-02-15 18:00:00'),
(2, '2024-01-31 08:00:00', '2024-02-01 18:00:00'),
(2, '2024-02-29 08:00:00', '2024-03-01 18:00:00'),
(3, '2024-02-15 08:15:00', '2024-02-15 15:15:00');