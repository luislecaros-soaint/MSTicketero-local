package com.example.MSTicketero.infrastructure.repository;

import com.example.MSTicketero.domain.model.IngresoTicketero;
import com.example.MSTicketero.domain.model.InteraccionWhatsapp;
import com.example.MSTicketero.domain.port.TicketRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class TicketRepositoryAdapter implements TicketRepositoryPort {

    private final IngresoTicketeroRepository jpaTicketRepo;
    private final InteraccionWhatsappRepository jpaInteraccionRepo;

    public TicketRepositoryAdapter(IngresoTicketeroRepository t, InteraccionWhatsappRepository i) {
        this.jpaTicketRepo = t;
        this.jpaInteraccionRepo = i;
    }

    @Override
    public IngresoTicketero saveTicket(IngresoTicketero ticket) {
        return jpaTicketRepo.saveAndFlush(ticket);
    }

    @Override
    public IngresoTicketero findUltimoTicket(String ticketNumber, String rut) {
        return jpaTicketRepo.findTopByTicketNumberAndRutOrderByIdDesc(ticketNumber, rut);
    }

    @Override
    public InteraccionWhatsapp saveInteraccion(InteraccionWhatsapp interaccion) {
        return jpaInteraccionRepo.save(interaccion);
    }
}