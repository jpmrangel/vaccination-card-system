package br.com.jprangel.vaccination_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jprangel.vaccination_api.dto.VaccinationCardResponse;
import br.com.jprangel.vaccination_api.dto.VaccinationRecordRequest;
import br.com.jprangel.vaccination_api.usecase.vaccination.AddVaccinationUseCase;
import br.com.jprangel.vaccination_api.usecase.vaccination.DeleteVaccinationRecordUseCase;
import br.com.jprangel.vaccination_api.usecase.vaccination.GetVaccinationCardUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/api/persons/{personId}/card")
public class VaccinationRecordController {
  
  private final AddVaccinationUseCase addVaccinationUseCase;
  private final GetVaccinationCardUseCase getVaccinationCardUseCase;
  private final DeleteVaccinationRecordUseCase deleteVaccinationRecordUseCase;

  public VaccinationRecordController(AddVaccinationUseCase addVaccinationUseCase,
                                     GetVaccinationCardUseCase getVaccinationCardUseCase,
                                     DeleteVaccinationRecordUseCase deleteVaccinationRecordUseCase) {
    this.addVaccinationUseCase = addVaccinationUseCase;
    this.getVaccinationCardUseCase = getVaccinationCardUseCase;
    this.deleteVaccinationRecordUseCase = deleteVaccinationRecordUseCase;
  }

  @PostMapping
  public ResponseEntity<VaccinationCardResponse> addVaccination(
    @PathVariable Long personId, 
    @RequestBody VaccinationRecordRequest request) {

    VaccinationCardResponse response = addVaccinationUseCase.execute(personId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<VaccinationCardResponse> getVaccinationCard(@PathVariable Long personId) {
    VaccinationCardResponse response = getVaccinationCardUseCase.execute(personId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/records/{recordId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteVaccinationRecord(
    @PathVariable Long personId, 
    @PathVariable Long recordId) {
      
    deleteVaccinationRecordUseCase.execute(recordId);
  }
  

}
