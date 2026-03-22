package de.arvato.mybe.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributionListCSVDTO {
    private long id;
    private String title;
    private String email;
    private String orgUnitName;
    private String orgUnitShortName;
    private String ownerName;
    private String ownerRacf;
    private String ownerEmail;
    private String employeeName;
    private String employeeRacf;
    private String employeeEmail;
    private String modDate;
    private String modUser;
    private long orgUnitId;
}