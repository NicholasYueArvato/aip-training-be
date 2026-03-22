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
public class UserDAO extends AbstractBaseDAO{

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);

    private final static RowMapper<UserDTO> ROW_MAPPER =
            JdbcTemplateMapperFactory.newInstance()
                    .ignorePropertyNotFound()
                    .newRowMapper(UserDTO.class);
    
    public List<UserDTO> getUser(long userId)
    {
        String query = "SELECT u.* " +
                " FROM public.\"user\" u WHERE id=:userId";

        StringBuilder querySb = new StringBuilder(query);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("userId", userId );
        return namedParameterJdbcTemplate.query(querySb.toString(), parameters, ROW_MAPPER);
    }
    
    public List<UserDTO> checkUser(String racf)
    {
        String query = "SELECT u.* " +
                " FROM  public.\"user\" u WHERE racf=:racf";

        StringBuilder querySb = new StringBuilder(query);

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("racf", racf );
        return namedParameterJdbcTemplate.query(querySb.toString(), parameters, ROW_MAPPER);
    }

    public List<UserDTO> getAllUsers()
    {
        String query = "SELECT u.* " +
                " FROM public.\"user\" u";

        StringBuilder querySb = new StringBuilder(query);
        return namedParameterJdbcTemplate.query(querySb.toString(), ROW_MAPPER);
    }
    
    public long addUser(UserDTO dto)
    { 
        String query = "INSERT INTO \"user\" " +
                "(first_name, last_name, email, racf, oid) " +
                "VALUES " +
                "(:firstName,:lastName,:email,:racf,:oid)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("firstName", dto.getFirstName())
                .addValue("lastName", dto.getLastName())
		        .addValue("email", dto.getEmail())
		        .addValue("racf", dto.getRacf())
		        .addValue("oid", dto.getOid());
        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[] { "id" });//no of rows
        return keyHolder.getKey().longValue(); //retrieve id
    }
    
    public long updateUser(UserDTO dto,long id)
    {
    	String checkQuery = "SELECT u.* " +
                " FROM \"user\" u WHERE id=:id";
        StringBuilder querySb = new StringBuilder(checkQuery);
        MapSqlParameterSource checkDtoParam = new MapSqlParameterSource().addValue("id", dto.getId() ); 
        List<UserDTO> ret = namedParameterJdbcTemplate.query(querySb.toString(), checkDtoParam, ROW_MAPPER);
        if (ret.size()==0) { //if an employee is return then return 0
        	return 0;
        }
        String query = "UPDATE \"user\" " +
        		"SET first_name=:firstName, last_name=:lastName, email=:email, racf=:racf, oid=:oid " +
        		"WHERE id=:id";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("firstName", dto.getFirstName())
                .addValue("lastName", dto.getLastName())
		        .addValue("email", dto.getEmail())
		        .addValue("racf", dto.getRacf())
		        .addValue("oid", dto.getOid())
		        .addValue("id", id);
        
        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[] { "id" });//no of rows
        return keyHolder.getKey().longValue(); //retrieve id
    }
    
    public long deleteUser(long id)
    {
        String query = "DELETE FROM \"user\" " +
        				"WHERE id=:id";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
		        .addValue("id", id);
        if (namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[] { "id" })>0) {
        	return keyHolder.getKey().longValue();
        }else {
        	return 0;
        }
    }
}
