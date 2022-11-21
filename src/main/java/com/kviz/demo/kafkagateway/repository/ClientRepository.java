package com.kviz.demo.kafkagateway.repository;

import com.kviz.demo.kafkagateway.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByTypeAndAccount(String type, String account);
    void deleteByTypeAndAccount(String type, String account);
}
