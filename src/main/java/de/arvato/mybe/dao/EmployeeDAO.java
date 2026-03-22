package de.arvato.mybe.dao;

import de.arvato.mybe.base.security.UserSession;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class EmployeeDAO extends AbstractBaseDAO
{
    private static final RowMapper<EmployeeDTO> ROW_MAPPER = JdbcTemplateMapperFactory.newInstance().ignorePropertyNotFound().newRowMapper(EmployeeDTO.class);

    public EmployeeDTO getEmployeeById(long id)
    {
        String query = "SELECT e.*, (s.first_name || COALESCE(' ' || s.middle_name, '') || ' ' || s.last_name) AS supervisorName, " +
                "s.racf as supervisorRacf, (SELECT name FROM org_unit o WHERE o.id=e.org_unit_id) AS orgUnit " +
                " from employee e " +
                "LEFT Join employee s on s.id = e.supervisor_id WHERE e.id=:id  ORDER BY id";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);

        List<EmployeeDTO> results = namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
        return !results.isEmpty() ? results.get(0) : null;
    }

    public EmployeeDTO getEmployeeByRacf(String racf)
    {
        String query = "SELECT * FROM employee WHERE LOWER(racf)=LOWER(:racf)";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("racf", racf);

        List<EmployeeDTO> results = namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
        return !results.isEmpty() ? results.get(0) : null;
    }

    public List<EmployeeDTO> getEmployeeByCandidateKeys(String racf, long catsId, long hrId, String email, long employeeIdToExclude)
    {
        String query = "SELECT * FROM employee " +
                "WHERE (LOWER(racf)=LOWER(:racf) " +
                "OR cats_id=:cats_id " +
                "OR hr_id=:hr_id " +
                "OR LOWER(email)=LOWER(:email)" +
                ") AND id != :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("racf", racf)
                .addValue("cats_id", catsId)
                .addValue("hr_id", hrId)
                .addValue("email", email)
                .addValue("id", employeeIdToExclude);

        return namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    public List<EmployeeDTO> getEmployeeByOrgUnitId(long org_unit_id)
    {
        String query = "SELECT * FROM employee WHERE org_unit_id=:org_unit_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("org_unit_id", org_unit_id);

        return namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    public List<EmployeeDTO> getEmployeeByDefDateAndCheckEmailSend(Date definedDate, boolean checkEmailSend)
    {
        String query = "SELECT e.*, (s.first_name || COALESCE(' ' || s.middle_name, '') || ' ' || s.last_name) AS supervisorName " +
                " FROM employee e " +
                " LEFT JOIN employee s on s.id = e.supervisor_id " +
                " WHERE e.check_email_send =:checkEmailSend AND e.def_date >=:defDate order by e.first_name";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("checkEmailSend", checkEmailSend)
                .addValue("defDate", definedDate);

        return namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    public List<EmployeeDTO> getAllEmployees()
    {
        String query = "SELECT e.*, (s.first_name || COALESCE(' ' || s.middle_name, '') || ' ' || s.last_name) AS supervisorName, " +
                "s.racf as supervisorRacf, (SELECT name FROM org_unit o WHERE o.id=e.org_unit_id) AS orgUnit " +
                " from employee e " +
                "left join employee s on s.id = e.supervisor_id order by e.first_name";

        return namedParameterJdbcTemplate.query(query, ROW_MAPPER);
    }

    public long createEmployee(EmployeeDTO dto)
    {
        String query = "INSERT INTO employee " +
                "(first_name, middle_name, last_name, email, racf, designation, joining_date, last_date, job_grade," +
                "def_date, def_user, mod_date, mod_user, supervisor_id, org_unit_id, cats_id, hr_id, activity_type, check_email_send) " +
                "VALUES " +
                "(:firstName, :middleName, :lastName, :email, :racf, :designation, :joiningDate, :lastDate, :jobGrade," +
                ":defDate, :defUser, :modDate, :modUser, :supervisorId, :orgUnitId, :catsId, :hrId, :activityType, :checkEmailSend)";

        Date defDate = new Date();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("firstName", dto.getFirstName())
                .addValue("middleName", dto.getMiddleName())
                .addValue("lastName", dto.getLastName())
                .addValue("email", dto.getEmail())
                .addValue("racf", dto.getRacf())
                .addValue("designation", dto.getDesignation())
                .addValue("joiningDate", dto.getJoiningDate())
                .addValue("lastDate", dto.getLastDate())
                .addValue("jobGrade", dto.getJobGrade())
                .addValue("defDate", defDate)
                .addValue("defUser", UserSession.get().getRacf())
                .addValue("modDate", defDate)
                .addValue("modUser", UserSession.get().getRacf())
                .addValue("supervisorId", dto.getSupervisorId())
                .addValue("orgUnitId", dto.getOrgUnitId())
                .addValue("catsId", dto.getCatsId())
                .addValue("hrId", dto.getHrId())
                .addValue("activityType", dto.getActivityType())
                .addValue("checkEmailSend", false);

        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[]{"id"});

        if (keyHolder.getKey() == null) return 0L;
        return (long) keyHolder.getKey();
    }

    public long updateEmployee(long id, EmployeeDTO dto)
    {
        String query = "UPDATE employee " +
                "SET first_name=:firstName, middle_name=:middleName, last_name=:lastName, email=:email, designation=:designation, " +
                "racf=:racf, joining_date=:joiningDate, last_date=:lastDate, job_grade=:jobGrade, mod_date=:modDate, mod_user=:modUser, " +
                "supervisor_id=:supervisorId, org_unit_id=:orgUnitId, cats_id=:catsId, hr_id=:hrId, activity_type=:activityType " +
                "WHERE id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("firstName", dto.getFirstName())
                .addValue("middleName", dto.getMiddleName())
                .addValue("lastName", dto.getLastName())
                .addValue("email", dto.getEmail())
                .addValue("designation", dto.getDesignation())
                .addValue("racf", dto.getRacf())
                .addValue("joiningDate", dto.getJoiningDate())
                .addValue("lastDate", dto.getLastDate())
                .addValue("jobGrade", dto.getJobGrade())
                .addValue("modDate", new Date())
                .addValue("modUser", UserSession.get().getRacf())
                .addValue("supervisorId", dto.getSupervisorId() == 0 ? null : dto.getSupervisorId())
                .addValue("orgUnitId", dto.getOrgUnitId())
                .addValue("id", id)
                .addValue("catsId", dto.getCatsId())
                .addValue("hrId", dto.getHrId())
                .addValue("activityType", dto.getActivityType());

        return namedParameterJdbcTemplate.update(query, parameters);
    }

    public long updateEmployeeCheckEmailSend(List<Long> employeeIDs) {
        String query = "UPDATE employee SET check_email_send=:checkEmailSend WHERE id IN (:id)";
        MapSqlParameterSource parameter = new MapSqlParameterSource()
                .addValue("checkEmailSend", true)
                .addValue("id", employeeIDs);
        return namedParameterJdbcTemplate.update(query, parameter);
    }

    public long deleteEmployeeById(long id)
    {
        String query = "DELETE FROM employee WHERE id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
        return namedParameterJdbcTemplate.update(query, parameters);
    }

    public boolean isReferencedAsSupervisor(long id)
    {
        String query = "SELECT id FROM employee WHERE supervisor_id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);

        return !namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER).isEmpty();
    }

    public void updateEmployeeOrganisationUnitId(long employeeId, long organisationUnitId)
    {
        String query = "UPDATE employee SET org_unit_id=:organisationUnitId,mod_date=:modDate,mod_user=:modUser WHERE id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("organisationUnitId", organisationUnitId == 0L ? null : organisationUnitId)
                .addValue("modDate", new Date()).addValue("modUser", UserSession.get().getRacf())
                .addValue("id", employeeId);

        namedParameterJdbcTemplate.update(query, parameters);
    }

    public void removeAllOrgUnitEmployee(long orgUnitId)
    {
        String query = "UPDATE employee SET org_unit_id=null,mod_date=:modDate,mod_user=:modUser WHERE org_unit_id = :orgUnitId";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("modDate", new Date())
                .addValue("modUser", "System")
                .addValue("orgUnitId", orgUnitId);

        namedParameterJdbcTemplate.update(query, parameters);
    }
}
