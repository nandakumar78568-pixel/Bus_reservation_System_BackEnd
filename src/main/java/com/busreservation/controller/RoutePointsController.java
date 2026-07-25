package com.busreservation.controller;

import com.busreservation.model.BoardingPoint;
import com.busreservation.repository.BoardingPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RoutePointsController {

    @Autowired
    private BoardingPointRepository boardingPointRepository;

    @GetMapping("/{routeId}/points")
    public List<BoardingPoint> getPoints(@PathVariable Integer routeId) {
        return boardingPointRepository.findByRoute_RouteId(routeId);
    }
}
