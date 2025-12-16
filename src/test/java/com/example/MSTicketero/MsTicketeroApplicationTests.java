package com.example.MSTicketero;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.MSTicketero.infrastructure.controller.TicketeroController;

@SpringBootTest
class MsTicketeroApplicationTests {

    @Autowired
    private TicketeroController controller;

    @Test
    void contextLoads() {
        assertNotNull(controller, "El controlador no debería ser nulo si el contexto cargó correctamente");
    }

}