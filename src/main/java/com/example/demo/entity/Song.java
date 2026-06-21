package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(name = "youtube_link", length = 500)
    private String youtubeLink;

    @Column(length = 1000)
    private String note;

    // --- Constructors ---
    public Song() {
    }

    public Song(Long id, String title, String artist, String youtubeLink, String note) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.youtubeLink = youtubeLink;
        this.note = note;
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public String getNote() {
        return note;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public void setNote(String note) {
        this.note = note;
    }
}