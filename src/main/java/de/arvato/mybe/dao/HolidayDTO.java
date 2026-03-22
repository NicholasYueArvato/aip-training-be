package de.arvato.mybe.dao;

import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDTO {
    private long id;
    @NotBlank(message = "name cannot be empty")
    private String name;
    @CsvDate("dd/MM/yyyy")
    @NotBlank(message = "date cannot be empty")
    private LocalDate date;
    private String description;
}
