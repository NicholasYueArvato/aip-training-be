package de.arvato.mybe.dao;

import java.util.List;

import lombok.Data;

@Data
public class EmailQueueDTO
{
    private long id;
    private String subject;
    private String sender;
    private String recipient;
    private String contents;
    private String status;
    private String type;
    private List<String> cc;
}
