package br.com.jprangel.vaccination_api.usecase.vaccination;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.VaccinationCardResponse;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.VaccinationRecord;
import br.com.jprangel.vaccination_api.repository.PersonRepository;
import br.com.jprangel.vaccination_api.repository.VaccinationRecordRepository;

@Service
public class GetVaccinationCardUseCase {
  
  private final PersonRepository personRepository;
  private final VaccinationRecordRepository vaccinationRecordRepository;

  public GetVaccinationCardUseCase(PersonRepository personRepository, VaccinationRecordRepository vaccinationRecordRepository) {
    this.personRepository = personRepository;
    this.vaccinationRecordRepository = vaccinationRecordRepository;
  }

  @Transactional(readOnly = true)
  public VaccinationCardResponse execute(Long personId) {
    Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResourceNotFoundException("Person with ID " + personId + " not found."));

    List<VaccinationRecord> records = vaccinationRecordRepository.findByPersonId(personId);

    List<VaccinationCardResponse.VaccinationRecordInfo> recordInfos = records
            .stream()
            .map(record -> {
              VaccinationCardResponse.VaccinationRecordInfo info = new VaccinationCardResponse.VaccinationRecordInfo();
              info.setRecordId(record.getId());
              info.setVaccineName(record.getVaccine().getName());
              info.setDose(record.getDose());
              info.setApplicationDate(record.getApplicationDate());
              return info;
            }).collect(Collectors.toList());

    VaccinationCardResponse response = new VaccinationCardResponse();
    response.setPersonId(person.getId());
    response.setPersonName(person.getName());
    response.setPersonCpf(person.getCpf());
    response.setRecords(recordInfos);

    return response;
  }
  
}
