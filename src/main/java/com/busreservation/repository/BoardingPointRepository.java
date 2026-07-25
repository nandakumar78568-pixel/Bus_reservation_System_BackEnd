package com.busreservation.repository;

import com.busreservation.model.BoardingPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardingPointRepository extends JpaRepository<BoardingPoint, Integer> {
    List<BoardingPoint> findByRoute_RouteId(Integer routeId);
}
