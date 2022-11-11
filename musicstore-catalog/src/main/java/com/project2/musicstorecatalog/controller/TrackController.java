package com.project2.musicstorecatalog.controller;

import com.project2.musicstorecatalog.model.Album;
import com.project2.musicstorecatalog.model.Track;
import com.project2.musicstorecatalog.repository.AlbumRepository;
import com.project2.musicstorecatalog.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/track")
public class TrackController {

    @Autowired
    TrackRepository repo;

    @Autowired
    AlbumRepository albumRepo;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Track> getAllTrack() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Track getTrackById(@PathVariable Long id) {
        Optional<Track> returnVal = repo.findById(id);
        return returnVal.isPresent() ? returnVal.get() : null;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Track addTrack(@RequestBody @Valid Track track) {
        Optional<Album> album = albumRepo.findById(track.getAlbumId());
        if(album.isPresent()){
            return repo.save(track);
        } else {
            throw new IllegalArgumentException("Album id is not exist, unable to create.");
        }

    }

    @PutMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTrack(@RequestBody @Valid Track track) {
        Optional<Album> album = albumRepo.findById(track.getAlbumId());
        if (track.getId() != null) {
            if (album.isPresent()) {
                repo.save(track);
            } else {
                throw new IllegalArgumentException("Album id is not exist, unable to update.");
            }
        } else {
            throw new IllegalArgumentException("Track id is not present, unable to update");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrack(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
