package com.example.MSTicketero.application.interactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MSTicketero.application.dto.GeneralResponseDTO;
import com.example.MSTicketero.application.dto.InteraccionRequestDTO;
import com.example.MSTicketero.application.dto.TicketRequestDTO;
import com.example.MSTicketero.application.input.GestionarTicketUseCase;
import com.example.MSTicketero.domain.model.IngresoTicketero;
import com.example.MSTicketero.domain.model.InteraccionWhatsapp;
import com.example.MSTicketero.domain.port.NotificationPort;
import com.example.MSTicketero.domain.port.TicketRepositoryPort;

@Service
public class TicketInteractor implements GestionarTicketUseCase {

    private static final Logger log = LoggerFactory.getLogger(TicketInteractor.class);
    private final TicketRepositoryPort repositoryPort;
    private final NotificationPort notificationPort;

    public TicketInteractor(TicketRepositoryPort repositoryPort, NotificationPort notificationPort) {
        this.repositoryPort = repositoryPort;
        this.notificationPort = notificationPort;
    }

    private String generarIdOrdenable() {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
                
        String randomPart = java.util.UUID.randomUUID().toString().substring(0, 8);
        
        return timestamp + "-" + randomPart;
    }

    @Override
    @Transactional
    public GeneralResponseDTO recibirTicket(TicketRequestDTO request) {
        
        if (request.getAction() == null) {
             return new GeneralResponseDTO("error: action no recibido", 400);
        }
        
        if (request.getAction() < 0 || request.getAction() > 2) {
            log.warn("Ticket rechazado por action inválido: {}", request.getAction());
            return new GeneralResponseDTO("error: action es invalido", 400);
        }

        if (request.getAction() == 0 && request.getAvgTime() == null) {
            return new GeneralResponseDTO("error: avgTime no recibido para creación de ticket", 400);
        }

        if (request.getTicketNumber() == null) {
            return new GeneralResponseDTO("error: ticketNumber no recibido", 400);
        }

        if (request.getQueue() == null) {
            return new GeneralResponseDTO("error: queue no recibido", 400);
        }
        if (request.getBranchOffice() == null) {
             return new GeneralResponseDTO("error: branchOffice no recibido", 400);
        }
        
        if (request.getRut() == null) {
             return new GeneralResponseDTO("error: rut no recibido", 400);
        }
        
        if (request.getPhoneNumber() == null) {
             return new GeneralResponseDTO("error: phoneNumber no recibido", 400);
        }

        IngresoTicketero existente = repositoryPort.findUltimoTicket(request.getTicketNumber(), request.getRut());
        if (existente != null && existente.getAction().equals(request.getAction()) && "PENDIENTE".equals(existente.getStatusWhatsapp())) {
            log.info("Ticket duplicado detectado (mismo ticket y acción pendiente): {}", request.getTicketNumber());
            return new GeneralResponseDTO("ok (duplicado)", 200);
        }

        IngresoTicketero entidad = new IngresoTicketero();
        entidad.setId(generarIdOrdenable());
        entidad.setTicketNumber(request.getTicketNumber()); 
        entidad.setQueue(request.getQueue());
        entidad.setBranchOffice(request.getBranchOffice());
        entidad.setRut(request.getRut());
        entidad.setPhoneNumber(request.getPhoneNumber()); 
        entidad.setAction(request.getAction());
        entidad.setIsNewPhone(request.getIsNewPhone());
        entidad.setStatusWhatsapp("PENDIENTE");

        IngresoTicketero ticketGuardado = repositoryPort.saveTicket(entidad);
        log.info("Ticket guardado localmente: {}", request.getTicketNumber());

        boolean enviado = notificationPort.enviarNotificacion(request);

        if (enviado) {
            ticketGuardado.setStatusWhatsapp("ENVIADO");
        } else {
            ticketGuardado.setStatusWhatsapp("ERROR_ENVIO");
        }
        repositoryPort.saveTicket(ticketGuardado);

        return new GeneralResponseDTO("ok", 200);
    }

    @Override
    public GeneralResponseDTO registrarInteraccion(InteraccionRequestDTO request) {
        if (request.getTicketNumber() == null || request.getRut() == null) {
            log.warn("Interacción rechazada por falta de identificadores");
            return new GeneralResponseDTO("error: datos faltantes", 400);
        }

        IngresoTicketero ticket = repositoryPort.findUltimoTicket(request.getTicketNumber(), request.getRut());

        if (ticket == null) {
            log.error("Intento de interacción para ticket inexistente: {} - {}", request.getTicketNumber(), request.getRut());
            return new GeneralResponseDTO("error: ticket no encontrado", 404);
        }


        if (request.getTipo().equals(ticket.getStatusWhatsapp())) {
            log.info("Interacción ignorada: El ticket {} ya se encuentra en estado {}", 
                    request.getTicketNumber(), request.getTipo());
            return new GeneralResponseDTO("ok (sin cambios)", 200);
        }

        if ("ERROR_ENVIO".equals(ticket.getStatusWhatsapp())) {
            log.warn("Intento de interacción inválido: El ticket {} falló en el envío inicial (Status: ERROR_ENVIO)", 
                    request.getTicketNumber());
            return new GeneralResponseDTO("error: ticket con envío fallido", 409);
        }

        if ("FINALIZADO".equals(ticket.getStatusWhatsapp())) {
            log.warn("Intento de modificar ticket ya finalizado: {}", request.getTicketNumber());
            return new GeneralResponseDTO("ok (ignorado por estado final)", 200);
        }

        InteraccionWhatsapp mensaje = new InteraccionWhatsapp();
        mensaje.setTicketNumber(request.getTicketNumber());
        mensaje.setRut(request.getRut());
        mensaje.setFechaHora(request.getFecha());
        mensaje.setTipo(request.getTipo()); 
        repositoryPort.saveInteraccion(mensaje);

        ticket.setStatusWhatsapp(request.getTipo());
        repositoryPort.saveTicket(ticket);
        
        log.info("Interacción registrada y estado actualizado a '{}' para ticket {}", request.getTipo(), request.getTicketNumber());

        return new GeneralResponseDTO("ok", 200);
    }
}