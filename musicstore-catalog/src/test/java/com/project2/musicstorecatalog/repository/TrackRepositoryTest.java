package com.project2.musicstorecatalog.repository;

import com.project2.musicstorecatalog.model.Album;
import com.project2.musicstorecatalog.model.Artist;
import com.project2.musicstorecatalog.model.Label;
import com.project2.musicstorecatalog.model.Track;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TrackRepositoryTest {

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    ArtistRepository artistRepository;

    private Track track1;
    private Track track2;

    private Album album1;

    private Album album2;

    private Label label1;

    private Artist artist1;


    @Before
    public void setUp() throws Exception {
        trackRepository.deleteAll();
        albumRepository.deleteAll();
        labelRepository.deleteAll();
        artistRepository.deleteAll();

        //Arrange create 3 track objects for further test use;

        label1 = new Label("label1", "www.label1.com");
        label1 = labelRepository.save(label1);

        artist1 = new Artist("artist1", "@artist1", "#artist1");
        artist1 = artistRepository.save(artist1);

        album1 = new Album();
        album1.setTitle("album1");
        album1.setArtistId(artist1.getId());
        album1.setLabelId(label1.getId());
        album1.setReleaseDate(LocalDate.of(2022,01, 01));
        album1.setListPrice(new BigDecimal("19.99"));

        album1 = albumRepository.save(album1);

        album2 = new Album();
        album2.setTitle("album2");
        album2.setArtistId(artist1.getId());
        album2.setLabelId(label1.getId());
        album2.setReleaseDate(LocalDate.of(2000, 02, 20));
        album2.setListPrice(new BigDecimal("29.99"));

        album2= albumRepository.save(album2);

        //Arrange create 2 track objects for further test use;
        track1 = new Track();
        track1.setAlbumId(album1.getId());
        track1.setTitle("Thunderstruck");
        track1.setRunTime(10);

        track2 = new Track();
        track2.setAlbumId(album2.getId());
        track2.setTitle("Never Fade Away");
        track2.setRunTime(6);

    }

    @After
    public void tearDown() throws Exception {
        trackRepository.deleteAll();
        albumRepository.deleteAll();
        labelRepository.deleteAll();
        artistRepository.deleteAll();
    }

    @Test
    public void shouldCreateFindDeleteTrack(){

        Track savedTrack = trackRepository.save(track1);
        trackRepository.save(track2);
        Optional<Track> foundTrack = trackRepository.findById(savedTrack.getId());

        assertTrue(foundTrack.isPresent());
        assertEquals(savedTrack, foundTrack.get());

        trackRepository.deleteById(savedTrack.getId());

        foundTrack = trackRepository.findById(savedTrack.getId());
        assertFalse(foundTrack.isPresent());
    }

    @Test
    public void shouldFindAllTrack() {
        Track savedTrack1 = trackRepository.save(track1);
        Track savedTrack2 = trackRepository.save(track2);

        Set<Track> trackSet = new HashSet<>();

        trackSet.add(savedTrack1);
        trackSet.add(savedTrack2);

        Set<Track> outputSet = new HashSet<>(trackRepository.findAll());

        assertEquals(trackSet.size(), outputSet.size());
        assertEquals(trackSet, outputSet);

    }

    @Test
    public void shouldUpdateTrackById() {
        Track savedTrack = trackRepository.save(track1);

        savedTrack.setRunTime(100);
        savedTrack.setTitle("Hard As Rock");
        savedTrack.setAlbumId(album2.getId());

        trackRepository.save(savedTrack);

        Optional<Track> updatedTrack = trackRepository.findById(savedTrack.getId());
        assertEquals(savedTrack, updatedTrack.get());
    }

}