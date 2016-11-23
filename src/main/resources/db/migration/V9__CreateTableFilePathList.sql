CREATE TABLE file_info_list (
app_user_id INT NOT NULL
,client_id VARCHAR(64) NOT NULL
,file_path VARCHAR(2048) NOT NULL
,file_hash VARCHAR(64) NOT NULL
,document_id VARCHAR(64) NOT NULL
,document_size INT NOT NULL
,removed INT NOT NULL
,created_at TIMESTAMP NOT NULL DEFAULT NOW()
,updated_at TIMESTAMP NOT NULL
,removed_at TIMESTAMP
,FOREIGN KEY(app_user_id) REFERENCES app_user_list(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;
