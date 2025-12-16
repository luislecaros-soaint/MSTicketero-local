package com.example.MSTicketero.application.dto;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class TicketRequestDTO {
    private String ticketNumber;
    private String queue;
    private String branchOffice;
    @JsonProperty("RUT")
    @JsonAlias({"rut"})
    private String rut;
    private String phoneNumber;
    private Integer action;
    private Boolean isNewPhone;
    private Long avgTime;
    private String lastCalled;
}