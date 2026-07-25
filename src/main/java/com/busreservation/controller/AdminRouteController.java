package com.busreservation.controller;

import com.busreservation.model.Route;
import com.busreservation.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
public class AdminRouteController {

    @Autowired
    private RouteRepository routeRepository;

    @GetMapping
    public List<Route> getAll() { return routeRepository.findAll(); }

    @PostMapping
    public Route add(@RequestBody Route route) { return routeRepository.save(route); }

    @DeleteMapping("/{routeId}")
    public void delete(@PathVariable Integer routeId) { routeRepository.deleteById(routeId); }
}
