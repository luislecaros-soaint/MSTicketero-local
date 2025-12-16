package com.example.MSTicketero.infrastructure.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.MSTicketero.application.dto.GeneralResponseDTO;
import com.example.MSTicketero.application.dto.InteraccionRequestDTO;
import com.example.MSTicketero.application.dto.TicketRequestDTO;
import com.example.MSTicketero.application.input.GestionarTicketUseCase;

@WebMvcTest(TicketeroController.class)
class TicketeroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GestionarTicketUseCase useCase;

    @Test
    void recibirTicket_DeberiaRetornar200() throws Exception {
        when(useCase.recibirTicket(any(TicketRequestDTO.class)))
                .thenReturn(new GeneralResponseDTO("ok", 200));

        mockMvc.perform(post("/ticketero/ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ticketNumber\":\"A1\", \"rut\":\"1-9\", \"action\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    void actualizarInteraccion_DeberiaRetornar200() throws Exception {
        when(useCase.registrarInteraccion(any(InteraccionRequestDTO.class)))
                .thenReturn(new GeneralResponseDTO("ok", 200));

        mockMvc.perform(put("/whatsapp/interaccion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ticketNumber\":\"A1\", \"tipo\":\"LEIDO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}