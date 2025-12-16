package com.example.MSTicketero.infrastructure.repository;
import com.example.MSTicketero.domain.model.IngresoTicketero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngresoTicketeroRepository extends JpaRepository<IngresoTicketero, String> {
    IngresoTicketero findTopByTicketNumberAndRutOrderByIdDesc(String ticketNumber, String rut);
}