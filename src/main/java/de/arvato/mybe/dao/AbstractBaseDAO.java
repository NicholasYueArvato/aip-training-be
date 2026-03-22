package de.arvato.mybe.dao;


import de.arvato.mybe.base.security.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Abstract DAO for JDBC access
 */
public abstract class AbstractBaseDAO extends JdbcDaoSupport
{
    public final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected ApplicationJdbcTemplate applicationJdbcTemplate;

    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @PostConstruct
    protected void initializeDataSource()
    {
        setDataSource(dataSource);
    }

    @PostConstruct
    protected void initializeJdbcTemplate()
    {
        setJdbcTemplate(applicationJdbcTemplate);
    }

    @Autowired
    protected AuthHelper authHelper;

    /**
     * Returns the ApplicationJdbcTemplate
     *
     * @return
     */
    protected ApplicationJdbcTemplate getApplicationJdbcTemplate()
    {
        return (ApplicationJdbcTemplate) getJdbcTemplate();
    }

    /**
     * Returns the NamedParameterJdbcTemplate
     *
     * @return NamedParameterJdbcTemplate
     */
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate()
    {
        return namedParameterJdbcTemplate;
    }

    /**
     * Set the NamedParameterJdbcTemplate
     *
     * @param namedParameterJdbcTemplate
     */
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
}
