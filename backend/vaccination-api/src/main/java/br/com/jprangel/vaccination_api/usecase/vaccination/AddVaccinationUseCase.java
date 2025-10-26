package br.com.jprangel.vaccination_api.usecase.vaccination;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.VaccinationCardGridDTO;
import br.com.jprangel.vaccination_api.dto.VaccinationRecordRequest;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.VaccinationRecord;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import br.com.jprangel.vaccination_api.repository.PersonRepository;
import br.com.jprangel.vaccination_api.repository.VaccinationRecordRepository;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@Service
public class AddVaccinationUseCase {
  
  private final PersonRepository personRepository;
  private final VaccineRepository vaccineRepository;
  private final VaccinationRecordRepository vaccinationRecordRepository;
  private final GetVaccinationCardGridUseCase getVaccinationCardGridUseCase;

  public AddVaccinationUseCase(
    PersonRepository personRepository,
    VaccineRepository vaccineRepository,
    VaccinationRecordRepository vaccinationRecordRepository,
    GetVaccinationCardGridUseCase getVaccinationCardGridUseCase) {
      
    this.personRepository = personRepository;
    this.vaccineRepository = vaccineRepository;
    this.vaccinationRecordRepository = vaccinationRecordRepository;
    this.getVaccinationCardGridUseCase = getVaccinationCardGridUseCase;
  }

  @Transactional
  public VaccinationCardGridDTO execute(Long personId, VaccinationRecordRequest request) {
    Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResourceNotFoundException("Person with ID " + personId + " not found."));

    Vaccine vaccine = vaccineRepository.findById(request.getVaccineId())
            .orElseThrow(() -> new ResourceNotFoundException("Vaccine with ID " + request.getVaccineId() + " not found."));

    validateDose(person, vaccine, request.getDose());
    
    VaccinationRecord newRecord = new VaccinationRecord();
    newRecord.setPerson(person);
    newRecord.setVaccine(vaccine);
    newRecord.setDose(request.getDose());
    newRecord.setApplicationDate(request.getApplicationDate());

    vaccinationRecordRepository.save(newRecord);

    return getVaccinationCardGridUseCase.execute(personId, null);
  }

  private void validateDose(Person person, Vaccine vaccine, DoseType newDose) {
    Long personId = person.getId();
    Long vaccineId = vaccine.getId();

    // VALIDAÇÃO 1: A dose existe no esquema da vacina? (Evita blocos cinzas)
    if (!vaccine.getDoseSchedule().contains(newDose)) {
      throw new BusinessException("The requested dose: " + newDose.getDescricao() + 
           " is not applicable for the vaccine: " + vaccine.getName() + ".");
    }

    // VALIDAÇÃO 2: A dose já foi registrada? (Evita duplicatas)
    if (vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, newDose)) {
      throw new BusinessException("This dose (" + newDose.getDescricao() + ") has already been recorded for this person.");
    }

    // VALIDAÇÃO 3: Lógica de Sequência
    switch (newDose) {
      case SEGUNDA_DOSE:
        if (!vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.PRIMEIRA_DOSE)) {
          throw new BusinessException("1st dose is required before registering the 2nd dose.");
        }
        break;

      case TERCEIRA_DOSE:
        if (!vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.SEGUNDA_DOSE)) {
          throw new BusinessException("2nd dose is required before registering the 3rd dose.");
        }
        break;
      
      // Reforços (qualquer um deles)
      case PRIMEIRO_REFORCO:
      case SEGUNDO_REFORCO:
        DoseType lastPrimaryDose = findLastPrimaryDose(vaccine.getDoseSchedule());
        
        if (lastPrimaryDose == null) {
          throw new BusinessException("Unable to register the booster: primary dose schedule not defined for this vaccine.");
        }

        if (!vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, lastPrimaryDose)) {
          throw new BusinessException(lastPrimaryDose.getDescricao() + " is required before registering a booster dose.");
        }
        break;
      
      // Doses que não têm pré-requisitos
      case PRIMEIRA_DOSE:
      default:
        break;
    }
  }

  private DoseType findLastPrimaryDose(List<DoseType> schedule) {
    if (schedule.contains(DoseType.TERCEIRA_DOSE)) {
      return DoseType.TERCEIRA_DOSE;
    }
    if (schedule.contains(DoseType.SEGUNDA_DOSE)) {
      return DoseType.SEGUNDA_DOSE;
    }
    if (schedule.contains(DoseType.PRIMEIRA_DOSE)) {
      return DoseType.PRIMEIRA_DOSE;
    }
    return null;
  }
}
