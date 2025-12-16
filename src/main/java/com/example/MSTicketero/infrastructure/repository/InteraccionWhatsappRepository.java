package com.example.MSTicketero.infrastructure.repository;
import com.example.MSTicketero.domain.model.InteraccionWhatsapp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteraccionWhatsappRepository extends JpaRepository<InteraccionWhatsapp, String> {
}