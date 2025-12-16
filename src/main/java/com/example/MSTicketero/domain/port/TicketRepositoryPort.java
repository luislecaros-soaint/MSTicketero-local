package com.example.MSTicketero.domain.port;

import com.example.MSTicketero.domain.model.IngresoTicketero;
import com.example.MSTicketero.domain.model.InteraccionWhatsapp;

public interface TicketRepositoryPort {
    IngresoTicketero saveTicket(IngresoTicketero ticket);
    IngresoTicketero findUltimoTicket(String ticketNumber, String rut);
    InteraccionWhatsapp saveInteraccion(InteraccionWhatsapp interaccion);
}