package de.arvato.mybe.dao;

import java.util.List;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDAO extends AbstractBaseDAO{
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleDAO.class);


    private final static RowMapper<RoleDTO> ROW_MAPPER =
            JdbcTemplateMapperFactory.newInstance()
                    .ignorePropertyNotFound()
                    .newRowMapper(RoleDTO.class);

    public List<RoleDTO> getRole(long roleId)
    {
        //language=SQL
        String query = "SELECT r.* " +
                " FROM role r WHERE id=:roleId";

        StringBuilder querySb = new StringBuilder(query);

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("roleId", roleId);
        return namedParameterJdbcTemplate.query(querySb.toString(), parameters, ROW_MAPPER);
    }
    
    public List<RoleDTO> checkRole(String name)
    {
        String query = "SELECT r.* " +
                " FROM role r WHERE name=:name";

        StringBuilder querySb = new StringBuilder(query);

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name",name);
        return namedParameterJdbcTemplate.query(querySb.toString(), parameters, ROW_MAPPER);
    }
    
    public List<RoleDTO> getAllRoles()
    {
        String query = "SELECT r.* " +
                " FROM role r";

        StringBuilder querySb = new StringBuilder(query);
        return namedParameterJdbcTemplate.query(querySb.toString(), ROW_MAPPER);
    }
    
    public long addRole(RoleDTO dto)
    { 
        String query = "INSERT INTO role " +
                "(name, description) " +
                "VALUES " +
                "(:name,:description)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", dto.getName())
                .addValue("description", dto.getDescription());
        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[] { "id" });//no of rows
        return keyHolder.getKey().longValue(); //retrieve id
    }
    
    public long updateRole(RoleDTO dto,long id)
    {
        String query = "UPDATE role " +
        		"SET name=:name, description=:description " +
        		"WHERE id=:id";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
        		.addValue("name", dto.getName())
                .addValue("description", dto.getDescription())
                .addValue("id", dto.getId());
        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[] { "id" });//no of rows
        return keyHolder.getKey().longValue(); //retrieve id
    }
    
    public long deleteRole(long id)
    {
        String query = "DELETE FROM role " +
        				"WHERE id=:id";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
		        .addValue("id", id);
        if (namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[] { "id" }) >0) {
        	return keyHolder.getKey().longValue();
        }else {
        	return 0;
        }
    }
}
