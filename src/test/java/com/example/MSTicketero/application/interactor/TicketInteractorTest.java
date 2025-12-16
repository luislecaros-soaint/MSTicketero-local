package com.example.MSTicketero.application.interactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.MSTicketero.application.dto.GeneralResponseDTO;
import com.example.MSTicketero.application.dto.InteraccionRequestDTO;
import com.example.MSTicketero.application.dto.TicketRequestDTO;
import com.example.MSTicketero.domain.model.IngresoTicketero;
import com.example.MSTicketero.domain.model.InteraccionWhatsapp;
import com.example.MSTicketero.domain.port.NotificationPort;
import com.example.MSTicketero.domain.port.TicketRepositoryPort;

@ExtendWith(MockitoExtension.class)
class TicketInteractorTest {

    @Mock
    private TicketRepositoryPort repositoryPort;

    @Mock
    private NotificationPort notificationPort;
    @InjectMocks
    private TicketInteractor interactor;

    private TicketRequestDTO validTicketRequest;
    private InteraccionRequestDTO validInteraccionRequest;

    @BeforeEach
    void setUp() {
        validTicketRequest = new TicketRequestDTO();
        validTicketRequest.setTicketNumber("A100");
        validTicketRequest.setQueue("Ventas");
        validTicketRequest.setBranchOffice("Centro");
        validTicketRequest.setRut("12345678-9");
        validTicketRequest.setPhoneNumber("999999999");
        validTicketRequest.setAction(0);
        validTicketRequest.setAvgTime(300L);

        validInteraccionRequest = new InteraccionRequestDTO();
        validInteraccionRequest.setTicketNumber("A100");
        validInteraccionRequest.setRut("12345678-9");
        validInteraccionRequest.setTipo("LEIDO");
        validInteraccionRequest.setFecha("2025-01-01T10:00:00");
    }


