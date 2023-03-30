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
                        SELECT uuid, 
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
                SELECT uuid, 
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

}