package com.example.demo.controller;

import com.example.demo.entity.PeriodEntry;
import com.example.demo.repository.PeriodRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/periods")
public class PeriodController {
    private final PeriodRepository repository;
    public PeriodController(PeriodRepository repository) { this.repository = repository; }

    @GetMapping
    public List<PeriodEntry> getAll() { return repository.findAll(); }

    @PostMapping
    public PeriodEntry add(@RequestBody PeriodEntry entry) { return repository.save(entry); }

    @PutMapping("/{id}")
    public PeriodEntry update(@PathVariable Long id, @RequestBody PeriodEntry updated) {
        return repository.findById(id).map(entry -> {
            entry.setStartDate(updated.getStartDate());
            entry.setEndDate(updated.getEndDate());
            return repository.save(entry);
        }).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}
