package com.deals.server.repository;

public class SqlQueries {
    
    public static final String SAVE_DEALS = "INSERT INTO deals (uuid, valid, category) VALUES (?,?,?);";
    public static final String SAVE_DEAL_DETAILS = """
                    INSERT INTO deal_details (
                        deal_id, 
                        name, 
                        description,
                        image_url,
                        website_url,
                        terms_conditions,
                        tags,
                        valid_start_date,
                        valid_end_date,
                        venue,
                        latitude,
                        longitude
                        ) VALUES (
                        ?,?,?,?,?,?,?,?,?,?,?,?
                        )
                    """;            
    public static final String GET_DEALS_BY_CATEGORY = """
                        SELECT deal_id, 
                                name, 
                                description, 
                                image_url, 
                                website_url, 
                                terms_conditions, 
                                tags,
                                valid_start_date, 
                                valid_end_date, 
                                venue, 
                                latitude, 
                                longitude
                        FROM deals
                        JOIN deal_details
                        ON deals.uuid = deal_details.deal_id
                        WHERE category = (?)
                        AND valid = true;
                    """;

    public static final String GET_DEALS_BY_KEYWORD = """
                SELECT deal_id, 
                        name, 
                        description, 
                        image_url, 
                        website_url, 
                        terms_conditions, 
                        tags,
                        valid_start_date, 
                        valid_end_date, 
                        venue, 
                        latitude, 
                        longitude
                FROM deals
                JOIN deal_details
                ON deals.uuid = deal_details.deal_id
                WHERE
                    NAME LIKE CONCAT('%', ?, '%')
                OR
                    DESCRIPTION LIKE CONCAT('%', ?, '%')
                OR
                    TAGS LIKE CONCAT ('%', ?, '%')
                AND
                    valid = true;
             """;
    public static final String GET_UUIDS = "SELECT UUID FROM DEALS;";


    public static final String UPDATE_VALID_STATUS = """
        UPDATE deals 
        SET valid = FALSE 
        WHERE UUID IN 
            (select deal_id from deal_details where valid_end_date <= NOW());
            """;
    
    public static final String USER_EXIST = "SELECT count(*) FROM USER_INFO WHERE EMAIL = ?;";

    public static final String CREATE_USER = """
        INSERT INTO user_info (
            email,
            password,
            first_name,
            last_name,
            dob,
            receive_update
        )
        VALUES (
            ?,
            sha(?),
            ?,
            ?,
            ?,
            ?
        );
            """;

    public static final String SAVE_USER_DEAL = "INSERT INTO user_deal (email, deal_id) VALUES (?, ?);";

    public static final String GET_USER_DEAL = """
                SELECT 
                    deal_id, 
                    name, 
                    description, 
                    image_url, 
                    website_url, 
                    terms_conditions, 
                    tags,
                    valid_start_date, 
                    valid_end_date, 
                    venue, 
                    latitude, 
                    longitude
                FROM
                    deal_details
                WHERE
                    deal_id
                IN 
                    (SELECT deal_id from user_deal where email = ?);
            """;
    
    public static final String VERIFY_USER_CREDS = "SELECT count(*) FROM user_info WHERE email = ? AND password = sha(?);";

    public static final String DELETE_USER_DEAL = "DELETE FROM user_deal WHERE email= ? AND deal_id = ?;";

    public static final String GET_SUBSCRIBERS_EMAIL = "SELECT email FROM user_info WHERE receive_update = true;";
}