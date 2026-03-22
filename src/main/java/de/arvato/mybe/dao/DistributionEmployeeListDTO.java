package de.arvato.mybe.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributionEmployeeListDTO {

    private long id;
    private String employeeRacf;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String orgUnitId;
    private String orgUnitName;
    private long distributionId;

    public String getFullName()
    {
        return firstName + (StringUtils.isEmpty(middleName) ? " " : String.format(" %s ", middleName)) + lastName;
    }
}
