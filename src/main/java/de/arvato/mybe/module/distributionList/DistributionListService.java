package de.arvato.mybe.module.distributionList;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.opencsv.CSVWriter;

import de.arvato.mybe.backend.exception.BaseException;
import de.arvato.mybe.backend.general.ErrorCodes;
import de.arvato.mybe.backend.util.transaction.TransactionalByException;
import de.arvato.mybe.dao.DistributionEmployeeListDAO;
import de.arvato.mybe.dao.DistributionEmployeeListDTO;
import de.arvato.mybe.dao.DistributionListCSVDTO;
import de.arvato.mybe.dao.DistributionListDAO;
import de.arvato.mybe.dao.DistributionListDTO;
import de.arvato.mybe.dao.OrgUnitDTO;
import de.arvato.mybe.module.orgunit.OrgUnitService;
import de.arvato.mybe.util.ObjectHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributionListService {

    private final DistributionListDAO distributionListDAO;
    private final DistributionEmployeeListDAO distributionEmployeeListDAO;
    private final OrgUnitService orgUnitService;

    @TransactionalByException
    public boolean createDistributionList(DistributionListDTO distributionList) {
        validateDistribution(distributionList);

        long distributionId = distributionListDAO.createDistributionList(distributionList);

        if (distributionId > 0 && !CollectionUtils.isEmpty(distributionList.getEmployeeList())) {
            createDistributionEmployeeList(distributionId, distributionList.getEmployeeList());
        }

        return (distributionId > 0);
    }

    private void createDistributionEmployeeList(long distributionId, List<DistributionEmployeeListDTO> employeeList){
        for(DistributionEmployeeListDTO employee : employeeList){
            employee.setDistributionId(distributionId);
            distributionEmployeeListDAO.createDistributionEmployee(employee);
        }
    }

    public List<DistributionListDTO> getAllDistributionList(){
        List<DistributionListDTO> distributionList = distributionListDAO.getAllDistributionList();

        enrichDistributionList(distributionList);

        return distributionList;
    }

    public List<DistributionListDTO> getDistributionListByRacf(String racf){
        List<DistributionListDTO> distributionList = distributionListDAO.getDistributionListByRacf(racf);

        enrichDistributionList(distributionList);

        return distributionList;
    }

    public List<DistributionListDTO> getDistributionListById(long id)
    {
        List<DistributionListDTO> distributionList = distributionListDAO.getDistributionListById(id);
        if(distributionList.isEmpty()){
            throw new BaseException(ErrorCodes.EMAIL_DISTRIBUTION_NOT_FOUND, "Email Distribution List not found for ID: " + id);
        }

        distributionList.forEach(distribution -> {
            List<DistributionEmployeeListDTO> distributionEmployeeList = distributionEmployeeListDAO.getEmployeeListByDistributionId(distribution.getId(), true);
            distribution.setEmployeeList(distributionEmployeeList);

        });

        return distributionList;
    }

    public boolean updateDistributionList(long id, DistributionListDTO distributionList){
        validateDistribution(distributionList);

        if(!isDistributionListExist(id)){
            throw new BaseException(ErrorCodes.EMAIL_DISTRIBUTION_NOT_FOUND, "Email Distribution List not found for ID: " + id);
        }

        boolean updated = (distributionListDAO.updateDistributionList(id,distributionList) > 0) ;

        if(updated){
            updateDistributionEmployeeList(id, distributionList.getEmployeeList());
        }

        return updated;
    }

    private void updateDistributionEmployeeList(long distributionId, List<DistributionEmployeeListDTO> employeeList){
        List<String> existingEmployeeRacfs = distributionEmployeeListDAO.getEmployeeListByDistributionId(distributionId, false)
                .stream()
                .map(DistributionEmployeeListDTO::getEmployeeRacf).toList();

        List<String> currentRacfs = new ArrayList<>();
        for(DistributionEmployeeListDTO employee: employeeList){
            if(!existingEmployeeRacfs.contains(employee.getEmployeeRacf())){
                employee.setDistributionId(distributionId);
                distributionEmployeeListDAO.createDistributionEmployee(employee);
            }

            currentRacfs.add(employee.getEmployeeRacf());
        }

        List<String> removeEmployeeRacfs = ObjectHelper.getDifferent(existingEmployeeRacfs, currentRacfs);
        for(String employeeRacf : removeEmployeeRacfs){
            distributionEmployeeListDAO.deleteByEmployeeRacfAndDistributionId(employeeRacf, distributionId);
        }
    }

    public boolean deleteDistributionList(long id){

        if(!isDistributionListExist(id)){
            throw new BaseException(ErrorCodes.EMAIL_DISTRIBUTION_NOT_FOUND, "Email Distribution List not found for ID: " + id);
        }

        distributionEmployeeListDAO.deleteByDistributionId(id);
        return (distributionListDAO.deleteDistributionListById(id) > 0);
    }

    private void enrichDistributionList(List<DistributionListDTO> distributionList){
        distributionList.forEach(distribution -> {
            List<DistributionEmployeeListDTO> distributionEmployeeList = distributionEmployeeListDAO.getEmployeeListByDistributionId(distribution.getId(), true);

            distribution.setEmployeeList(distributionEmployeeList);
        });
    }

    private boolean isDistributionListExist(long id){
        return distributionListDAO.getDistributionListById(id).size() > 0;
    }

    private void validateDistribution(DistributionListDTO dto) {
        validateDistributionField(dto.getTitle(), "Incomplete email distribution data, title is required.");
        validateDistributionField(dto.getEmail(), "Incomplete email distribution data, email is required.");
        validateDistributionField(dto.getOwnerRacf(), "Incomplete email distribution data, owner racf is required.");
        if (CollectionUtils.isEmpty(dto.getEmployeeList())) {
            throw new BaseException(ErrorCodes.EMAIL_DISTRIBUTION_DATA_INCOMPLETE,
                    "Incomplete email distribution data, employee is required.");
        }
    }

    private void validateDistributionField(String value, String message)
    {
        if (StringUtils.isBlank(value))
        {
            throw new BaseException(ErrorCodes.EMAIL_DISTRIBUTION_DATA_INCOMPLETE, message);
        }
    }

    public String exportDistributionListCSV() {
        try (StringWriter writer = new StringWriter();
                CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
                        CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            List<DistributionListCSVDTO> distributionEmployeeListDTOList = distributionEmployeeListDAO.getAllCsvDistributionList();
            // Write CSV header
            String[] header = { "ID", "Title", "Email", "Business Unit", "Business Unit Short Name",
                    "Owner Name", "Owner RACF", "Owner Email", "Employee Name", "Employee RACF", "Employee Email", "Mod Date", "Mod By" };
            csvWriter.writeNext(header);
            // Write DTO data to CSV
            for (DistributionListCSVDTO distributionListCSVDTO : distributionEmployeeListDTOList) {
                getOrgUnitDetails(distributionListCSVDTO);
                String[] rowData = { Long.toString(distributionListCSVDTO.getId()), distributionListCSVDTO.getTitle(),
                        distributionListCSVDTO.getEmail(),
                        distributionListCSVDTO.getOrgUnitName(), distributionListCSVDTO.getOrgUnitShortName(),
                        distributionListCSVDTO.getOwnerName(),
                        distributionListCSVDTO.getOwnerRacf(), distributionListCSVDTO.getOwnerEmail(),
                        distributionListCSVDTO.getEmployeeName(),
                        distributionListCSVDTO.getEmployeeRacf(),
                        distributionListCSVDTO.getEmployeeEmail(), distributionListCSVDTO.getModDate(),
                        distributionListCSVDTO.getModUser()};
                csvWriter.writeNext(rowData);
            }

            return writer.toString();
        } catch (Exception e) {
            log.error("Failed to export distribution list : ", e);
            throw new BaseException(ErrorCodes.DISTRIBUTION_LIST_EXPORT_FAILED, "Failed to export distribution list");
        }
    }

    private void getOrgUnitDetails(DistributionListCSVDTO distributionListCSVDTO) {
        if(distributionListCSVDTO.getOrgUnitId() > 0) {
            OrgUnitDTO orgUnit = orgUnitService.getBusinessUnit(distributionListCSVDTO.getOrgUnitId());
            distributionListCSVDTO.setOrgUnitName(orgUnit.getName());
            distributionListCSVDTO.setOrgUnitShortName(orgUnit.getShortName());
        } else {
            distributionListCSVDTO.setOrgUnitName("-");
            distributionListCSVDTO.setOrgUnitShortName("-");
        }
    }
}
