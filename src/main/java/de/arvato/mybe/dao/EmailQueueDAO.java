package de.arvato.mybe.dao;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import io.micrometer.core.lang.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.arvato.mybe.backend.exception.BaseException;
import de.arvato.mybe.backend.general.ErrorCodes;
import de.arvato.mybe.base.security.UserSession;
import de.arvato.mybe.common.EmailQueueStatusType;

@Repository
public class EmailQueueDAO extends AbstractBaseDAO
{
    private final ObjectMapper objectMapper;

    static final RowMapper<EmailMigrationDTO> EMAIL_MIGRATION_ROW_MAPPER = JdbcTemplateMapperFactory.newInstance()
            .ignorePropertyNotFound()
            .newRowMapper(EmailMigrationDTO.class);

    public EmailQueueDAO(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Nullable
    public EmailMigrationDTO getLatestEmailMigration()
    {
        String query = "SELECT * FROM email_migration order by id desc limit 1";
        List<EmailMigrationDTO> result = namedParameterJdbcTemplate.query(query, EMAIL_MIGRATION_ROW_MAPPER);
        if (result.size() > 0)
        {
            return result.get(0);
        }
        return null;
    }


    public long createEmailMigration(EmailMigrationDTO dto)
    {
        String query = "INSERT INTO email_migration (last_email_id, def_date) values (:lastEmailId, :defDate)";
        Date defDate = new Date();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = null;
        parameters = new MapSqlParameterSource()
                .addValue("lastEmailId", dto.getLastEmailId())
                .addValue("defDate", defDate);

        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[]{"id"});
        if (keyHolder.getKey() == null) return 0L;
        return (long) keyHolder.getKey();
    }

    public void updatePendingEmailQueueToCancel(long startId)
    {
        String query = "UPDATE email_queue SET status=:cancelStatus WHERE id > :startId " +
                " AND status=:pendingStatus";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("startId", startId)
                .addValue("cancelStatus", EmailQueueStatusType.CANCELLED)
                .addValue("pendingStatus", EmailQueueStatusType.PENDING);
        namedParameterJdbcTemplate.update(query, parameters);
    }

    public List<EmailQueueDTO> getEmailByStatus(String status)
    {
        String query = "SELECT * FROM email_queue WHERE status=:status";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("status", status);
        return namedParameterJdbcTemplate.query(query, parameters, new EmailQueueRowMapper());
    }



    public List<EmailQueueDTO> getAllEmail()
    {
        String query = "SELECT * FROM email_queue";
        return namedParameterJdbcTemplate.query(query, new EmailQueueRowMapper());
    }

    public long create(EmailQueueDTO dto)
    {
        String query = "INSERT INTO email_queue (recipient,sender,contents,status,type,def_date,def_user,mod_date,mod_user,subject,cc) " +
                "VALUES (:recipient,:sender,:contents,:status,:type,:defDate,:defUser,:modDate,:modUser,:subject,:cc)";
        Date defDate = new Date();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = null;
        try
        {
            parameters = new MapSqlParameterSource()
                    .addValue("recipient", dto.getRecipient())
                    .addValue("sender", dto.getSender())
                    .addValue("contents", dto.getContents())
                    .addValue("status", EmailQueueStatusType.PENDING)
                    .addValue("type", dto.getType())
                    .addValue("subject", dto.getSubject())
                    .addValue("defDate", defDate)
                    .addValue("defUser", UserSession.get().getRacf())
                    .addValue("modDate", defDate)
                    .addValue("modUser", UserSession.get().getRacf())
                    .addValue("cc", objectMapper.writeValueAsString(dto.getCc()));
        }
        catch (Exception e)
        {
            throw new BaseException(ErrorCodes.EMAIL_SETTING_DATA_CONVERSION_ERROR, "Email Queue JSON conversion Error" + e);
        }
        namedParameterJdbcTemplate.update(query, parameters, keyHolder, new String[]{"id"});

        if (keyHolder.getKey() == null) return 0L;
        return (long) keyHolder.getKey();
    }

    public long update(long emailQueueId, EmailQueueDTO dto)
    {
        String query = "UPDATE email_queue " +
                "SET recipient=:recipient, sender=:sender, contents=:contents, status=:status, type=:type, mod_date=:modDate, mod_user=:modUser WHERE id=:id";
        Date modDate = new Date();
        MapSqlParameterSource parameter = new MapSqlParameterSource()
                .addValue("recipient", dto.getRecipient())
                .addValue("sender", dto.getSender())
                .addValue("contents", dto.getContents())
                .addValue("status", dto.getStatus())
                .addValue("type", dto.getType())
                .addValue("modDate", modDate)
                .addValue("modUser", UserSession.get().getRacf())
                .addValue("id", emailQueueId);
        return namedParameterJdbcTemplate.update(query, parameter);
    }

    public long updateStatus(long emailQueueId, String status)
    {
        String query = "UPDATE email_queue SET status=:status WHERE id=:id";
        MapSqlParameterSource parameter = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", emailQueueId);
        return namedParameterJdbcTemplate.update(query, parameter);
    }

    private class EmailQueueRowMapper implements RowMapper<EmailQueueDTO>
    {
        @Override
        public EmailQueueDTO mapRow(ResultSet rs, int rowNum)
        {
            try
            {
                EmailQueueDTO emailQueueDTO = new EmailQueueDTO();
                emailQueueDTO.setId(rs.getLong("id"));
                emailQueueDTO.setSubject(rs.getString("subject"));
                emailQueueDTO.setRecipient(rs.getString("recipient"));
                emailQueueDTO.setStatus(rs.getString("status"));
                emailQueueDTO.setSender(rs.getString("sender"));
                emailQueueDTO.setType(rs.getString("type"));
                emailQueueDTO.setContents(rs.getString("contents"));

                String ccString = rs.getString("cc");

                if(StringUtils.isNotBlank(ccString))
                {
                    List<String> cc = objectMapper.readValue(rs.getString("cc"), new TypeReference<List<String>>()
                    {
                    });
                    emailQueueDTO.setCc(cc);
                }

                return emailQueueDTO;
            }
            catch (Exception e)
            {
                throw new BaseException(ErrorCodes.EMAIL_SETTING_DATA_CONVERSION_ERROR, "Email Queue JSON conversion Error" + e);
            }
        }
    }
}
