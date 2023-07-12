CREATE TABLE Person(
    id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name varchar(100) NOT NULL,
    age int NOT NULL
);

INSERT INTO Person(name, age) VALUES ('Bryan Mason', 2001);
INSERT INTO Person(name, age) VALUES ('Regina Lynch', 2018);
INSERT INTO Person(name, age) VALUES ('Wilfred Thomas', 2017);
INSERT INTO Person(name, age) VALUES ('Loren Warner', 2021);
INSERT INTO Person(name, age) VALUES ('Osborne Robbins', 2023);