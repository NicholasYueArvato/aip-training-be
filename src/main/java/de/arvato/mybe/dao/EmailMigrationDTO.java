package de.arvato.mybe.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EmailMigrationDTO
{
    private long id;
    private long lastEmailId;
    private Date defDate;
}
