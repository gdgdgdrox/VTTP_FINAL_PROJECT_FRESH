CREATE TABLE categories (
	name varchar(56) NOT NULL UNIQUE PRIMARY KEY
);

INSERT INTO categories(name) VALUES
	('attractions'),
    ('bars_clubs'),
    ('food_beverages'),
    ('tours');

CREATE TABLE deals (
	uuid varchar(128) NOT NULL PRIMARY KEY,
    valid boolean NOT NULL DEFAULT true,
    category varchar(56) NOT NULL,
    FOREIGN KEY (category) REFERENCES categories(name)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE TABLE deal_details (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    deal_id varchar(128) NOT NULL,
    name varchar(255) NOT NULL,
    description text NOT NULL,
    image_url varchar(255),
    website_url varchar(255),
    terms_conditions text NOT NULL,
    tags varchar(255) NOT NULL,
    valid_start_date datetime NOT NULL,
    valid_end_date datetime NOT NULL,
    venue varchar(128) NOT NULL,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    FOREIGN KEY (deal_id) REFERENCES deals(uuid) ON DELETE CASCADE
);

CREATE TABLE user_info (
    email varchar(56) NOT NULL PRIMARY KEY,
    password varchar(128) NOT NULL,
    first_name varchar(56) NOT NULL,
    last_name varchar(56) NOT NULL,
    dob date NOT NULL,
    receive_update boolean default false
);

CREATE TABLE user_deal (
    deal_id varchar(128) NOT NULL,
    email varchar(56) NOT NULL,
    PRIMARY KEY (deal_id, email),
    FOREIGN KEY (email) REFERENCES user_info(email) 
    ON DELETE CASCADE 
    ON UPDATE CASCADE
);

