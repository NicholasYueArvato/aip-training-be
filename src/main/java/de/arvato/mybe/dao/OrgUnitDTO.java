package de.arvato.mybe.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrgUnitDTO
{
    private long id;
    private String name;
    private Map<String, String> unitLabels;
    private String firstName;
    private String middleName;
    private String lastName;
    private long leadId;
    private String leadName;
    private long parentId;
    private String shortName;
    private Boolean isActive;
    private String parentName;
    private int level;
    private List<EmployeeDTO> employees;

    public void setLabels(String labels)
    {
        try
        {
            this.unitLabels = new ObjectMapper().readValue(labels, Map.class);
        }
        catch (Exception ex)
        {
            this.unitLabels = new HashMap<>();
        }
    }

    public String getLeadName()
    {
        if((!StringUtils.isBlank(firstName))){
            return firstName + (StringUtils.isBlank(middleName) ? " " : String.format(" %s ", middleName)) + lastName;
        }
        return "";
    }
}