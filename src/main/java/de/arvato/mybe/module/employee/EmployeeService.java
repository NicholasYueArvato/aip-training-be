package de.arvato.mybe.module.employee;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;

import de.arvato.mybe.backend.exception.BaseException;
import de.arvato.mybe.backend.exception.EntityNotFoundException;
import de.arvato.mybe.backend.general.ErrorCodes;
import de.arvato.mybe.backend.util.transaction.TransactionalByException;
import de.arvato.mybe.dao.EmployeeDAO;
import de.arvato.mybe.dao.EmployeeDTO;
import de.arvato.mybe.dao.OrgUnitDAO;
import de.arvato.mybe.dao.OrgUnitDTO;
import de.arvato.mybe.module.orgunit.OrgUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeService
{
    private final EmployeeDAO employeeDAO;

    private final OrgUnitDAO orgUnitDAO;

    private final OrgUnitService orgUnitService;

    private static final FastDateFormat dateFormatter = FastDateFormat.getInstance("dd/MM/yyyy");

    public List<EmployeeDTO> getEmployee(long employeeId)
    {
        return Collections.singletonList(employeeDAO.getEmployeeById(employeeId));
    }

    @Cacheable(value = "employees")
    public List<EmployeeDTO> getCachedEmployee()
    {
        return getAllEmployees();
    }

    public List<EmployeeDTO> getAllEmployees()
    {
        return employeeDAO.getAllEmployees();
    }

    public List<EmployeeDTO> getEmployeesByBU(List<String> shortNameList)
    {
        List<EmployeeDTO> employeeList = getAllEmployees();

        for (EmployeeDTO employee : employeeList)
        {
            OrgUnitDTO businessUnit = getBusinessUnit(employee.getOrgUnitId());

            if(businessUnit != null){
                employee.setOrgUnit(businessUnit.getShortName());
                employee.setOrgUnitId(businessUnit.getId());
            }
        }

        return employeeList
                .stream()
                .filter(emp -> shortNameList.contains(emp.getOrgUnit()))
                .toList();
    }

    private OrgUnitDTO getBusinessUnit(long orgUnitId)
    {
        OrgUnitDTO orgUnit = orgUnitDAO.getById(orgUnitId);

        if (orgUnit != null && !isBusinessUnit(orgUnit.getUnitLabels()))
        {
            return getBusinessUnit(orgUnit.getParentId());
        }
        else
        {
            return orgUnit;
        }
    }

    private boolean isBusinessUnit(Map<String, String> unitLabels)
    {
        return unitLabels.containsKey("type") && unitLabels.get("type").equalsIgnoreCase("business_unit");
    }

    public long createEmployee(EmployeeDTO employee)
    {
        trimEmployeeData(employee);
        validateEmployeeData(employee);
        validateEmployeeUniqueFieldsForDuplicate(employee);

        return employeeDAO.createEmployee(employee);
    }

    public boolean updateEmployee(long employeeId, EmployeeDTO employee)
    {
        trimEmployeeData(employee);
        validateEmployeeData(employee);
        validateEmployeeUniqueFieldsForDuplicate(employee);

        EmployeeDTO employeeFromDB = employeeDAO.getEmployeeById(employeeId);
        if (employeeFromDB == null)
        {
            throw new EntityNotFoundException(ErrorCodes.EMPLOYEE_NOT_FOUND, "employeeId", employeeId);
        }

        return employeeDAO.updateEmployee(employeeId, employee) > 0;
    }

    public boolean deleteEmployeeById(long employeeId)
    {
        EmployeeDTO employee = employeeDAO.getEmployeeById(employeeId);
        validateEntity(employeeId, employee);

        if (employee.getOrgUnitId() > 0)
        {
            throw new BaseException(ErrorCodes.EMPLOYEE_EXIST_IN_ORGUNIT, "Delete not allowed, employee still in organisation unit");
        }

        if (employeeDAO.isReferencedAsSupervisor(employee.getId()))
        {
            throw new BaseException(ErrorCodes.EMPLOYEE_REFERENCE_SUPERVISOR, "Delete not allowed, referenced by at least one employee as supervisor");
        }

        return employeeDAO.deleteEmployeeById(employeeId) > 0;
    }

    private void validateEntity(long employeeId, EmployeeDTO employee)
    {
        if (null == employee)
        {
            throw new EntityNotFoundException(ErrorCodes.EMPLOYEE_NOT_FOUND, "employeeId", employeeId);
        }
    }

    private void validateEmployeeData(EmployeeDTO employee)
    {
        validateEmployeeField("RACF", employee.getRacf());
        validateEmployeeField("first name", employee.getFirstName());
        validateEmployeeField("last name", employee.getLastName());
        validateEmployeeField("email", employee.getEmail());
        validateEmployeeField("designation", employee.getDesignation());
        validateEmployeeField("cats id", employee.getCatsId());
        validateEmployeeField("hr id", employee.getHrId());

        validateEmployeeJoiningDate(employee.getJoiningDate());
        validateEmployeeLastDate(employee.getJoiningDate(), employee.getLastDate());
    }

    private void validateEmployeeField(String field, String value)
    {
        if (StringUtils.isBlank(value))
        {
            throw new BaseException(ErrorCodes.EMPLOYEE_FILE_INCOMPLETE, "Incomplete employee data, " + field + " is required.");
        }
    }

    private void validateEmployeeField(String field, long id)
    {
        if (id <= 0)
        {
            throw new BaseException(ErrorCodes.EMPLOYEE_FILE_INCOMPLETE, "Incomplete employee data, " + field + " is required.");
        }
    }

    private void validateEmployeeJoiningDate(Date joiningDate)
    {
        if (joiningDate == null)
        {
            throw new BaseException(ErrorCodes.EMPLOYEE_JOIN_DATE_IS_INVALID, "Incomplete employee data, joining date is required.");
        }
    }

    private void validateEmployeeLastDate(Date joiningDate, Date lastDate)
    {
        if (lastDate != null && !lastDate.after(joiningDate))
        {
            throw new BaseException(ErrorCodes.EMPLOYEE_LAST_DATE_IS_INVALID, "Last date cannot be before joining date");
        }
    }

    @TransactionalByException
    public void saveUploadFile(String uploadFileName, InputStream fsInputStream, boolean replaceSupervisor)
    {
        log.info("Start saving employee data from import file, {}", uploadFileName);

        List<EmployeeDTO> employeesFromDataFile = new CsvToBeanBuilder<EmployeeDTO>(new InputStreamReader(fsInputStream))
                .withType(EmployeeDTO.class)
                .build()
                .parse();
        saveFileToDB(employeesFromDataFile, replaceSupervisor);
    }

    @TransactionalByException
    protected void saveFileToDB(List<EmployeeDTO> employeesFromDataFile, boolean replaceSupervisor)
    {
        employeesFromDataFile.forEach(employee -> {
            trimEmployeeData(employee);
            validateEmployeeData(employee);

            EmployeeDTO employeeToUpdate = employeeDAO.getEmployeeByRacf(employee.getRacf());
            if (employeeToUpdate == null)
            {
                validateEmployeeUniqueFieldsForDuplicate(employee);
                employeeDAO.createEmployee(employee);
                return;
            }

            employee.setId(employeeToUpdate.getId());
            employee.setOrgUnitId(employeeToUpdate.getOrgUnitId());
            employee.setSupervisorId(employeeToUpdate.getSupervisorId());

            validateEmployeeUniqueFieldsForDuplicate(employee);
            employeeDAO.updateEmployee(employee.getId(), employee);
        });

        employeesFromDataFile.forEach(employee -> {
            EmployeeDTO employeeToUpdate = employeeDAO.getEmployeeByRacf(employee.getRacf());

            if (employeeToUpdate.getSupervisorId() > 0 && !replaceSupervisor)
            {
                return;
            }

            if (StringUtils.isBlank(employee.getSupervisorRacf()))
            {
                throw new BaseException(ErrorCodes.EMPLOYEE_NO_SUPERVISOR, "Supervisor is required.");
            }

            EmployeeDTO newSupervisor = employeeDAO.getEmployeeByRacf(employee.getSupervisorRacf());
            if (newSupervisor == null)
            {
                throw new BaseException(ErrorCodes.EMPLOYEE_NOT_FOUND, String.format("Supervisor Racf : %s for %s was not found.", employee.getSupervisorRacf(), employee.getFullName()));
            }

            employeeToUpdate.setSupervisorId(newSupervisor.getId());
            employeeDAO.updateEmployee(employeeToUpdate.getId(), employeeToUpdate);
        });
    }

    private void trimEmployeeData(EmployeeDTO employee)
    {
        Optional.ofNullable(employee.getFirstName())
                .map(String::trim)
                .ifPresent(employee::setFirstName);

        Optional.ofNullable(employee.getMiddleName())
                .map(String::trim)
                .ifPresent(employee::setMiddleName);

        Optional.ofNullable(employee.getLastName())
                .map(String::trim)
                .ifPresent(employee::setLastName);

        Optional.ofNullable(employee.getEmail())
                .map(String::trim)
                .ifPresent(employee::setEmail);

        Optional.ofNullable(employee.getDesignation())
                .map(String::trim)
                .ifPresent(employee::setDesignation);

        Optional.ofNullable(employee.getRacf())
                .map(String::trim)
                .ifPresent(employee::setRacf);

        Optional.ofNullable(employee.getSupervisorRacf())
                .map(String::trim)
                .ifPresent(employee::setSupervisorRacf);

        Optional.ofNullable(employee.getActivityType())
                .map(String::trim)
                .ifPresent(employee::setActivityType);

        Optional.ofNullable(employee.getJobGrade())
                .map(String::trim)
                .ifPresent(employee::setJobGrade);
    }

    private void validateEmployeeUniqueFieldsForDuplicate(EmployeeDTO employeeToValidate)
    {
        List<EmployeeDTO> employeeList = employeeDAO.getEmployeeByCandidateKeys(
                employeeToValidate.getRacf(), employeeToValidate.getCatsId(), employeeToValidate.getHrId(), employeeToValidate.getEmail(), employeeToValidate.getId()
        );

        employeeList.forEach(employeeFromDb -> {
            if (employeeToValidate.getRacf().compareToIgnoreCase(employeeFromDb.getRacf()) == 0)
            {
                throw new BaseException(ErrorCodes.DUPLICATE_EMPLOYEE_RACF, "Employee with RACF: " + employeeToValidate.getRacf() + " already exist");
            }
            if (employeeToValidate.getCatsId() == employeeFromDb.getCatsId())
            {
                throw new BaseException(ErrorCodes.DUPLICATE_EMPLOYEE_CATS_ID, "Employee with CATS ID: " + employeeToValidate.getCatsId() + " already exist");
            }
            if (employeeToValidate.getHrId() == employeeFromDb.getHrId())
            {
                throw new BaseException(ErrorCodes.DUPLICATE_EMPLOYEE_HR_ID, "Employee with HRID: " + employeeToValidate.getHrId() + " already exist");
            }
            if (employeeToValidate.getEmail().compareToIgnoreCase(employeeFromDb.getEmail()) == 0)
            {
                throw new BaseException(ErrorCodes.DUPLICATE_EMPLOYEE_EMAIL, "Employee with Email: " + employeeToValidate.getEmail() + " already exist");
            }
        });
    }

    public String exportEmployeeData() {
        try (StringWriter writer = new StringWriter();
                CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
                        CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            List<EmployeeDTO> employeeDTOList = employeeDAO.getAllEmployees();
            // Write CSV header
            String[] header = { "firstName", "middleName", "lastName", "email", "designation",
                    "racf", "joiningDate", "lastDate", "activityType", "HRID", "CATSID", "supervisorRacf",
                    "supervisorName", "orgUnit", "businessUnit" };
            csvWriter.writeNext(header);
            // Write DTO data to CSV
            for (EmployeeDTO employeeDTO : employeeDTOList) {
                String[] rowData = { employeeDTO.getFirstName(), employeeDTO.getMiddleName(), employeeDTO.getLastName(),
                        employeeDTO.getEmail(), employeeDTO.getDesignation(),employeeDTO.getRacf(),
                        formatDate(employeeDTO.getJoiningDate()), formatDate(employeeDTO.getLastDate()),employeeDTO.getActivityType(),
                        Long.toString(employeeDTO.getHrId()), Long.toString(employeeDTO.getCatsId()),employeeDTO.getSupervisorRacf(),
                        employeeDTO.getSupervisorName(), employeeDTO.getOrgUnit(), getBusinessUnitName(employeeDTO.getOrgUnitId()) };
                csvWriter.writeNext(rowData);
            }

            return writer.toString();
        } catch (Exception e) {
            log.error("Failed to export employee data : ",e);
            throw new BaseException(ErrorCodes.EMPLOYEE_DATA_EXPORT_FAILED, "Failed to export employee data");
        }
    }

    private String getBusinessUnitName(long id) {
        String name = "N.A";
        try{
            name = orgUnitService.getBusinessUnit(id).getName();
        }catch (Exception e){
            log.error("Business Unit not found, : " + id);
        }
        return name;
    }

    private String formatDate(Date date) {
        if (date != null) {
            return dateFormatter.format(date);
        }
        return "";
    }
}
