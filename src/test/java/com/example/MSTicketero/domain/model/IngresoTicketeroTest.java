package com.example.MSTicketero.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IngresoTicketeroTest {

    @Test
    void testPersistableLogic() {
        IngresoTicketero ticket = new IngresoTicketero();
        assertTrue(ticket.isNew(), "El ticket debería ser nuevo al instanciarse");
        ticket.markNotNew();
        assertFalse(ticket.isNew(), "El ticket no debería ser nuevo después de markNotNew");
    }

    @Test
    void testGettersAndSetters() {
        IngresoTicketero ticket = new IngresoTicketero();
        ticket.setId("123");
        ticket.setTicketNumber("A1");
        ticket.setAction(0);
        
        assertEquals("123", ticket.getId());
        assertEquals("A1", ticket.getTicketNumber());
        assertEquals(0, ticket.getAction());
    }
}