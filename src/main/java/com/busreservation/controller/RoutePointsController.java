package com.busreservation.controller;

import com.busreservation.model.BoardingPoint;
import com.busreservation.repository.BoardingPointRepository;
import com.busreservation.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/routes")
public class RoutePointsController {

    @Autowired
    private BoardingPointRepository boardingPointRepository;

    @Autowired
    private RouteRepository routeRepository;

    @GetMapping("/{routeId}/points")
    public List<BoardingPoint> getPoints(@PathVariable Integer routeId) {
        return boardingPointRepository.findByRoute_RouteId(routeId);
    }

    @GetMapping("/cities")
    public List<String> getCities() {
        Set<String> cities = new TreeSet<>(); // sorted, no duplicates
        cities.addAll(routeRepository.findDistinctSources());
        cities.addAll(routeRepository.findDistinctDestinations());
        return new ArrayList<>(cities);
    }
}