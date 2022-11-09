package com.project2.musicstorecatalog.repository;

import com.project2.musicstorecatalog.model.Artist;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArtistRepositoryTest {

    @Autowired
    ArtistRepository artistRepository;

    private Artist artist1;

    private Artist artist2;

    @Before
    public void setUp() throws Exception {
        artistRepository.deleteAll();

        artist1 = new Artist("Rock Star", "@RockStar", "#RockStar");
        artist2 = new Artist("Pop Star", "@PopStar", "#PopStar");
    }
    @After
    public void tearDown() throws Exception{
        artistRepository.deleteAll();
    }

    @Test
    public void shouldCreateFindDeleteArtist() {
        Artist savedArtist = artistRepository.save(artist1);
        artistRepository.save(artist2);

        Optional<Artist> foundArtist = artistRepository.findById(savedArtist.getId());

        assertTrue(foundArtist.isPresent());
        assertEquals(foundArtist.get(), savedArtist);

        artistRepository.deleteById(artist1.getId());

        foundArtist = artistRepository.findById(artist1.getId());
        assertFalse(foundArtist.isPresent());

    }

    @Test
    public void shouldFindAllArtists() {
        Artist savedArtist1 = artistRepository.save(artist1);
        Artist savedArtist2 = artistRepository.save(artist2);
        Set<Artist> savedArtistList = new HashSet<>();

        savedArtistList.add(savedArtist1);
        savedArtistList.add(savedArtist2);

        Set<Artist> outputList = new HashSet<>(artistRepository.findAll());
        assertEquals(savedArtistList.size(), outputList.size());
        assertEquals(savedArtistList, outputList);

    }

    @Test
    public void shouldUpdateArtistById() {
        Artist savedArtist = artistRepository.save(artist1);

        savedArtist.setName("super nova");
        savedArtist.setInstagram("@SuperNova");
        savedArtist.setTwitter("#SuperNova");

        artistRepository.save(savedArtist);

        Optional<Artist> updatedArtist = artistRepository.findById(savedArtist.getId());
        assertEquals(updatedArtist.get(), savedArtist);
    }
}