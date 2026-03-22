package de.arvato.mybe.module.holiday;

import aep.core.starter.security.methodsecurity.HasRight;
import de.arvato.mybe.backend.general.ErrorCodes;
import de.arvato.mybe.backend.rest.RemoteResponse;
import de.arvato.mybe.dao.HolidayDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/holiday")
@RequiredArgsConstructor
@Slf4j
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping
    @Operation(
            summary = "Get holiday by year",
            description = "Get holiday"
    )
    @HasRight("central.holiday.view")
    public RemoteResponse getHolidayByYear(@RequestParam("year") int year)
    {
        return RemoteResponse.fillResponseSuccess(holidayService.getHolidayByYear(year));
    }

    @GetMapping(path = "/{id}")
    @Operation(
            summary = "Get holiday by id",
            description = "Get holiday by id"
    )
    @HasRight("central.holiday.view")
    public RemoteResponse getHolidayById(@PathVariable long id){
        return RemoteResponse.fillResponseSuccess(holidayService.getHolidayById(id));
    }

    @PostMapping
    @Operation(
            summary = "Add holiday",
            description = "Add holiday"
    )
    @HasRight("central.holiday.edit")
    public RemoteResponse addHoliday(@Valid @RequestBody HolidayDTO holidayDTO)
    {
        return RemoteResponse.fillResponseSuccess(holidayService.addHoliday(holidayDTO) > 0);
    }

    @PostMapping(path = "/import")
    @Operation(
            summary = "Add holiday file",
            description = "Add holiday file"
    )
    @HasRight("central.holiday.edit")
    public RemoteResponse addHolidayFile(@RequestParam("file") MultipartFile multipartFile)
    {
        try
        {
            holidayService.addHolidayFile(multipartFile.getInputStream());
            return RemoteResponse.fillResponseSuccess();
        }
        catch (Exception ex)
        {
            log.error("Error in importing holiday data, {}", ex.getMessage(), ex);
            return RemoteResponse.fillResponseFailed(ErrorCodes.GENERAL_EXCEPTION, ex.getMessage());
        }
    }

    @PutMapping(path = "/{id}")
    @Operation(
            summary = "Edit holiday",
            description = "Edit holiday"
    )
    @HasRight("central.holiday.edit")
    public RemoteResponse updateHoliday(@Valid @RequestBody HolidayDTO holidayDTO,
                                        @PathVariable long id)
    {
        return RemoteResponse.fillResponseSuccess(holidayService.updateHoliday(id, holidayDTO));
    }

    @DeleteMapping(path = "/{id}")
    @Operation(
            summary = "Delete holiday",
            description = "Delete holiday"
    )
    @HasRight("central.holiday.edit")
    public RemoteResponse deleteHoliday(@PathVariable long id)
    {
        return RemoteResponse.fillResponseSuccess(holidayService.deleteHoliday(id));
    }

}
