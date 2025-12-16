package com.example.MSTicketero.domain.port;

import com.example.MSTicketero.application.dto.TicketRequestDTO;

public interface NotificationPort {
    boolean enviarNotificacion(TicketRequestDTO ticketRequest);
}