    @Test
    void recibirTicket_ActionNulo_Retorna400() {
        validTicketRequest.setAction(null);
        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);
        assertEquals(400, response.getStatus());
        assertEquals("error: action no recibido", response.getMessage());
    }

    @Test
    void recibirTicket_ActionFueraDeRango_Retorna400() {
        validTicketRequest.setAction(3); 
        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);
        assertEquals(400, response.getStatus());
        assertEquals("error: action es invalido", response.getMessage());
    }

    @Test
    void recibirTicket_Action0SinAvgTime_Retorna400() {
        validTicketRequest.setAction(0);
        validTicketRequest.setAvgTime(null);
        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);
        assertEquals(400, response.getStatus());
        assertTrue(response.getMessage().contains("avgTime"));
    }

    @Test
    void recibirTicket_DatosFaltantes_TicketNumber_Retorna400() {
        validTicketRequest.setTicketNumber(null);
        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);
        assertEquals(400, response.getStatus());
        assertTrue(response.getMessage().contains("ticketNumber"));
    }

    @Test
    void recibirTicket_DatosFaltantes_Rut_Retorna400() {
        validTicketRequest.setRut(null);
        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);
        assertEquals(400, response.getStatus());
        assertTrue(response.getMessage().contains("rut"));
    }

    @Test
    void recibirTicket_DuplicadoMismaAccionYPendiente_Retorna200SinGuardar() {
        IngresoTicketero existente = new IngresoTicketero();
        existente.setAction(0);
        existente.setStatusWhatsapp("PENDIENTE");

        when(repositoryPort.findUltimoTicket(anyString(), anyString())).thenReturn(existente);

        
        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);

        
        assertEquals(200, response.getStatus());
        assertTrue(response.getMessage().contains("duplicado"));
        verify(repositoryPort, never()).saveTicket(any());
        verify(notificationPort, never()).enviarNotificacion(any()); 
    }

    @Test
    void recibirTicket_NuevoExitoso_FlujoCompleto() {
        
        when(repositoryPort.findUltimoTicket(anyString(), anyString())).thenReturn(null);
        when(notificationPort.enviarNotificacion(any())).thenReturn(true); 
        
        IngresoTicketero mockGuardado = new IngresoTicketero();
        mockGuardado.setStatusWhatsapp("PENDIENTE");
        when(repositoryPort.saveTicket(any(IngresoTicketero.class))).thenReturn(mockGuardado);

        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);

        assertEquals(200, response.getStatus());
        
        ArgumentCaptor<IngresoTicketero> captor = ArgumentCaptor.forClass(IngresoTicketero.class);
        verify(repositoryPort, times(2)).saveTicket(captor.capture());
     
        assertEquals("ENVIADO", captor.getValue().getStatusWhatsapp());
    }

    @Test
    void recibirTicket_FalloAPIExterna_GuardaErrorEnvio() {
       
        when(repositoryPort.findUltimoTicket(anyString(), anyString())).thenReturn(null);
        when(notificationPort.enviarNotificacion(any())).thenReturn(false); 

        IngresoTicketero mockGuardado = new IngresoTicketero();
        mockGuardado.setStatusWhatsapp("PENDIENTE");
        when(repositoryPort.saveTicket(any(IngresoTicketero.class))).thenReturn(mockGuardado);

        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);

        assertEquals(200, response.getStatus());
        
        ArgumentCaptor<IngresoTicketero> captor = ArgumentCaptor.forClass(IngresoTicketero.class);
        verify(repositoryPort, times(2)).saveTicket(captor.capture());
        
        assertEquals("ERROR_ENVIO", captor.getValue().getStatusWhatsapp());
    }


    @Test
    void registrarInteraccion_FaltaIdentificadores_Retorna400() {
        validInteraccionRequest.setTicketNumber(null);
        GeneralResponseDTO response = interactor.registrarInteraccion(validInteraccionRequest);
        assertEquals(400, response.getStatus());
    }

    @Test
    void registrarInteraccion_TicketNoEncontrado_Retorna404() {
        when(repositoryPort.findUltimoTicket(anyString(), anyString())).thenReturn(null);
        GeneralResponseDTO response = interactor.registrarInteraccion(validInteraccionRequest);
        assertEquals(404, response.getStatus());
    }

    @Test
    void registrarInteraccion_MismoEstado_Ignora() {
        IngresoTicketero ticket = new IngresoTicketero();
        ticket.setStatusWhatsapp("LEIDO"); 

        when(repositoryPort.findUltimoTicket(anyString(), anyString())).thenReturn(ticket);

        GeneralResponseDTO response = interactor.registrarInteraccion(validInteraccionRequest);

        assertEquals(200, response.getStatus());
        assertTrue(response.getMessage().contains("sin cambios"));
        verify(repositoryPort, never()).saveInteraccion(any());
    }

    @Test
    void registrarInteraccion_EstadoErrorEnvio_Retorna409() {
        IngresoTicketero ticket = new IngresoTicketero();
        ticket.setStatusWhatsapp("ERROR_ENVIO");

        when(repositoryPort.findUltimoTicket(anyString(), anyString())).thenReturn(ticket);

        GeneralResponseDTO response = interactor.registrarInteraccion(validInteraccionRequest);

        assertEquals(409, response.getStatus()); 
        verify(repositoryPort, never()).saveInteraccion(any());
    }

    @Test
    void registrarInteraccion_EstadoFinalizado_Ignora() {
        IngresoTicketero ticket = new IngresoTicketero();
        ticket.setStatusWhatsapp("FINALIZADO");

        when(repositoryPort.findUltimoTicket(anyString(), anyString())).thenReturn(ticket);

        GeneralResponseDTO response = interactor.registrarInteraccion(validInteraccionRequest);

        assertEquals(200, response.getStatus());
        assertTrue(response.getMessage().contains("ignorado"));
        verify(repositoryPort, never()).saveInteraccion(any());
    }

    @Test
    void registrarInteraccion_Exito_GuardaTrazaYActualizaEstado() {
        IngresoTicketero ticket = new IngresoTicketero();
        ticket.setStatusWhatsapp("ENVIADO");

        when(repositoryPort.findUltimoTicket(anyString(), anyString())).thenReturn(ticket);

        GeneralResponseDTO response = interactor.registrarInteraccion(validInteraccionRequest);

        assertEquals(200, response.getStatus());
        
        verify(repositoryPort).saveInteraccion(any(InteraccionWhatsapp.class));
        
        ArgumentCaptor<IngresoTicketero> captor = ArgumentCaptor.forClass(IngresoTicketero.class);
        verify(repositoryPort).saveTicket(captor.capture());
        assertEquals("LEIDO", captor.getValue().getStatusWhatsapp());
    }

    @Test
    void recibirTicket_FaltaQueue_Retorna400() {
        validTicketRequest.setQueue(null);
        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);
        assertEquals(400, response.getStatus());
        assertTrue(response.getMessage().contains("queue"));
        verify(repositoryPort, never()).saveTicket(any());
    }

    @Test
    void recibirTicket_FaltaBranchOffice_Retorna400() {
        validTicketRequest.setBranchOffice(null);
        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);
        assertEquals(400, response.getStatus());
        assertTrue(response.getMessage().contains("branchOffice"));
        verify(repositoryPort, never()).saveTicket(any());
    }

    @Test
    void recibirTicket_FaltaPhoneNumber_Retorna400() {
        validTicketRequest.setPhoneNumber(null);
        GeneralResponseDTO response = interactor.recibirTicket(validTicketRequest);
        assertEquals(400, response.getStatus());
        assertTrue(response.getMessage().contains("phoneNumber"));
        verify(repositoryPort, never()).saveTicket(any());
    }
}