CREATE TABLE app_user_auth_token_list (
app_user_id INT
,auth_token VARCHAR(64) NOT NULL
,expires TIMESTAMP NOT NULL
,PRIMARY KEY(app_user_id)
,FOREIGN KEY(app_user_id) REFERENCES app_user_list(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;
