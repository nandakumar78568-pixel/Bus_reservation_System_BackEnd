package com.busreservation.controller;

import com.busreservation.model.Bus;
import com.busreservation.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/buses")
public class AdminController {

    @Autowired
    private BusRepository busRepository;

    @GetMapping
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    @PostMapping
    public Bus addBus(@RequestBody Bus bus) {
        return busRepository.save(bus);
    }

    @DeleteMapping("/{busId}")
    public void deleteBus(@PathVariable Integer busId) {
        busRepository.deleteById(busId);
    }
}
