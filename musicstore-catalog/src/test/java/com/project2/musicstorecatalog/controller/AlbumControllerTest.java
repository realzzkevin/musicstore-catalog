package com.project2.musicstorecatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project2.musicstorecatalog.model.Album;
import com.project2.musicstorecatalog.model.Artist;
import com.project2.musicstorecatalog.model.Label;
import com.project2.musicstorecatalog.repository.AlbumRepository;
import com.project2.musicstorecatalog.repository.ArtistRepository;
import com.project2.musicstorecatalog.repository.LabelRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AlbumController.class)
public class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AlbumRepository albumRepo;

    @MockBean
    private LabelRepository labelRepo;

    @MockBean
    private ArtistRepository artistRepo;

    private Album inputAlbum1;
    private Album inputAlbum2;
    private Album outputAlbum1;
    private Album outputAlbum2;

    private Label label;

    private Artist artist;

    private List<Album> albumList;

    @Before
    public void setUp() {

        mapper = new ObjectMapper();

        inputAlbum1 = new Album();
        inputAlbum1.setTitle("album1");
        inputAlbum1.setArtistId(1L);
        inputAlbum1.setLabelId(2L);
        inputAlbum1.setReleaseDate(LocalDate.of(2022,01, 01));
        inputAlbum1.setListPrice(new BigDecimal("19.99"));

        inputAlbum2 = new Album();
        inputAlbum2.setTitle("album2");
        inputAlbum2.setArtistId(2L);
        inputAlbum2.setLabelId(3L);
        inputAlbum2.setReleaseDate(LocalDate.of(2000, 02, 20));
        inputAlbum2.setListPrice(new BigDecimal("29.99"));

        outputAlbum1 = new Album();
        outputAlbum1.setTitle("album1");
        outputAlbum1.setArtistId(1L);
        outputAlbum1.setLabelId(2L);
        outputAlbum1.setReleaseDate(LocalDate.of(2022,01, 01));
        outputAlbum1.setListPrice(new BigDecimal("19.99"));

        outputAlbum1.setId(1L);

        outputAlbum2 = new Album();
        outputAlbum2.setTitle("album2");
        outputAlbum2.setArtistId(2L);
        outputAlbum2.setLabelId(3L);
        outputAlbum2.setReleaseDate(LocalDate.of(2000, 02, 20));
        outputAlbum2.setListPrice(new BigDecimal("29.99"));

        outputAlbum2.setId((long)2);

        albumList = new ArrayList<>();
        albumList.add(outputAlbum1);
        albumList.add(outputAlbum2);

        label = new Label("label", "website");
        label.setId(1L);

        artist = new Artist("Rock Start", null, null);
        artist.setId(1L);
    }

    @Test
    public void shouldReturnAllAlbums() throws Exception {

        doReturn(albumList).when(albumRepo).findAll();

        String outJson = mapper.writeValueAsString(albumList);

        mockMvc.perform(
                        get("/album")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(outJson));

    }

    @Test
    public void shouldReturnAlbumById() throws Exception {
        when(albumRepo.findById((long)1)).thenReturn(Optional.of(outputAlbum1));
        String outJson = mapper.writeValueAsString(outputAlbum1);

        mockMvc.perform(
                        get("/album/{id}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(outJson));
    }

    @Test
    public void shouldReturnNewAlbum() throws Exception {

        when(albumRepo.save(inputAlbum1)).thenReturn(outputAlbum1);
        when(labelRepo.findById(2L)).thenReturn(Optional.of(label));
        when(artistRepo.findById(1L)).thenReturn(Optional.of(artist));

        String inJson = mapper.writeValueAsString(inputAlbum1);
        String outJson = mapper.writeValueAsString(outputAlbum1);

        mockMvc.perform(
                        post("/album")
                                .content(inJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(outJson));

    }

    @Test
    public void shouldReturn204StatusWithUpdate() throws Exception {

        when(albumRepo.save(outputAlbum2)).thenReturn(null);
        when(labelRepo.findById(3L)).thenReturn(Optional.of(label));
        when(artistRepo.findById(2L)).thenReturn(Optional.of(artist));
        String inJson = mapper.writeValueAsString(outputAlbum2);
        mockMvc.perform(
                        put("/album")
                                .content(inJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturn422StatusCreateAlbumWithMissingProperties() throws Exception {
        Album badInputRequest = inputAlbum2;
        badInputRequest.setListPrice(null);
        String badInputJson = mapper.writeValueAsString(badInputRequest);
//        the mocking is not necessary. exception was throw at controller layer.
//        when(labelRepo.findById(2L)).thenReturn(Optional.of(label));
//        when(artistRepo.findById(1L)).thenReturn(Optional.of(artist));
//
//        doReturn(new IllegalArgumentException("No List price found, unable to create")).when(albumRepo).save(badInputRequest);

        mockMvc.perform(
                        post("/album")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldReturn422StatusWhenLabelIdOrArtistIdNotExist() throws Exception {
        Album badInputRequest = inputAlbum2;
        String badInputJson = mapper.writeValueAsString(badInputRequest);
//        Mocking is not necessary
//        when(labelRepo.findById(anyLong())).thenReturn(null);
//        when(artistRepo.findById(anyLong())).thenReturn(null);

        //create a new album with not exist label id and artist id;
        mockMvc.perform(
                        post("/album")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());


        badInputRequest = outputAlbum2;
        badInputJson = mapper.writeValueAsString(badInputRequest);

        //update a album with not exist label id and artist id
        mockMvc.perform(
                        put("/album")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());


        badInputRequest = outputAlbum2;
        badInputJson = mapper.writeValueAsString(badInputRequest);
    }
    @Test
    public void shouldReturn422StatusUpdateAlbumWithMissingProperties() throws Exception {
        Album badInputRequest = outputAlbum1;
        badInputRequest.setListPrice(null);
        String badInputJson = mapper.writeValueAsString(badInputRequest);

        mockMvc.perform(
                        put("/album")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldFailWhenUpdateWithBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("No id found, unable to update")).when(albumRepo).save(inputAlbum1);
        String inJson = mapper.writeValueAsString(inputAlbum1);
        mockMvc.perform(
                        put("/album")
                                .content(inJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldReturnNoContentWithDelete() throws Exception {
        doNothing().when(albumRepo).deleteById(1L);

        mockMvc.perform(
                        delete("/album/{id}", 1L)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}