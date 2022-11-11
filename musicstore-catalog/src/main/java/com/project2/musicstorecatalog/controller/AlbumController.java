package com.project2.musicstorecatalog.controller;

import com.project2.musicstorecatalog.model.Album;
import com.project2.musicstorecatalog.model.Artist;
import com.project2.musicstorecatalog.model.Label;
import com.project2.musicstorecatalog.repository.AlbumRepository;
import com.project2.musicstorecatalog.repository.ArtistRepository;
import com.project2.musicstorecatalog.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Autowired
    AlbumRepository repo;

    @Autowired
    LabelRepository labelRepo;

    @Autowired
    ArtistRepository artisRepo;


    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Album> getAllAlbum() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Album getAlbumById(@PathVariable Long id) {
        Optional<Album> returnVal = repo.findById(id);
        return returnVal.isPresent() ? returnVal.get() : null;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Album addAlbum(@RequestBody @Valid Album album) {
        if(isLabelAndArtistExist(album)){
            return repo.save(album);
        } else {
            throw new IllegalArgumentException("Label or artist no found.");
        }

    }

    @PutMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAlbum(@RequestBody @Valid Album album) {
        if(album.getId()!= null) {
            if(isLabelAndArtistExist(album)){
                repo.save(album);
            } else {
                throw new IllegalArgumentException("Label or artist no found. unable to update");
            }
        } else {
            throw new IllegalArgumentException("Album id not present, unable to update");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long id) {
        repo.deleteById(id);
    }

    private boolean isLabelAndArtistExist(Album album) {
        Optional<Label> label = labelRepo.findById(album.getLabelId());
        Optional<Artist> artist = artisRepo.findById(album.getArtistId());

        return label.isPresent() && artist.isPresent();
    }
}
