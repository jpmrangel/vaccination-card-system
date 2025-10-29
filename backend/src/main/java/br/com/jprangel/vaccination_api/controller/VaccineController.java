package br.com.jprangel.vaccination_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jprangel.vaccination_api.dto.VaccineRequest;
import br.com.jprangel.vaccination_api.dto.VaccineResponse;
import br.com.jprangel.vaccination_api.usecase.vaccine.CreateVaccineUseCase;
import br.com.jprangel.vaccination_api.usecase.vaccine.FindAllVaccinesUseCase;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineController {
  
  private final CreateVaccineUseCase createVaccineUseCase;
  private final FindAllVaccinesUseCase findAllVaccinesUseCase;

  public VaccineController(
    CreateVaccineUseCase createVaccineUseCase, 
    FindAllVaccinesUseCase findAllVaccinesUseCase) {
      
    this.createVaccineUseCase = createVaccineUseCase;
    this.findAllVaccinesUseCase = findAllVaccinesUseCase;
  }

  @PostMapping
  public ResponseEntity<VaccineResponse> createVaccine(@RequestBody VaccineRequest request) {
    VaccineResponse response = createVaccineUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<VaccineResponse>> getAllVaccines() {
    List<VaccineResponse> response = findAllVaccinesUseCase.execute();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
