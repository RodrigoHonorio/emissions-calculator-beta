package uk.org.spire.emissions_calculator_beta.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.org.spire.emissions_calculator_beta.entity.LondonBorough;
import uk.org.spire.emissions_calculator_beta.repository.LondonBoroughRepository;

import java.util.List;

@RestController
@RequestMapping("/api/boroughs")
@RequiredArgsConstructor
public class LondonBoroughController {

    private final LondonBoroughRepository repository;

    @GetMapping
    public ResponseEntity<List<LondonBorough>> getAllBoroughs() {
        List<LondonBorough> boroughs = repository.findAll();
        return ResponseEntity.ok(boroughs);
    }
}