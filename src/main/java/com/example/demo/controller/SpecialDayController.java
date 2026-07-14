package com.example.demo.controller;

import com.example.demo.entity.SpecialDay;
import com.example.demo.repository.SpecialDayRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/special-days")
public class SpecialDayController {

    private final SpecialDayRepository repo;

    public SpecialDayController(SpecialDayRepository repo) {
        this.repo = repo;
    }

    // GET all special days
    @GetMapping
    public List<SpecialDay> getAll() {
        return repo.findAll();
    }

    // POST add special day
    @PostMapping
    public SpecialDay add(@RequestBody SpecialDay day) {
        return repo.save(day);
    }

    // DELETE special day
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
