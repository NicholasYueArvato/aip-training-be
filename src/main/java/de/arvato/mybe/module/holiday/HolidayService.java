package de.arvato.mybe.module.holiday;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import de.arvato.mybe.backend.exception.BaseException;
import de.arvato.mybe.backend.exception.EntityNotFoundException;
import de.arvato.mybe.backend.general.ErrorCodes;
import de.arvato.mybe.backend.util.transaction.TransactionalByException;
import de.arvato.mybe.dao.HolidayDAO;
import de.arvato.mybe.dao.HolidayDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayDAO holidayDAO;

    public List<HolidayDTO> getAllHolidays() {
        return holidayDAO.getAllHolidays();
    }

    public List<HolidayDTO> getHolidayByYear(int year) {
        return holidayDAO.getHolidayByYear(year);
    }

    public List<HolidayDTO> getHolidayById(long id) {
        return Collections.singletonList(holidayDAO.getHolidayById(id));
    }

    public long addHoliday(HolidayDTO holidayDTO) {
        if (checkExistingHolidayByDate(holidayDTO.getDate()) != null) {
            throw new BaseException(ErrorCodes.HOLIDAY_DUPLICATE, "Duplicate holiday date added");
        }
        return holidayDAO.addHoliday(holidayDTO);
    }

    @TransactionalByException
    public void addHolidayFile(InputStream inputStream) {
        List<HolidayDTO> holidaysFromCsv = holidayCsvToDTO(inputStream);

        holidaysFromCsv.forEach(holidayCsv -> {
            HolidayDTO holidayFound = checkExistingHolidayByDate(holidayCsv.getDate());
            if (holidayFound != null) {
                holidayDAO.updateHoliday(holidayFound.getId(), holidayCsv);
            } else {
                holidayDAO.addHoliday(holidayCsv);
            }
        });
    }

    public boolean updateHoliday(long id, HolidayDTO holidayDTO) {
        checkExistingHoliday(id);
        HolidayDTO existingHolidayDate = checkExistingHolidayByDate(holidayDTO.getDate());
        if (existingHolidayDate != null && existingHolidayDate.getId() != id) {
            throw new BaseException(ErrorCodes.HOLIDAY_DUPLICATE, "Duplicate holiday date added");
        }
        return holidayDAO.updateHoliday(id, holidayDTO) > 0;
    }

    public boolean deleteHoliday(long id) {
        checkExistingHoliday(id);
        return holidayDAO.deleteHoliday(id) > 0;
    }

    private void checkExistingHoliday(long id) {
        if (holidayDAO.getHolidayById(id) == null) {
            throw new EntityNotFoundException(ErrorCodes.HOLIDAY_NOT_FOUND, "holidayId", id);
        }
    }

    public HolidayDTO checkExistingHolidayByDate(LocalDate date) {
        return holidayDAO.getHolidayByDate(date);
    }

    private List<HolidayDTO> holidayCsvToDTO(InputStream inputStream) {
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("Public Holiday","name");
        columnMapping.put("Date","date");
        columnMapping.put("Remarks","description");

        HeaderColumnNameTranslateMappingStrategy<HolidayDTO> mappingStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
        mappingStrategy.setType(HolidayDTO.class);
        mappingStrategy.setColumnMapping(columnMapping);

        try (BOMInputStream bomInputStream = new BOMInputStream(inputStream, false))
        {
            return new CsvToBeanBuilder<HolidayDTO>(new InputStreamReader(bomInputStream, StandardCharsets.UTF_8))
                    .withType(HolidayDTO.class)
                    .withMappingStrategy(mappingStrategy)
                    .build()
                    .parse();
        }
        catch (IOException e)
        {
            throw new BaseException(ErrorCodes.HOLIDAY_FILE_INCOMPLETE, "Invalid Holiday File");
        }
    }
}

