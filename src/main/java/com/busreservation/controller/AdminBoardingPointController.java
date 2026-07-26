package com.busreservation.controller;

import com.busreservation.model.BoardingPoint;
import com.busreservation.model.Route;
import com.busreservation.repository.BoardingPointRepository;
import com.busreservation.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/boarding-points")
public class AdminBoardingPointController {

    @Autowired
    private BoardingPointRepository boardingPointRepository;

    @Autowired
    private RouteRepository routeRepository;

    @GetMapping("/route/{routeId}")
    public List<BoardingPoint> getByRoute(@PathVariable Integer routeId) {
        return boardingPointRepository.findByRoute_RouteId(routeId);
    }

    @PostMapping
    public BoardingPoint add(@RequestBody BoardingPoint point) {
        Integer routeId = point.getRoute() != null ? point.getRoute().getRouteId() : null;
        if (routeId == null) {
            throw new RuntimeException("routeId is required");
        }
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        point.setRoute(route);
        return boardingPointRepository.save(point);
    }

    @DeleteMapping("/{pointId}")
    public void delete(@PathVariable Integer pointId) {
        boardingPointRepository.deleteById(pointId);
    }
}