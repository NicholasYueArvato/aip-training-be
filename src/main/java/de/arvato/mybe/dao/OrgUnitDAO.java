package de.arvato.mybe.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.arvato.mybe.backend.exception.BaseException;
import de.arvato.mybe.backend.general.ErrorCodes;
import de.arvato.mybe.base.config.AppConfig;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrgUnitDAO extends AbstractBaseDAO
{
    @Autowired
    private ObjectMapper objectMapper;
    private static final RowMapper<OrgUnitDTO> ROW_MAPPER =
            JdbcTemplateMapperFactory.newInstance()
                    .ignorePropertyNotFound()
                    .newRowMapper(OrgUnitDTO.class);

    public OrgUnitDTO getById(long id)
    {
        String query = "SELECT * FROM org_unit WHERE id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);

        List<OrgUnitDTO> results = namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
        return !results.isEmpty() ? results.get(0) : null;
    }

    public List<OrgUnitDTO> getOrgUnitByNameAndParent(String name, long parentId)
    {
        String query = "SELECT * FROM org_unit WHERE name=:name AND parent_id=:parentId ";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("parentId", parentId);

        return namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    public OrgUnitDTO getGlobalDeliverySite()
    {
        String query = "SELECT * FROM org_unit WHERE labels @> '{\"type\": \"global_delivery_site\"}'";

        List<OrgUnitDTO> results = namedParameterJdbcTemplate.query(query, ROW_MAPPER);
        return !results.isEmpty() ? results.get(0) : null;
    }

    public List<OrgUnitDTO> getAllOrgUnits()
    {
        String query = "SELECT o.*,e.first_name,e.middle_name,e.last_name, " +
                "(SELECT name FROM org_unit WHERE id = o.parent_id ) AS parentName " +
                "FROM org_unit o LEFT JOIN employee e ON o.lead_id = e.id;";

        return namedParameterJdbcTemplate.query(query, ROW_MAPPER);
    }

    public long createOrgUnit(OrgUnitDTO dto)
    {
        String query = "INSERT INTO org_unit " +
                "(name, labels, lead_id, parent_id, short_name, is_active) " +
                "VALUES " +
                "(:name, :labels ::jsonb,:leadId,:parentId,:shortName, :isActive ::boolean )";

        String json = null;
        try
        {
            json = objectMapper.writeValueAsString(dto.getUnitLabels());
        }
        catch (JsonProcessingException e)
        {
            throw new BaseException(ErrorCodes.GENERAL_EXCEPTION, "Error in creating organisation unit");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", dto.getName())
                .addValue("labels", json)
                .addValue("leadId", dto.getLeadId())
                .addValue("parentId", dto.getParentId())
                .addValue("shortName", dto.getShortName())
                .addValue("isActive", dto.getIsActive());

        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[]{"id"});

        if (keyHolder.getKey() == null) return 0L;
        return (long) keyHolder.getKey();
    }

    public long updateOrgUnit(long id, OrgUnitDTO dto) throws BaseException
    {
        String query = "UPDATE org_unit " +
                "SET name=:name, " +
                "labels=:labels ::jsonb, " +
                "lead_id=:leadId, " +
                "parent_id=:parentId, " +
                "short_name=:shortName, " +
                "is_active=:isActive ::boolean " +
                "WHERE id=:id";

        String json = null;
        try
        {
            json = objectMapper.writeValueAsString(dto.getUnitLabels());
        }
        catch (JsonProcessingException e)
        {
            throw new BaseException(ErrorCodes.GENERAL_EXCEPTION, "Error in updating organisation unit.");
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", dto.getName())
                .addValue("labels", json)
                .addValue("leadId", dto.getLeadId())
                .addValue("parentId", dto.getParentId())
                .addValue("shortName", dto.getShortName())
                .addValue("isActive", dto.getIsActive())
                .addValue("id", id);

        return namedParameterJdbcTemplate.update(query, parameters);
    }

    public long deleteOrgUnit(long id)
    {
        String query = "DELETE FROM org_unit WHERE id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);

        return namedParameterJdbcTemplate.update(query, parameters);
    }

    public List<OrgUnitDTO> getChildOrgUnit(long id)
    {
        String query = "SELECT * FROM org_unit " +
                "WHERE parent_id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);
        return namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
    }
}

