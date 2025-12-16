package com.example.MSTicketero.application.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data @AllArgsConstructor
public class GeneralResponseDTO {
    private String message;
    private Integer status;
}