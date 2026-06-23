package com.example.demo.controller;

import com.example.demo.entity.Song;
import com.example.demo.service.SongService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")

public class SongController {

    private final SongService service;

    public SongController(SongService service) {
        this.service = service;
    }

    @GetMapping
    public List<Song> getSongs() {
        return service.getAllSongs();
    }

    @PostMapping
    public Song addSong(@RequestBody Song song) {
        return service.addSong(song);
    }

    @DeleteMapping("/{id}")
    public void deleteSong(@PathVariable Long id) {
        service.deleteSong(id);
    }
}