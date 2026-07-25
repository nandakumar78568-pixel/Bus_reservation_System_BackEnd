package com.busreservation.controller;

import com.busreservation.model.Schedule;
import com.busreservation.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/search")
    public List<Schedule> search(@RequestParam(required = false) String source,
                                   @RequestParam(required = false) String destination) {
        if (source != null && destination != null) {
            return scheduleRepository.findByRoute_SourceAndRoute_Destination(source, destination);
        }
        return scheduleRepository.findAll();
    }
}
