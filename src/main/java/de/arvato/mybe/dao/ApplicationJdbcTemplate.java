package de.arvato.mybe.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Extended JDBC template
 * Do common jdbc stuffs here
 */
public class ApplicationJdbcTemplate extends JdbcTemplate
{
    /**
     * Constructor
     *
     * @param datasource
     */
    public ApplicationJdbcTemplate(DataSource datasource)
    {
        super(datasource);
    }

}
