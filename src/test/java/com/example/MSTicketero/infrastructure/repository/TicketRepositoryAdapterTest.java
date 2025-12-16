package com.example.MSTicketero.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.MSTicketero.domain.model.IngresoTicketero;
import com.example.MSTicketero.domain.model.InteraccionWhatsapp;

@ExtendWith(MockitoExtension.class)
class TicketRepositoryAdapterTest {

    @Mock
    private IngresoTicketeroRepository jpaTicketRepo;

    @Mock
    private InteraccionWhatsappRepository jpaInteraccionRepo;

    @InjectMocks
    private TicketRepositoryAdapter adapter;

    @Test
    void saveTicket_DelegaAlRepoJPA() {
        IngresoTicketero ticket = new IngresoTicketero();
        when(jpaTicketRepo.saveAndFlush(ticket)).thenReturn(ticket);

        IngresoTicketero result = adapter.saveTicket(ticket);

        assertEquals(ticket, result);
        verify(jpaTicketRepo).saveAndFlush(ticket);
    }

    @Test
    void findUltimoTicket_LlamaMetodoCorrecto() {
        String num = "A1";
        String rut = "1-9";
        IngresoTicketero ticket = new IngresoTicketero();
        
        when(jpaTicketRepo.findTopByTicketNumberAndRutOrderByIdDesc(num, rut)).thenReturn(ticket);

        IngresoTicketero result = adapter.findUltimoTicket(num, rut);

        assertEquals(ticket, result);
        verify(jpaTicketRepo).findTopByTicketNumberAndRutOrderByIdDesc(num, rut);
    }

    @Test
    void saveInteraccion_DelegaAlRepoJPA() {
        InteraccionWhatsapp interaccion = new InteraccionWhatsapp();
        when(jpaInteraccionRepo.save(interaccion)).thenReturn(interaccion);

        adapter.saveInteraccion(interaccion);

        verify(jpaInteraccionRepo).save(interaccion);
    }
}