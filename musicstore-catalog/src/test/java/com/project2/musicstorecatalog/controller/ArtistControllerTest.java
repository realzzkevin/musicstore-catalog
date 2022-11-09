package com.project2.musicstorecatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project2.musicstorecatalog.model.Artist;
import com.project2.musicstorecatalog.model.Label;
import com.project2.musicstorecatalog.repository.ArtistRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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
@WebMvcTest(ArtistController.class)
public class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ArtistRepository artistRepo;

    private Artist inputArtist1;
    private Artist inputArtist2;
    private Artist outputArtist1;
    private Artist outputArtist2;

    private List<Artist> artistList;

    @Before
    public void setUp() {

        mapper = new ObjectMapper();

        inputArtist1 = new Artist("Rock Star", "@RockStar", "#RockStar");
        inputArtist2 = new Artist("Pop Star", "@PopStar", "#PopStar");

        outputArtist1 = new Artist("Rock Star", "@RockStar", "#RockStar");
        outputArtist1.setId((long)1);
        outputArtist2 = new Artist("Pop Star", "@PopStar", "#PopStar");
        outputArtist2.setId((long)2);

        artistList = new ArrayList<>();
        artistList.add(outputArtist1);
        artistList.add(outputArtist2);

    }

    @Test
    public void shouldReturnAllArtist() throws Exception {

        doReturn(artistList).when(artistRepo).findAll();

        String outJson = mapper.writeValueAsString(artistList);

        mockMvc.perform(
                        get("/artist")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(outJson));

    }

    @Test
    public void shouldReturnArtistById() throws Exception {
        when(artistRepo.findById((long)1)).thenReturn(Optional.of(outputArtist1));
        String outJson = mapper.writeValueAsString(outputArtist1);

        mockMvc.perform(
                        get("/artist/{id}", (long)1)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(outJson));
    }

    @Test
    public void shouldReturnNewArtist() throws Exception {

        when(artistRepo.save(inputArtist1)).thenReturn(outputArtist1);

        String inJson = mapper.writeValueAsString(inputArtist1);
        String outJson = mapper.writeValueAsString(outputArtist1);

        mockMvc.perform(
                        post("/artist")
                                .content(inJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(outJson));

    }

    @Test
    public void shouldReturn204StatusWithUpdate() throws Exception {
        when(artistRepo.save(inputArtist2)).thenReturn(null);
        String inJson = mapper.writeValueAsString(inputArtist2);
        mockMvc.perform(
                        put("/artist")
                                .content(inJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldFailWhenUpdateWithBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("No id found, unable to update")).when(artistRepo).save(inputArtist1);
        String inJson = mapper.writeValueAsString(inputArtist1);
        mockMvc.perform(
                        put("/artist")
                                .content(inJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldReturnNoContentWithDelete() throws Exception {
        doNothing().when(artistRepo).deleteById((long)1);

        mockMvc.perform(
                        delete("/artist/{id}", (long)1)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}