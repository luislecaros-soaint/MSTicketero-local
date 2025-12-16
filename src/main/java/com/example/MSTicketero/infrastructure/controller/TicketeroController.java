package com.example.MSTicketero.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.MSTicketero.application.dto.GeneralResponseDTO;
import com.example.MSTicketero.application.dto.InteraccionRequestDTO;
import com.example.MSTicketero.application.dto.TicketRequestDTO;
import com.example.MSTicketero.application.input.GestionarTicketUseCase;

@RestController
public class TicketeroController {

    private final GestionarTicketUseCase gestionarTicketUseCase;

    public TicketeroController(GestionarTicketUseCase gestionarTicketUseCase) {
        this.gestionarTicketUseCase = gestionarTicketUseCase;
    }
    @PostMapping("/ticketero/ticket")
    public ResponseEntity<GeneralResponseDTO> recibirTicket(@RequestBody TicketRequestDTO request) {
        GeneralResponseDTO response = gestionarTicketUseCase.recibirTicket(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/whatsapp/interaccion")
    public ResponseEntity<GeneralResponseDTO> registrarInteraccion(@RequestBody InteraccionRequestDTO request) {
        GeneralResponseDTO response = gestionarTicketUseCase.registrarInteraccion(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}