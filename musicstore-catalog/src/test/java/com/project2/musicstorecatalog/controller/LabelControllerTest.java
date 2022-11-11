package com.project2.musicstorecatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project2.musicstorecatalog.model.Label;
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

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(LabelController.class)
public class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private LabelRepository labelRepo;

    private Label inputLabel1;
    private Label inputLabel2;
    private Label outputLabel1;
    private Label outputLabel2;

    private List<Label> labelList;


    @Before
    public void setUp () {
        mapper = new ObjectMapper();

        inputLabel1 = new Label("label1", "www.label1.com");
        inputLabel2 = new Label("label2", "www.label2.com");

        outputLabel1 = new Label("label1", "www.label1.com");
        outputLabel1.setId((long)1);
        outputLabel2 = new Label("label2", "www.label2.com");
        outputLabel2.setId((long)2);

        labelList = new ArrayList<>();
        labelList.add(outputLabel1);
        labelList.add(outputLabel2);
    }


    @Test
    public void shouldReturnNewLabel() throws Exception {

        when(labelRepo.save(inputLabel1)).thenReturn(outputLabel1);

        String inJson = mapper.writeValueAsString(inputLabel1);
        String outJson = mapper.writeValueAsString(outputLabel1);

        mockMvc.perform(
                post("/label")
                        .content(inJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(outJson));

    }

    @Test
    public void shouldReturnLabelById() throws Exception {
        when(labelRepo.findById((long)1)).thenReturn(Optional.of(outputLabel1));
        String outJson = mapper.writeValueAsString(outputLabel1);

        mockMvc.perform(
                get("/label/{id}", (long)1)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(outJson));
    }

    @Test
    public void shouldReturn204StatusWithUpdate() throws Exception {
        when(labelRepo.save(outputLabel2)).thenReturn(null);
        String inJson = mapper.writeValueAsString(outputLabel2);
        mockMvc.perform(
                put("/label")
                        .content(inJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturn422StatusCreateLabelWithMissingNames() throws Exception {
        Label badInputRequest = inputLabel1;
        badInputRequest.setName(null);


        String badInputJson = mapper.writeValueAsString(badInputRequest);

        doReturn(new IllegalArgumentException("Name is missing, unable to create")).when(labelRepo).save(badInputRequest);

        mockMvc.perform(
                        post("/label")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
    @Test
    public void shouldReturn422StatusUpdateLabelWithMissingProperties() throws Exception {
        Label badInputRequest = inputLabel2;
        badInputRequest.setName(null);


        String badInputJson = mapper.writeValueAsString(badInputRequest);

        doReturn(new IllegalArgumentException("Name is missing, unable to update")).when(labelRepo).save(badInputRequest);

        mockMvc.perform(
                        put("/label")
                                .content(badInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldFailWhenUpdateWithBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("No id found, unable to update")).when(labelRepo).save(inputLabel1);
        String inJson = mapper.writeValueAsString(inputLabel1);
        mockMvc.perform(
                put("/label")
                        .content(inJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldReturnNoContentWithDelete() throws Exception {
        doNothing().when(labelRepo).deleteById((long)1);

        mockMvc.perform(
                delete("/label/{id}", (long)1)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnAllLabel() throws Exception {

        doReturn(labelList).when(labelRepo).findAll();

        String outJson = mapper.writeValueAsString(labelList);

        mockMvc.perform(
                get("/label")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(outJson));

    }

}