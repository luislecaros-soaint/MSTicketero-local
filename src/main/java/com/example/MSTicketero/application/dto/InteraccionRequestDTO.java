package com.example.MSTicketero.application.dto;
import lombok.Data;
@Data
public class InteraccionRequestDTO {
    private String ticketNumber;
    private String rut;
    private String phoneNumber;
    private String fecha;
    private String tipo;
}