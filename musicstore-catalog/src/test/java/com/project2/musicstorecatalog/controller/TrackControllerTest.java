package com.project2.musicstorecatalog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project2.musicstorecatalog.model.Album;
import com.project2.musicstorecatalog.model.Track;
import com.project2.musicstorecatalog.repository.AlbumRepository;
import com.project2.musicstorecatalog.repository.TrackRepository;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackController.class)
public class TrackControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TrackRepository trackRepo;

    @MockBean
    private AlbumRepository albumRepo;

    private Track inputTrack1;
    private Track inputTrack2;
    private Track outputTrack1;
    private Track outputTrack2;

    private List<Track> trackList;

    private Album album;

    @Before
    public void setUp() {

        mapper = new ObjectMapper();

        inputTrack1 = new Track();
        inputTrack1.setAlbumId(1L);
        inputTrack1.setTitle("Thunderstruck");
        inputTrack1.setRunTime(10);

        inputTrack2 = new Track();
        inputTrack2.setAlbumId(2L);
        inputTrack2.setTitle("Never Fade Away");
        inputTrack2.setRunTime(6);

        outputTrack1 = new Track();
        outputTrack1.setAlbumId(1L);
        outputTrack1.setTitle("Thunderstruck");
        outputTrack1.setRunTime(10);

        outputTrack1.setId(1L);

        outputTrack2 = new Track();
        outputTrack2.setAlbumId(2L);
        outputTrack2.setTitle("Never Fade Away");
        outputTrack2.setRunTime(6);

        outputTrack2.setId(2L);

        trackList = new ArrayList<>();
        trackList.add(outputTrack1);
        trackList.add(outputTrack2);
        //empty album object for mocked album repo only.
        album = new Album();
        album.setId(1L);

    }

    @Test
    public void shouldReturnAllTrack() throws Exception {

        doReturn(trackList).when(trackRepo).findAll();
        String outJson = mapper.writeValueAsString(trackList);

        mockMvc.perform(
                        get("/track")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(outJson));
    }

    @Test
    public void shouldReturnTrackById() throws Exception {
        when(trackRepo.findById((long)1)).thenReturn(Optional.of(outputTrack1));
        String outJson = mapper.writeValueAsString(outputTrack1);

        mockMvc.perform(
                        get("/track/{id}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(outJson));
    }

    @Test
    public void shouldReturnNewTrack() throws Exception {

        when(trackRepo.save(inputTrack1)).thenReturn(outputTrack1);
        //assume the album id exists in the database
        when(albumRepo.findById(outputTrack1.getId())).thenReturn(Optional.of(album));

        String inJson = mapper.writeValueAsString(inputTrack1);
        String outJson = mapper.writeValueAsString(outputTrack1);

        mockMvc.perform(
                        post("/track")
                                .content(inJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(outJson));
    }

    @Test
    public void shouldReturn204StatusWithUpdate() throws Exception {
        when(trackRepo.save(outputTrack2)).thenReturn(null);
        //assume the album id exists in the database
        when(albumRepo.findById(outputTrack2.getId())).thenReturn(Optional.of(album));
        String inJson = mapper.writeValueAsString(outputTrack2);
        mockMvc.perform(
                        put("/track")
                                .content(inJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldFailWhenUpdateWithBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("No id found, unable to update")).when(trackRepo).save(inputTrack1);
        String inJson = mapper.writeValueAsString(inputTrack1);
        mockMvc.perform(
                        put("/track")
                                .content(inJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
    @Test
    public void shouldReturn422StatusCreateTrackWithMissingProperties() throws Exception {
        Track badInputRequest = inputTrack2;
        badInputRequest.setTitle(null);
        String badInputJson = mapper.writeValueAsString(badInputRequest);

        mockMvc.perform(
                        post("/track")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        badInputRequest = inputTrack2;
        badInputRequest.setAlbumId(null);

        mockMvc.perform(
                        post("/track")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    public void shouldReturn422StatusWhenAlbumIdNotExist() throws Exception {
        Track badInputRequest = inputTrack2;
        String badInputString = mapper.writeValueAsString(badInputRequest);

        mockMvc.perform(
                post("/track")
                        .content(badInputString)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        badInputRequest = outputTrack2;
        badInputString = mapper.writeValueAsString(badInputRequest);

        mockMvc.perform(
                put("/track")
                        .content(badInputString)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

    }


    @Test
    public void shouldReturn422StatusUpdateTrackWithMissingProperties() throws Exception {
        Track badInputRequest = inputTrack2;
        badInputRequest.setTitle(null);
        String badInputJson = mapper.writeValueAsString(badInputRequest);

        mockMvc.perform(
                        post("/track")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        badInputRequest = inputTrack2;
        badInputRequest.setAlbumId(null);

        mockMvc.perform(
                        post("/track")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldReturnNoContentWithDelete() throws Exception {
        doNothing().when(trackRepo).deleteById((long)1);

        mockMvc.perform(
                        delete("/track/{id}", (long)1)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}