package br.com.jprangel.vaccination_api.usecase.vaccination;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.VaccinationCardResponse;
import br.com.jprangel.vaccination_api.dto.VaccinationRecordRequest;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.VaccinationRecord;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.repository.PersonRepository;
import br.com.jprangel.vaccination_api.repository.VaccinationRecordRepository;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@Service
public class AddVaccinationUseCase {
  
  private final PersonRepository personRepository;
  private final VaccineRepository vaccineRepository;
  private final VaccinationRecordRepository vaccinationRecordRepository;
  private final GetVaccinationCardUseCase getVaccinationCardUseCase;

  public AddVaccinationUseCase(PersonRepository personRepository,
                              VaccineRepository vaccineRepository,
                              VaccinationRecordRepository vaccinationRecordRepository,
                              GetVaccinationCardUseCase getVaccinationCardUseCase) {
    this.personRepository = personRepository;
    this.vaccineRepository = vaccineRepository;
    this.vaccinationRecordRepository = vaccinationRecordRepository;
    this.getVaccinationCardUseCase = getVaccinationCardUseCase;
  }

  @Transactional
  public VaccinationCardResponse execute(Long personId, VaccinationRecordRequest request) {
    Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResourceNotFoundException("Person with ID " + personId + " not found."));

    Vaccine vaccine = vaccineRepository.findById(request.getVaccineId())
            .orElseThrow(() -> new ResourceNotFoundException("Vaccine with ID " + request.getVaccineId() + " not found."));

    VaccinationRecord newRecord = new VaccinationRecord();
    newRecord.setPerson(person);
    newRecord.setVaccine(vaccine);
    newRecord.setDose(request.getDose());
    newRecord.setApplicationDate(request.getApplicationDate());

    vaccinationRecordRepository.save(newRecord);

    return getVaccinationCardUseCase.execute(personId);
  }
}
