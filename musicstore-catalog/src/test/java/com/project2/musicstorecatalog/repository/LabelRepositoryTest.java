package com.project2.musicstorecatalog.repository;

import com.project2.musicstorecatalog.model.Label;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LabelRepositoryTest {

    @Autowired
    LabelRepository labelRepository;

    private Label label1;
    private Label label2;
    private Label label3;


    @Before
    public void setUp() throws Exception {
        labelRepository.deleteAll();

        label1 = new Label("label1", "www.label1.com");
        label2 = new Label("label2", "www.label2.com");
        label3 = new Label("label3", "www.label3.com");
    }

    @After
    public void tearDown() throws Exception {
        labelRepository.deleteAll();
    }

    @Test
    public void shouldCreateFindLabel() {
        Label savedLabel = labelRepository.save(label1);
        labelRepository.save(label2);

        Optional<Label> foundLabel = labelRepository.findById(savedLabel.getId());

        assertTrue(foundLabel.isPresent());
        assertEquals(savedLabel, foundLabel.get());

    }

    @Test
    public void shouldFindAllLabel() {

        Label savedLabel1 = labelRepository.save(label1);
        Label savedLabel2 = labelRepository.save(label2);
        Set<Label> savedLabelList = new HashSet<>();

        savedLabelList.add(savedLabel1);
        savedLabelList.add(savedLabel2);

        Set<Label> outputList = new HashSet<>(labelRepository.findAll());
        assertEquals(savedLabelList.size(), outputList.size());
        assertEquals(savedLabelList, outputList);
    }

    @Test
    public void shouldUpdateLabelById (){

        Label savedLabel = labelRepository.save(label1);

        savedLabel.setName("update label");
        labelRepository.save(savedLabel);
        Optional<Label> updatedLabel = labelRepository.findById(savedLabel.getId());

        assertEquals(savedLabel, updatedLabel.get());

    }

    @Test
    public void shouldDeleteLabel (){
        Label savedLabel1 = labelRepository.save(label1);
        Label savedLabel2 = labelRepository.save(label2);

        labelRepository.deleteById(savedLabel1.getId());
        Optional<Label> foundLabel = labelRepository.findById(savedLabel1.getId());

        assertFalse(foundLabel.isPresent());
    }
}