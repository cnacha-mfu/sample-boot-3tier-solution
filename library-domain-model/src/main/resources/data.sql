-- ---------------------------------------------------------------------------
-- Example rows. This file runs after schema.sql, on every startup.
-- Because schema.sql drops the tables first, the data is reset every time you
-- restart the app. That is on purpose: the sample always starts from a known state.
-- ---------------------------------------------------------------------------

INSERT INTO category (id, name, description) VALUES
    (1, 'Programming', 'Software development and computer languages'),
    (2, 'Databases',   'Database design, SQL and data modelling'),
    (3, 'Fiction',     'Novels and short stories');

INSERT INTO library_member (id, name, email, joinDate) VALUES
    (1, 'Somchai Jaidee',    'somchai@mfu.ac.th',  '2024-06-01'),
    (2, 'Malee Rakdee',      'malee@mfu.ac.th',    '2024-08-15'),
    (3, 'Anan Wongsawat',    'anan@mfu.ac.th',     '2025-01-20');

INSERT INTO book (id, title, author, year, addedDate, category_id) VALUES
    (1, 'Effective Java',                  'Joshua Bloch',      2018, '2024-05-10', 1),
    (2, 'Clean Code',                      'Robert C. Martin',  2008, '2024-05-10', 1),
    (3, 'Spring in Action',                'Craig Walls',       2022, '2024-07-02', 1),
    (4, 'Head First Design Patterns',      'Eric Freeman',      2020, '2024-09-11', 1),
    (5, 'SQL Antipatterns',                'Bill Karwin',       2010, '2024-06-18', 2),
    (6, 'Database System Concepts',        'Abraham Silberschatz', 2019, '2024-06-18', 2),
    (7, 'High Performance MySQL',          'Baron Schwartz',    2012, '2025-02-01', 2),
    (8, 'The Hobbit',                      'J. R. R. Tolkien',  1937, '2024-03-25', 3),
    (9, 'Nineteen Eighty-Four',            'George Orwell',     1949, '2024-03-25', 3),
    (10, 'The Old Man and the Sea',        'Ernest Hemingway',  1952, '2025-03-14', 3);

INSERT INTO transaction (id, type, transactionDate, book_id, member_id) VALUES
    (1, 'borrow', '2025-05-02', 1, 1),
    (2, 'return', '2025-05-16', 1, 1),
    (3, 'borrow', '2025-06-03', 5, 2),
    (4, 'borrow', '2025-06-20', 8, 3);
