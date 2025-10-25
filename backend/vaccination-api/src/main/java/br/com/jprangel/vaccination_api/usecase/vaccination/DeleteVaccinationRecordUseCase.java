package br.com.jprangel.vaccination_api.usecase.vaccination;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.repository.VaccinationRecordRepository;

@Service
public class DeleteVaccinationRecordUseCase {
  
  private final VaccinationRecordRepository vaccinationRecordRepository;

  public DeleteVaccinationRecordUseCase(VaccinationRecordRepository vaccinationRecordRepository) {
    this.vaccinationRecordRepository = vaccinationRecordRepository;
  }

  @Transactional
  public void execute(Long recordId) {
    if (!vaccinationRecordRepository.existsById(recordId)) {
      throw new ResourceNotFoundException("Vaccination record with ID " + recordId + " not found.");
    }
    vaccinationRecordRepository.deleteById(recordId);
  }
}
