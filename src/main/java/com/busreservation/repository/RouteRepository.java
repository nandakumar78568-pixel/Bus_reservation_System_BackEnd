package com.busreservation.repository;

import com.busreservation.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {

    @Query("SELECT DISTINCT r.source FROM Route r")
    List<String> findDistinctSources();

    @Query("SELECT DISTINCT r.destination FROM Route r")
    List<String> findDistinctDestinations();

    Optional<Route> findBySourceAndDestination(String source, String destination);
}