DROP TABLE IF EXISTS author;

CREATE TABLE author (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(250) NOT NULL
);

DROP TABLE IF EXISTS books;

CREATE TABLE books (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    author_id INT NOT NULL,
    title     VARCHAR(250) NOT NULL,
    priceOld  VARCHAR(250) DEFAULT NULL,
    price     VARCHAR(250) DEFAULT NULL,
    FOREIGN KEY (author_id) references author(id)
);
