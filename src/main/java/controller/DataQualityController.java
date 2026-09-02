package com.dataquality.validator.controller;

import com.dataquality.validator.entity.Validation;
import com.dataquality.validator.repository.ValidationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/vehicle")
@CrossOrigin
public class DataQualityController {

    private static final Pattern NUMBER_PLATE_PATTERN =
            Pattern.compile("^[A-Z]{2}[0-9]{1,2}[A-Z]{1,3}[0-9]{1,4}$");

    private final ValidationRepository validationRepository;

    public DataQualityController(ValidationRepository validationRepository) {
        this.validationRepository = validationRepository;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Vehicle Number Plate Validator is running!";
    }

    @PostMapping("/validate")
    public String validateNumberPlate(@RequestBody String numberPlate) {

        if (numberPlate == null || numberPlate.trim().isEmpty()) {
            return "Invalid: Number plate is empty";
        }

        numberPlate = numberPlate.trim().toUpperCase();

        boolean isValid =
                NUMBER_PLATE_PATTERN.matcher(numberPlate).matches();

        Validation validation =
                new Validation(numberPlate, isValid);

        validationRepository.save(validation);

        if (isValid) {
            return "Valid Number Plate: " + numberPlate;
        }

        return "Invalid Number Plate: " + numberPlate;
    }

    @GetMapping("/stats")
    public Map<String, Long> getStatistics() {

        long total = validationRepository.count();
        long valid = validationRepository.countByValidTrue();
        long invalid = validationRepository.countByValidFalse();

        return Map.of(
                "total", total,
                "valid", valid,
                "invalid", invalid
        );
    }

    @GetMapping("/history")
    public List<Validation> getHistory() {
        return validationRepository.findAll();
    }

    // CLEAR ALL HISTORY
    @DeleteMapping("/history")
    public String clearHistory() {

        validationRepository.deleteAll();

        return "Validation history cleared successfully";
    }
}