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
public class DistributionListDAO extends AbstractBaseDAO {

    private static final RowMapper<DistributionListDTO> ROW_MAPPER = JdbcTemplateMapperFactory.newInstance()
            .ignorePropertyNotFound()
            .newRowMapper(DistributionListDTO.class);

    public List<DistributionListDTO> getAllDistributionList(){
        String query = "SELECT dl.*, ou.name AS unitName, ou.short_name AS unitShortName, " +
                "owner.first_name, owner.middle_name, owner.last_name " +
                "FROM distribution_list dl LEFT JOIN org_unit ou ON dl.org_unit_id = ou.id " +
                "LEFT JOIN employee owner ON dl.owner_racf = owner.racf " +
                "ORDER BY dl.id";

        return namedParameterJdbcTemplate.query(query, ROW_MAPPER);
    }

    public List<DistributionListDTO> getDistributionListById(long id){
        String query = "SELECT dl.*, ou.name AS unit_name, ou.short_name AS unit_short_name, " +
                "owner.first_name, owner.middle_name, owner.last_name " +
                "FROM distribution_list dl LEFT JOIN org_unit ou ON dl.org_unit_id = ou.id " +
                "LEFT JOIN employee owner ON dl.owner_racf = owner.racf " +
                "WHERE dl.id=:id ORDER BY dl.id";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);

        return namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    public List<DistributionListDTO> getDistributionListByRacf(String racf){
        String query = "SELECT dl.title, dl.email " +
                "FROM distribution_list dl LEFT JOIN distribution_employee_list de ON dl.id = de.distribution_id " +
                "WHERE de.employee_racf=:racf";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("racf", racf);

        return namedParameterJdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    public long createDistributionList(DistributionListDTO distributionList){
        String query = "INSERT INTO distribution_list (title, description, email, " +
                       "org_unit_id, owner_racf, def_date, def_user, mod_date, mod_user) " +
                       "VALUES " +
                       "(:title, :description, :email, :org_unit_id, :owner_racf, " +
                       ":def_date, :def_user, :mod_date, :mod_user)";

        Date defDate = new Date();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("title", distributionList.getTitle())
                .addValue("description", distributionList.getDescription())
                .addValue("email", distributionList.getEmail())
                .addValue("org_unit_id", distributionList.getOrgUnitId())
                .addValue("owner_racf", distributionList.getOwnerRacf())
                .addValue("def_date", defDate)
                .addValue("def_user", UserSession.get().getRacf())
                .addValue("mod_date", defDate)
                .addValue("mod_user", UserSession.get().getRacf());

        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[]{"id"});

        if(keyHolder.getKey() == null) return 0L;
        return (long) keyHolder.getKey();
    }

    public long updateDistributionList(long id, DistributionListDTO distributionList){
        String query = "Update distribution_list SET title=:title, description=:description, email=:email, " +
                "org_unit_id=:org_unit_id, owner_racf=:owner_racf, mod_date=:mod_date, mod_user=:mod_user WHERE id=:id";

        Date mod_date = new Date();

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("title", distributionList.getTitle())
                .addValue("description", distributionList.getDescription())
                .addValue("email", distributionList.getEmail())
                .addValue("org_unit_id", distributionList.getOrgUnitId())
                .addValue("owner_racf", distributionList.getOwnerRacf())
                .addValue("mod_date", mod_date)
                .addValue("mod_user", UserSession.get().getRacf())
                .addValue("id", id);

        return namedParameterJdbcTemplate.update(query, parameters);
    }

    public long deleteDistributionListById(long id){
        String query = "DELETE FROM distribution_list WHERE id=:id";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
        return namedParameterJdbcTemplate.update(query, parameters);
    }
}
