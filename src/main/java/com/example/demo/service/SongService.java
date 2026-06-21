package com.example.demo.service;

import com.example.demo.entity.Song;
import com.example.demo.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {

    private final SongRepository repo;

    public SongService(SongRepository repo) {
        this.repo = repo;
    }

    public List<Song> getAllSongs() {
        return repo.findAll();
    }

    public Song addSong(Song song) {
        return repo.save(song);
    }

    public void deleteSong(Long id) {
        repo.deleteById(id);
    }
}