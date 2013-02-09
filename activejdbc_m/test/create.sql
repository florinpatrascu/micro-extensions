DROP TABLE IF EXISTS articles;
CREATE TABLE articles (
      id  int(11) NOT NULL  auto_increment PRIMARY KEY,
      title VARCHAR(100),
      summary VARCHAR(256),
      author VARCHAR(100)
  );