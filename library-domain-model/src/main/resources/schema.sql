-- ---------------------------------------------------------------------------
-- The tables of the library, created BEFORE Hibernate starts.
--
-- This file runs on every startup (spring.datasource.initialization-mode=always)
-- and Hibernate is told not to touch the schema (spring.jpa.hibernate.ddl-auto=none).
-- So the entity classes must match these tables, not the other way around.
--
-- The column names are written to match the Java field names exactly, because
-- of the naming strategy set in application.properties. That is why no @Column
-- annotation is needed anywhere in this project.
-- ---------------------------------------------------------------------------

-- Drop in reverse order of the foreign keys, so nothing is still referenced.
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS library_member;
DROP TABLE IF EXISTS category;

CREATE TABLE category (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (id)
);

-- MEMBER is a reserved word in MySQL 8, so the table is called library_member.
-- The entity therefore needs @Table(name = "library_member") - the one place in
-- this project where the class name and the table name are different.
CREATE TABLE library_member (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(150),
    joinDate DATE,
    PRIMARY KEY (id)
);

-- Many books belong to one category, so the FK lives here, on the "many" side.
-- A @ManyToOne field named "category" maps to the column category_id.
CREATE TABLE book (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(200) NOT NULL,
    author      VARCHAR(150),
    year        INT,
    addedDate   DATE,
    category_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES category (id)
);

-- A transaction is one borrow or one return: it points at one book and one member.
CREATE TABLE transaction (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    type            VARCHAR(10) NOT NULL,
    transactionDate DATE,
    book_id         BIGINT      NOT NULL,
    member_id       BIGINT      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transaction_book   FOREIGN KEY (book_id)   REFERENCES book (id),
    CONSTRAINT fk_transaction_member FOREIGN KEY (member_id) REFERENCES library_member (id)
);
