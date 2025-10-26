package br.com.jprangel.vaccination_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.jprangel.vaccination_api.dto.VaccinationCardGridDTO;
import br.com.jprangel.vaccination_api.dto.VaccinationRecordRequest;
import br.com.jprangel.vaccination_api.model.enuns.VaccineCategory;
import br.com.jprangel.vaccination_api.usecase.vaccination.AddVaccinationUseCase;
import br.com.jprangel.vaccination_api.usecase.vaccination.DeleteVaccinationRecordUseCase;
import br.com.jprangel.vaccination_api.usecase.vaccination.GetVaccinationCardGridUseCase;

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
  private final GetVaccinationCardGridUseCase getVaccinationCardGridUseCase;
  private final DeleteVaccinationRecordUseCase deleteVaccinationRecordUseCase;

  public VaccinationRecordController(
    AddVaccinationUseCase addVaccinationUseCase,
    GetVaccinationCardGridUseCase getVaccinationCardGridUseCase,
    DeleteVaccinationRecordUseCase deleteVaccinationRecordUseCase) {

    this.addVaccinationUseCase = addVaccinationUseCase;
    this.getVaccinationCardGridUseCase = getVaccinationCardGridUseCase;
    this.deleteVaccinationRecordUseCase = deleteVaccinationRecordUseCase;
  }

  @PostMapping
  public ResponseEntity<VaccinationCardGridDTO> addVaccination(
    @PathVariable Long personId, 
    @RequestBody VaccinationRecordRequest request) {

    VaccinationCardGridDTO response = addVaccinationUseCase.execute(personId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<VaccinationCardGridDTO> getVaccinationCard(
    @PathVariable Long personId, 
    @RequestParam(required = false) VaccineCategory category) {

    VaccinationCardGridDTO response = getVaccinationCardGridUseCase.execute(personId, category);
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
