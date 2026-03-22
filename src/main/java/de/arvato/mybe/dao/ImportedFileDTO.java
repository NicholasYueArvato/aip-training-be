package de.arvato.mybe.dao;


import lombok.Data;

import java.util.Date;

@Data
public class ImportedFileDTO
{
    private long id;
    private String name;
    private String description;
    private String status;
    private Date defTime;
}
