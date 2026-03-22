package de.arvato.mybe.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributionListDTO {

    private long id;
    private String title;
    private String description;
    private String email;
    private long orgUnitId;
    private String unitName;
    private String unitShortName;
    private String ownerRacf;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private LocalDateTime defDate;
    private String defUser;
    private LocalDateTime modDate;
    private String modUser;
    private List<DistributionEmployeeListDTO> employeeList;

    public String getFullName()
    {
        return firstName + (StringUtils.isEmpty(middleName) ? " " : String.format(" %s ", middleName)) + lastName;
    }
}
