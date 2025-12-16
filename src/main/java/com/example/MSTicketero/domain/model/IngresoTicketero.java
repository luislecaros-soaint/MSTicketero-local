package com.example.MSTicketero.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import org.springframework.data.domain.Persistable;

@Data
@Entity
@Table(name = "ingreso_ticketero")
public class IngresoTicketero implements Persistable<String> {

    @Id
    private String id;

    private String ticketNumber;
    private String queue;
    private String branchOffice;
    private String rut;
    private String phoneNumber;
    private Integer action;
    private Boolean isNewPhone;
    private String statusWhatsapp; 
    
    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}