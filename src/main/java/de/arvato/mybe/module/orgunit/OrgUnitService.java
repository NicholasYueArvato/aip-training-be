package de.arvato.mybe.module.orgunit;

import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import de.arvato.mybe.backend.exception.BaseException;
import de.arvato.mybe.backend.exception.EntityNotFoundException;
import de.arvato.mybe.backend.general.ErrorCodes;
import de.arvato.mybe.backend.util.transaction.TransactionalByException;
import de.arvato.mybe.common.OrgUnitType;
import de.arvato.mybe.dao.EmployeeDAO;
import de.arvato.mybe.dao.EmployeeDTO;
import de.arvato.mybe.dao.OrgUnitDAO;
import de.arvato.mybe.dao.OrgUnitDTO;
import de.arvato.mybe.util.ObjectHelper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrgUnitService
{
    private final OrgUnitDAO orgUnitDAO;
    private final EmployeeDAO employeeDAO;

    public OrgUnitDTO getByOrgUnitId(long id) {
        OrgUnitDTO orgUnit = orgUnitDAO.getById(id);
        validateEntity(id, orgUnit);

        List<EmployeeDTO> employees = employeeDAO.getEmployeeByOrgUnitId(id);
        orgUnit.setEmployees(employees);
        EmployeeDTO employee = employeeDAO.getEmployeeById(orgUnit.getLeadId());
        orgUnit.setLeadName(employee.getFullName());
        return orgUnit;
    }

    public List<OrgUnitDTO> getAllOrgUnit()
    {
        List<OrgUnitDTO> orgUnitList = orgUnitDAO.getAllOrgUnits();

        // grouping children by the parent
        Map<Long, List<OrgUnitDTO>> parentChildMap = orgUnitList
                .stream()
                .collect(groupingBy(OrgUnitDTO::getParentId));

        List<OrgUnitDTO> list = parentChildMap.get(0L);

        List<OrgUnitDTO> result = new ArrayList<>();
        for (OrgUnitDTO parent : list)
        {
            buildTree(result, parentChildMap, parent, 0);
        }
        return result;
    }

    private void buildTree(List<OrgUnitDTO> result,
                           Map<Long, List<OrgUnitDTO>> parentChildMap,
                           OrgUnitDTO parent,
                           int level)
    {
        List<OrgUnitDTO> childByParentId = parentChildMap.get(parent.getId());
        result.add(parent);
        parent.setLevel(++level);

        if (childByParentId != null)
        {
            for (OrgUnitDTO orgUnit : childByParentId)
            {
                buildTree(result, parentChildMap, orgUnit, level);
            }
        }
    }

    @TransactionalByException
    public long createOrgUnit(OrgUnitDTO orgUnit)
    {
        if (!orgUnitDAO.getOrgUnitByNameAndParent(orgUnit.getName(), orgUnit.getParentId()).isEmpty())
        {
            throw new BaseException(ErrorCodes.ORGUNIT_DUPLICATE_NAME, "Organisation unit with Name: " + orgUnit.getName() + " already exist");
        }

        if(!orgUnit.getIsActive())
        {
            if(orgUnit.getEmployees().size()>0)
            {
                throw new BaseException(ErrorCodes.ORGUNIT_HAS_EMPLOYEE,
                        "Unable to assign employee to inactive organisation unit.");
            }
        }

        long organisationUnitId = orgUnitDAO.createOrgUnit(orgUnit);
        if (organisationUnitId > 0)
        {
            updateEmployeeOrgUnitId(organisationUnitId, orgUnit.getEmployees());
        }
        return organisationUnitId;
    }

    @TransactionalByException
    public boolean updateOrgUnit(long orgUnitId, OrgUnitDTO orgUnit)
    {
        validateEntity(orgUnitId, orgUnitDAO.getById(orgUnitId));

        if(!orgUnit.getIsActive())
        {
            if(orgUnit.getEmployees().size()>0)
            {
                throw new BaseException(ErrorCodes.ORGUNIT_HAS_EMPLOYEE,
                        "Unable to assign employee to inactive organisation unit.");
            }
        }


        boolean updated = orgUnitDAO.updateOrgUnit(orgUnitId, orgUnit) > 0;
        if (updated)
        {
            checkNewOrgUnitEmployees(orgUnitId, orgUnit.getEmployees());
        }
        return updated;
    }

    @TransactionalByException
    public void updateEmployeeOrgUnitId(long organisationUnitId, List<EmployeeDTO> orgUnitEmployees)
    {
        for (EmployeeDTO employee : orgUnitEmployees)
        {
            if (employee.getOrgUnitId() > 0)
            {
                throw new BaseException(ErrorCodes.ORGUNIT_DUPLICATE_EMPLOYEE, employee.getFullName() +
                        " already exists in other organisation unit");
            }
            employeeDAO.updateEmployeeOrganisationUnitId(employee.getId(), organisationUnitId);
        }
    }

    private void removeEmployeeOrgUnit(List<Long> removeEmployeeIds)
    {
        for (Long id : removeEmployeeIds)
        {
            employeeDAO.updateEmployeeOrganisationUnitId(id, 0L);
        }
    }

    private void checkNewOrgUnitEmployees(long organisationUnitId, List<EmployeeDTO> orgUnitEmployees)
    {
        List<EmployeeDTO> existingEmployee = employeeDAO.getEmployeeByOrgUnitId(organisationUnitId);
        List<Long> existingEmployeeIds = existingEmployee
                .stream()
                .map(EmployeeDTO::getId)
                .collect(Collectors.toList());

        List<Long> currentEmployeeIds = orgUnitEmployees
                .stream()
                .map(EmployeeDTO::getId)
                .collect(Collectors.toList());

        List<EmployeeDTO> newEmployees = orgUnitEmployees
                .stream()
                .filter(e -> !existingEmployee.contains(e))
                .collect(Collectors.toList());
        updateEmployeeOrgUnitId(organisationUnitId, newEmployees);

        List<Long> removeEmployeeIds = ObjectHelper.findDifferent(existingEmployeeIds, currentEmployeeIds);
        removeEmployeeOrgUnit(removeEmployeeIds);
    }

    @TransactionalByException
    public boolean deleteByOrgUnitId(long orgUnitId)
    {
        validateEntity(orgUnitId, orgUnitDAO.getById(orgUnitId));

        if (orgUnitDAO.getChildOrgUnit(orgUnitId).isEmpty())
        {
            boolean deleted = orgUnitDAO.deleteOrgUnit(orgUnitId) > 0;
            if (deleted)
            {
                employeeDAO.removeAllOrgUnitEmployee(orgUnitId);
            }
            return deleted;
        }
        else
        {
            throw new BaseException(ErrorCodes.ORGUNIT_REFERENCE_BY_CHILD, "Delete not allowed. Organisation unit is referenced by at least 1 child unit.");
        }
    }

    private void validateEntity(long orgUnitId, OrgUnitDTO orgUnit) {
        if (null == orgUnit) {
            throw new EntityNotFoundException(ErrorCodes.ORGUNIT_NOT_FOUND, "orgUnitId", orgUnitId);
        }
    }

    @Cacheable(value = "businessUnit")
    public OrgUnitDTO getBusinessUnit(long orgUnitId) {
        OrgUnitDTO orgUnit = getByOrgUnitId(orgUnitId);

        if (orgUnit != null && isTeam(orgUnit.getUnitLabels())) {
            return getBusinessUnit(orgUnit.getParentId());
        } else {
            return orgUnit;
        }
    }

    private boolean isTeam(Map<String, String> unitLabels) {
        return unitLabels.containsKey("type") && (unitLabels.get("type").equalsIgnoreCase(OrgUnitType.TEAM) || unitLabels.get("type")
                .equalsIgnoreCase(OrgUnitType.SUB_TEAM));
    }
}
