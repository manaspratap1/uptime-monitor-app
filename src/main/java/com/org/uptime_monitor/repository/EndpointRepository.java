package com.org.uptime_monitor.repository;

import com.org.uptime_monitor.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndpointRepository extends JpaRepository<Endpoint, Long> {

    boolean existsByUrl(String url);
    List<Endpoint> findByAlertEmailIgnoreCase(String alertEmail);
}
