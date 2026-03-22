package de.arvato.mybe.dao;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class HolidayDAO extends AbstractBaseDAO
{
    private static final RowMapper<HolidayDTO> ROW_MAPPER = JdbcTemplateMapperFactory.newInstance().ignorePropertyNotFound().newRowMapper(HolidayDTO.class);

    public List<HolidayDTO> getAllHolidays()
    {
        String query = "SELECT id, name, description, date " +
                "FROM holiday " +
                "ORDER BY date ASC";
        return namedParameterJdbcTemplate.query(query, ROW_MAPPER);
    }

    public List<HolidayDTO> getHolidayByYear(int year)
    {
        String query = "SELECT h.id, h.name, h.description, h.date " +
                "FROM holiday h " +
                "WHERE DATE_PART('year', h.date)=:year " +
                "ORDER BY date ASC";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("year", year);

        return namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    public HolidayDTO getHolidayById(long id)
    {
        String query = "SELECT id, name, description, date " +
                "FROM holiday " +
                "WHERE id=:id " +
                "ORDER BY date ASC";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);

        List<HolidayDTO> result = namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
        return !result.isEmpty() ? result.get(0) : null;
    }

    public HolidayDTO getHolidayByDate(LocalDate date) {
        String query = "SELECT id, name, description, date " +
                "FROM holiday " +
                "WHERE date=:date ";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("date", date);

        List<HolidayDTO> result = namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
        return !result.isEmpty() ? result.get(0) : null;
    }

    public long addHoliday(HolidayDTO holidayDTO)
    {
        String query = "INSERT INTO holiday " +
                "(name, date, description) " +
                "VALUES " +
                "(:name, :date, :description)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", holidayDTO.getName())
                .addValue("date", holidayDTO.getDate())
                .addValue("description", holidayDTO.getDescription());

        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[]{"id"});

        if (keyHolder.getKey() == null) return 0L;
        return (long) keyHolder.getKey();
    }

    public long updateHoliday(long id, HolidayDTO holidayDTO)
    {
        String query = "UPDATE holiday " +
                "SET name=:name, date=:date, description=:description " +
                "WHERE id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", holidayDTO.getName())
                .addValue("date", holidayDTO.getDate())
                .addValue("description", holidayDTO.getDescription())
                .addValue("id", id);

        return namedParameterJdbcTemplate.update(query, parameters);
    }

    public long deleteHoliday(long id)
    {
        String query = "DELETE FROM holiday WHERE id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);

        return namedParameterJdbcTemplate.update(query, parameters);
    }
}
