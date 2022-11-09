package com.project2.musicstorecatalog.controller;

import com.project2.musicstorecatalog.model.Label;
import com.project2.musicstorecatalog.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/label")
public class LabelController {

    @Autowired
    LabelRepository repo;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Label> getAllLabels() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Label getLabelById(@PathVariable Long id) {
        Optional<Label> returnVal = repo.findById(id);
        return returnVal.isPresent() ? returnVal.get() : null;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Label addLabel(@RequestBody Label label) {
        return repo.save(label);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLabel(@RequestBody Label label) {
        repo.save(label);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable Long id) {
        repo.deleteById(id);
    }



}
