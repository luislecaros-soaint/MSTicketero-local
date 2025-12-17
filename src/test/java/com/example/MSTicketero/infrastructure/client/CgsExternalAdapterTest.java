package com.example.MSTicketero.infrastructure.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.context.ActiveProfiles;

import com.example.MSTicketero.application.dto.TicketRequestDTO;

@RestClientTest(CgsExternalAdapter.class)
@ActiveProfiles("test")
class CgsExternalAdapterTest {

    @Autowired
    private CgsExternalAdapter adapter;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void enviarNotificacion_Exito_RetornaTrue() {
        TicketRequestDTO request = new TicketRequestDTO();
        request.setTicketNumber("A100");
        server.expect(requestTo("http://localhost:8080/api/v1/ticket")) 
              .andExpect(method(HttpMethod.POST))
              .andExpect(header("X-API-KEY", "secret-key-test"))
              .andRespond(withSuccess());
        boolean resultado = adapter.enviarNotificacion(request);
        server.verify();
        assertTrue(resultado);
    }

    @Test
    void enviarNotificacion_FalloServidor_RetornaFalse() {
        TicketRequestDTO request = new TicketRequestDTO();
        request.setTicketNumber("A100");

        server.expect(requestTo("http://localhost:8080/api/v1/ticket"))
              .andExpect(method(HttpMethod.POST))
              .andRespond(withServerError());
        boolean resultado = adapter.enviarNotificacion(request);
        server.verify();
        assertFalse(resultado);
    }
}