package com.example.MSTicketero.application.input;

import com.example.MSTicketero.application.dto.GeneralResponseDTO;
import com.example.MSTicketero.application.dto.InteraccionRequestDTO;
import com.example.MSTicketero.application.dto.TicketRequestDTO;

public interface GestionarTicketUseCase {
    GeneralResponseDTO recibirTicket(TicketRequestDTO request);
    GeneralResponseDTO registrarInteraccion(InteraccionRequestDTO request);
}