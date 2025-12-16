package com.example.MSTicketero.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.MSTicketero.application.dto.TicketRequestDTO;
import com.example.MSTicketero.domain.port.NotificationPort;

@Component
public class CgsExternalAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(CgsExternalAdapter.class);
    private final RestClient restClient;
    
    @Value("${external.api.cgs.apikey}")
    private String apiKey;

    public CgsExternalAdapter(
            RestClient.Builder builder,
            @Value("${external.api.cgs.url}") String baseUrl) {
        
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public boolean enviarNotificacion(TicketRequestDTO ticketRequest) {
        try {
            log.info("Enviando ticket {} a API externa CGS...", ticketRequest.getTicketNumber());

            restClient.post()
                    .header("X-API-KEY", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ticketRequest)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Ticket {} enviado exitosamente a CGS.", ticketRequest.getTicketNumber());
            return true;

        } catch (Exception e) {
            log.error("Error al enviar ticket {} a CGS: {}", ticketRequest.getTicketNumber(), e.getMessage());
            return false;
        }
    }
}