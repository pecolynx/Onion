CREATE TABLE field_core_list (
id INT AUTO_INCREMENT
,version INT NOT NULL DEFAULT 1
,created_at TIMESTAMP NOT NULL DEFAULT NOW()
,updated_at TIMESTAMP NOT NULL
,name VARCHAR(64) NOT NULL
,field_type INT NOT NULL
,seq INT NOT NULL
,mapping_id INT
,PRIMARY KEY(ID)
,FOREIGN KEY(mapping_id) REFERENCES mapping_list(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;
