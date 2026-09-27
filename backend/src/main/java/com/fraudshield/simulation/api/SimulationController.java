package com.fraudshield.simulation.api;

import com.fraudshield.simulation.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/simulation")
@CrossOrigin(origins = "*") // Allow frontend to connect
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/stream")
    public SseEmitter streamTransactions() {
        return simulationService.subscribe();
    }

    @PostMapping("/start")
    public ResponseEntity<?> startSimulation(@RequestParam(defaultValue = "10") int tps) {
        try {
            simulationService.startSimulation(tps);
            return ResponseEntity.ok(Map.of("message", "Simulation started", "status", "RUNNING"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stopSimulation() {
        simulationService.stopSimulation();
        return ResponseEntity.ok(Map.of("message", "Simulation stopped", "status", "STOPPED"));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(Map.of(
            "status", simulationService.isRunning() ? "RUNNING" : "STOPPED"
        ));
    }
}
