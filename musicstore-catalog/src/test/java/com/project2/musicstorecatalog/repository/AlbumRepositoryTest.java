package com.project2.musicstorecatalog.repository;

import com.project2.musicstorecatalog.model.Album;
import com.project2.musicstorecatalog.model.Artist;
import com.project2.musicstorecatalog.model.Label;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlbumRepositoryTest {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    ArtistRepository artistRepository;

    private Album album1;

    private Album album2;

    private Label label1;

    private Artist artist1;

    @Before
    public void setUp() throws Exception {

        albumRepository.deleteAll();
        labelRepository.deleteAll();
        artistRepository.deleteAll();

        label1 = new Label("label1", "www.lable1.com");
        label1 = labelRepository.save(label1);

        artist1 = new Artist("artist1", "@artist1", "#artist1");
        artist1 = artistRepository.save(artist1);

        album1 = new Album();
        album1.setTitle("album1");
        album1.setArtistId(artist1.getId());
        album1.setLabelId(label1.getId());
        album1.setReleaseDate(LocalDate.of(2022,01, 01));
        album1.setListPrice(new BigDecimal("19.99"));

        album2 = new Album();
        album2.setTitle("album2");
        album2.setArtistId(artist1.getId());
        album2.setLabelId(label1.getId());
        album2.setReleaseDate(LocalDate.of(2000, 02, 20));
        album2.setListPrice(new BigDecimal("29.99"));

    }

    @After
    public void tearDown() throws Exception {
        albumRepository.deleteAll();
        labelRepository.deleteAll();
        artistRepository.deleteAll();
    }

    @Test
    public void shouldCreateFindDeleteAlbum() {
        Album savedAlbum = albumRepository.save(album1);

        Optional<Album> foundAlbum = albumRepository.findById(savedAlbum.getId());

        assertTrue(foundAlbum.isPresent());
        assertEquals(foundAlbum.get(), savedAlbum);

        albumRepository.deleteById(savedAlbum.getId());

        foundAlbum = albumRepository.findById(savedAlbum.getId());
        assertFalse(foundAlbum.isPresent());

    }

    @Test
    public void shouldFindAllAlbum() {
        Album savedAlbum1 = albumRepository.save(album1);
        Album savedAlbum2 = albumRepository.save(album2);
        Set<Album> albumSet = new HashSet<>();
        albumSet.add(savedAlbum1);
        albumSet.add(savedAlbum2);

        Set<Album> outputSet = new HashSet<>(albumRepository.findAll());

        assertEquals(albumSet.size(), outputSet.size());
        assertEquals(albumSet, outputSet);
    }

    @Test
    public void shouldUpdateAlbum() {
        Album savedAlbum = albumRepository.save(album1);

        savedAlbum.setTitle("new album 1");
        savedAlbum.setListPrice(new BigDecimal("59.99"));

        albumRepository.save(savedAlbum);

        Optional<Album> updatedAlbum = albumRepository.findById(savedAlbum.getId());
        assertEquals(savedAlbum, updatedAlbum.get());

    }
}