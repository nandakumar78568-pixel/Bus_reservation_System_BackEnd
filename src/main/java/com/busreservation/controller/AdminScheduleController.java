package com.busreservation.controller;

import com.busreservation.model.Schedule;
import com.busreservation.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/schedules")
public class AdminScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping
    public List<Schedule> getAll() { return scheduleRepository.findAll(); }

    @PostMapping
    public Schedule add(@RequestBody Schedule schedule) { return scheduleRepository.save(schedule); }

    @DeleteMapping("/{scheduleId}")
    public void delete(@PathVariable Integer scheduleId) { scheduleRepository.deleteById(scheduleId); }
}
