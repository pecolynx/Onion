CREATE TABLE lang_list (
id INT NOT NULL
,code VARCHAR(2) NOT NULL
,name VARCHAR(16) NOT NULL
,PRIMARY KEY(id)
,UNIQUE(code)
) ENGINE=InnoDB;
