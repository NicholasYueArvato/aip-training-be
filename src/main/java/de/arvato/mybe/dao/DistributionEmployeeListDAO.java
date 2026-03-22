package de.arvato.mybe.dao;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DistributionEmployeeListDAO  extends AbstractBaseDAO{

     static final RowMapper<DistributionEmployeeListDTO> ROW_MAPPER = JdbcTemplateMapperFactory.newInstance()
            .ignorePropertyNotFound()
            .newRowMapper(DistributionEmployeeListDTO.class);

    static final RowMapper<DistributionListCSVDTO> CSV_ROW_MAPPER = JdbcTemplateMapperFactory.newInstance()
            .ignorePropertyNotFound()
            .newRowMapper(DistributionListCSVDTO.class);

    public List<DistributionEmployeeListDTO> getAllDistributionEmployeeList(){
        String query = "SELECT del.*, e.email AS employee_email, e.first_name, e.middle_name, e.last_name, ou.name AS orgUnitName " +
                "FROM distribution_employee_list del " +
                "LEFT JOIN employee e ON del.employee_racf = e.racf " +
                "LEFT JOIN org_unit ou ON e.org_unit_id = ou.id ";

        return namedParameterJdbcTemplate.query(query, ROW_MAPPER);
    }

    public List<DistributionEmployeeListDTO> getEmployeeListByDistributionId(long distribution_id, boolean activeOnly){
        String query = "SELECT del.*, e.email AS email, e.first_name, e.middle_name, e.last_name, ou.name AS orgUnitName " +
                "FROM distribution_employee_list del " +
                "LEFT JOIN employee e ON del.employee_racf = e.racf " +
                "LEFT JOIN org_unit ou ON e.org_unit_id = ou.id " +
                "WHERE distribution_id=:distribution_id ";
        if(activeOnly) {
            query += " AND (e.last_date is null or e.last_date > CURRENT_TIMESTAMP)";
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("distribution_id", distribution_id);

        return namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    public long createDistributionEmployee(DistributionEmployeeListDTO distributionEmployee){
        String query = "INSERT INTO distribution_employee_list (employee_racf, distribution_id) " +
                "VALUES " +
                "(:employee_racf, :distribution_id)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_racf", distributionEmployee.getEmployeeRacf())
                .addValue("distribution_id", distributionEmployee.getDistributionId());

        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[]{"id"});

        if(keyHolder.getKey() == null) return 0L;
        return (long) keyHolder.getKey();
    }

    public long updateDistributionEmployeeList(long id, DistributionEmployeeListDTO employeeList){
        String query = "INSERT INTO distribution_employee_list (employee_racf, distribution_id) " +
                "VALUES " +
                "(:employee_racf, :distribution_id) " +
                "WHERE id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_racf", employeeList.getEmployeeRacf())
                .addValue("distribution_id", employeeList.getDistributionId())
                .addValue("id", id);

        return namedParameterJdbcTemplate.update(query, parameters);
    }

    public long deleteByDistributionId(long distributionId){
        String query = "DELETE FROM distribution_employee_list WHERE distribution_id=:distribution_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("distribution_id", distributionId);
        return namedParameterJdbcTemplate.update(query, parameters);
    }

    public long deleteByEmployeeRacfAndDistributionId(String employeeRacf, long distributionId){
        String query = "DELETE FROM distribution_employee_list WHERE employee_racf=:employee_racf AND distribution_id=:distribution_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_racf", employeeRacf)
                .addValue("distribution_id", distributionId);

        return namedParameterJdbcTemplate.update(query, parameters);
    }

    public List<DistributionListCSVDTO> getAllCsvDistributionList(){
        String query = """
                SELECT dl.id, title, dl.email, dl.org_unit_id,
                CONCAT(owner.first_name, ' ', owner.middle_name, ' ', owner.last_name) AS owner_name,
                owner_racf, owner.email as owner_email, CONCAT(e.first_name, ' ', e.middle_name, ' ', e.last_name) AS employee_name,
                del.employee_racf, e.email as employee_email, dl.mod_date, dl.mod_user
                FROM distribution_list dl
                JOIN distribution_employee_list del ON dl.id=del.distribution_id
                JOIN employee e ON del.employee_racf=e.racf
                JOIN employee owner ON dl.owner_racf=owner.racf
                ORDER BY dl.id
                """;

        return namedParameterJdbcTemplate.query(query, CSV_ROW_MAPPER);
    }
}
