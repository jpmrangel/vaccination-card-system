package br.com.jprangel.vaccination_api.usecase.vaccination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.DoseStatusDTO;
import br.com.jprangel.vaccination_api.dto.VaccinationCardGridDTO;
import br.com.jprangel.vaccination_api.dto.VaccineStatusDTO;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.mapper.PersonMapper;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.VaccinationRecord;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.model.enuns.DoseStatus;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import br.com.jprangel.vaccination_api.model.enuns.VaccineCategory;
import br.com.jprangel.vaccination_api.repository.PersonRepository;
import br.com.jprangel.vaccination_api.repository.VaccinationRecordRepository;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@Service
public class GetVaccinationCardGridUseCase {
  
  private final PersonRepository personRepository;
  private final VaccineRepository vaccineRepository;
  private final VaccinationRecordRepository vaccinationRecordRepository;
  private final PersonMapper personMapper;

  public GetVaccinationCardGridUseCase(
    PersonRepository personRepository,
    VaccineRepository vaccineRepository,
    VaccinationRecordRepository vaccinationRecordRepository,
    PersonMapper personMapper) {
      
    this.personRepository = personRepository;
    this.vaccineRepository = vaccineRepository;
    this.vaccinationRecordRepository = vaccinationRecordRepository;
    this.personMapper = personMapper;
  }

  @Transactional(readOnly = true)
  public VaccinationCardGridDTO execute(Long personId, VaccineCategory category) {
    Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResourceNotFoundException("Person with ID " + personId + " not found."));

    // Buscar Vacinas (com ou sem filtro de categoria)
    List<Vaccine> vaccines;
    if (category == null)
      vaccines = vaccineRepository.findAllByOrderByNameAsc();
    else
      vaccines = vaccineRepository.findByCategoryOrderByNameAsc(category);
    
    // Buscar TODOS os registros da pessoa e otimizar para busca rápida
    Map<String, VaccinationRecord> takenDosesMap = 
            vaccinationRecordRepository.findByPersonId(personId).stream()
              .collect(Collectors.toMap(
                record -> record.getVaccine().getId() + ":" + record.getDose(),
                record -> record
              ));
    
    // Montar o DTO do Grid
    VaccinationCardGridDTO gridDTO = new VaccinationCardGridDTO();
    gridDTO.setPerson(personMapper.toResponse(person));
    
    List<VaccineStatusDTO> vaccineStatusList = new ArrayList<>();
    
    // O "Merge" - Lógica principal
    for (Vaccine vaccine : vaccines) {
      VaccineStatusDTO vaccineStatus = new VaccineStatusDTO();
      vaccineStatus.setVaccineId(vaccine.getId());
      vaccineStatus.setVaccineName(vaccine.getName());
      vaccineStatus.setCategory(vaccine.getCategory());
      
      List<DoseStatusDTO> doseStatusList = new ArrayList<>();
      List<DoseType> schedule = vaccine.getDoseSchedule();

      // Iteramos sobre TODOS os tipos de dose do sistema
      for (DoseType doseType : DoseType.values()) {
        String lookupKey = vaccine.getId() + ":" + doseType;
        
        // Se a dose está no esquema, ela pode ser TOMADA ou FALTOSA
        if (schedule.contains(doseType)) {
          if (takenDosesMap.containsKey(lookupKey)) { // TOMADA
            VaccinationRecord record = takenDosesMap.get(lookupKey);
            doseStatusList.add(new DoseStatusDTO(
              doseType, 
              DoseStatus.TAKEN, 
              record.getId(), 
              record.getApplicationDate()
            ));
          } else { // FALTOSA
            doseStatusList.add(new DoseStatusDTO(
              doseType, 
              DoseStatus.MISSING, 
              null, 
              null
            ));
          }
        } else { // NÃO APLICÁVEL (cinza)
          doseStatusList.add(new DoseStatusDTO(
            doseType, 
            DoseStatus.NOT_APPLICABLE, 
            null, 
            null
          ));
        }
      }
      vaccineStatus.setDoses(doseStatusList);
      vaccineStatusList.add(vaccineStatus);
    }

    gridDTO.setVaccines(vaccineStatusList);
    return gridDTO;
  }
  
}
