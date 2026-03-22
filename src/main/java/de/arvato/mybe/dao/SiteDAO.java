package de.arvato.mybe.dao;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SiteDAO extends AbstractBaseDAO
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteDAO.class);

    private final static RowMapper<SiteDTO> ROW_MAPPER =
            JdbcTemplateMapperFactory.newInstance()
                    .ignorePropertyNotFound()
                    .newRowMapper(SiteDTO.class);

    public long create(SiteDTO dto)
    {
        String query = "INSERT INTO Site " +
                "(name, url, description) " +
                "VALUES " +
                "(:name,:url,:description)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", dto.getName())
                .addValue("url", dto.getUrl())
                .addValue("description", dto.getDescription());
        namedParameterJdbcTemplate.update(query, parameters, keyHolder);
        return keyHolder.getKey().longValue();

    }

    public List<SiteDTO> findSitesByStatus(String status)
    {
        String query = "SELECT s.* " +
                " FROM Site s WHERE status=:status";

        StringBuilder querySb = new StringBuilder(query);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("status", status);
        return namedParameterJdbcTemplate.query(querySb.toString(), parameters, ROW_MAPPER);
    }
}
