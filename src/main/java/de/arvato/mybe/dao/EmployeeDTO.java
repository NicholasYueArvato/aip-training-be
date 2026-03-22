package de.arvato.mybe.dao;

import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO
{
    private long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String email;
    private String designation;
    private String racf;
    @CsvDate(value = "dd/MM/yyyy")
    private Date joiningDate;
    @CsvDate(value = "dd/MM/yyyy")
    private Date lastDate;
    private String supervisorRacf;
    private Date defDate;
    private String defUser;
    private Date modDate;
    private String modUser;
    private String supervisorName;
    private long supervisorId;
    private String orgUnit;
    private long orgUnitId;
    private String jobGrade;
    private long catsId;
    private long hrId;
    private String activityType;

    public boolean isActive()
    {
        if (this.lastDate != null)
            return (new Date()).before(this.lastDate);
        return true;
    }

    public String getFullName()
    {
        return firstName + (StringUtils.isEmpty(middleName) ? " " : String.format(" %s ", middleName)) + lastName;
    }
}
