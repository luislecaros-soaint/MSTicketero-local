package com.example.MSTicketero.domain.model;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "mensaje")
public class InteraccionWhatsapp {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private String id;

    private String ticketNumber;
    private String rut;
    private String fechaHora;
    private String tipo;
}