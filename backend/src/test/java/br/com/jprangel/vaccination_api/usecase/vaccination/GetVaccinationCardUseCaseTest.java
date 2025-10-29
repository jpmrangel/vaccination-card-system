package br.com.jprangel.vaccination_api.usecase.vaccination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.DoseStatusDTO;
import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.dto.VaccinationCardGridDTO;
import br.com.jprangel.vaccination_api.dto.VaccineStatusDTO;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.mapper.PersonMapper;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.VaccinationRecord;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.model.enuns.DoseStatus;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import br.com.jprangel.vaccination_api.model.enuns.Sex;
import br.com.jprangel.vaccination_api.model.enuns.VaccineCategory;
import br.com.jprangel.vaccination_api.repository.PersonRepository;
import br.com.jprangel.vaccination_api.repository.VaccinationRecordRepository;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@ExtendWith(MockitoExtension.class)
public class GetVaccinationCardUseCaseTest {
  
  @Mock
  private PersonRepository personRepository;

  @Mock
  private VaccineRepository vaccineRepository;

  @Mock
  private VaccinationRecordRepository vaccinationRecordRepository;

  @Mock
  private PersonMapper personMapper;

  @InjectMocks
  private GetVaccinationCardGridUseCase getVaccinationCardGridUseCase;

  private Person person;
  private PersonResponse personResponse;
  private Vaccine vaccineBCG;
  private Vaccine vaccineHepB;
  private VaccinationRecord recordBCG;
  private Long personId;

  @BeforeEach
  void setUp() {
    personId = 1L;

    person = new Person(
      personId, "João Teste", "12345678900", 
      LocalDate.of(1990, 1, 1), Sex.MASCULINO, null
    );

    personResponse = new PersonResponse(
      personId, "João Teste", "12345678900", 
      LocalDate.of(1990, 1, 1), Sex.MASCULINO
    );

    // Vacina 1: BCG (só tem 1ª dose)
    vaccineBCG = new Vaccine(
      1L, "BCG", VaccineCategory.CARTEIRA_NACIONAL, 
      List.of(DoseType.PRIMEIRA_DOSE)
    );

    // Vacina 2: HepB (tem 1ª e 2ª dose)
    vaccineHepB = new Vaccine(
      2L, "Hepatite B", VaccineCategory.CARTEIRA_NACIONAL, 
      List.of(
          DoseType.PRIMEIRA_DOSE, 
          DoseType.SEGUNDA_DOSE, 
          DoseType.PRIMEIRO_REFORCO, 
          DoseType.SEGUNDO_REFORCO
      )
    );

    // Registro: Pessoa tomou a 1ª dose da BCG
    recordBCG = new VaccinationRecord(
      101L, LocalDate.now(), DoseType.PRIMEIRA_DOSE, person, vaccineBCG
    );
  }

  @Test
  @DisplayName("Deve retornar o grid de vacinação com lógica (TAKEN, MISSING, NOT_APPLICABLE)")
  void shouldReturnVaccinationGridWithLogic() {
    // A pessoa tomou a BCG
    List<VaccinationRecord> records = List.of(recordBCG);
    // O sistema tem duas vacinas cadastradas (BCG e HepB)
    List<Vaccine> allVaccines = List.of(vaccineBCG, vaccineHepB);
    
    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccineRepository.findAllByOrderByNameAsc()).thenReturn(allVaccines);
    when(vaccinationRecordRepository.findByPersonId(personId)).thenReturn(records);
    when(personMapper.toResponse(person)).thenReturn(personResponse);

    VaccinationCardGridDTO gridDTO = getVaccinationCardGridUseCase.execute(personId, null); 

    assertNotNull(gridDTO);
    assertEquals(personResponse, gridDTO.getPerson());
    assertNotNull(gridDTO.getVaccines());
    assertEquals(2, gridDTO.getVaccines().size());

    // --- Verificando a Vacina 1 (BCG) ---
    VaccineStatusDTO bcgStatus = gridDTO.getVaccines().get(0);
    assertEquals("BCG", bcgStatus.getVaccineName());
    
    Map<DoseType, DoseStatus> bcgDoses = bcgStatus.getDoses().stream()
        .collect(Collectors.toMap(DoseStatusDTO::getDoseType, DoseStatusDTO::getStatus));

    // A pessoa TOMOU a 1ª dose
    assertEquals(DoseStatus.TAKEN, bcgDoses.get(DoseType.PRIMEIRA_DOSE));
    // O esquema da BCG NÃO TEM 2ª dose
    assertEquals(DoseStatus.NOT_APPLICABLE, bcgDoses.get(DoseType.SEGUNDA_DOSE));
    assertEquals(DoseStatus.NOT_APPLICABLE, bcgDoses.get(DoseType.TERCEIRA_DOSE));
    assertEquals(DoseStatus.NOT_APPLICABLE, bcgDoses.get(DoseType.PRIMEIRO_REFORCO));
    assertEquals(DoseStatus.NOT_APPLICABLE, bcgDoses.get(DoseType.SEGUNDO_REFORCO));
    
    // --- Verificando a Vacina 2 (Hepatite B) ---
    VaccineStatusDTO hepBStatus = gridDTO.getVaccines().get(1);
    assertEquals("Hepatite B", hepBStatus.getVaccineName());

    Map<DoseType, DoseStatus> hepBDoses = hepBStatus.getDoses().stream()
        .collect(Collectors.toMap(DoseStatusDTO::getDoseType, DoseStatusDTO::getStatus));

    // O esquema TEM 1ª dose, mas a pessoa não tomou -> FALTOSA
    assertEquals(DoseStatus.MISSING, hepBDoses.get(DoseType.PRIMEIRA_DOSE));
    // O esquema TEM 2ª dose, mas a pessoa não tomou -> FALTOSA
    assertEquals(DoseStatus.MISSING, hepBDoses.get(DoseType.SEGUNDA_DOSE));
    // O esquema NÃO TEM 3ª dose
    assertEquals(DoseStatus.NOT_APPLICABLE, hepBDoses.get(DoseType.TERCEIRA_DOSE));
    // O esquema TEM 1º Reforço, mas a pessoa não tomou -> FALTOSA
    assertEquals(DoseStatus.MISSING, hepBDoses.get(DoseType.PRIMEIRO_REFORCO));
    // O esquema TEM 2º Reforço, mas a pessoa não tomou -> FALTOSA
    assertEquals(DoseStatus.MISSING, hepBDoses.get(DoseType.SEGUNDO_REFORCO));
  }

  @Test
  @DisplayName("Deve retornar o grid de vacinação filtrado por categoria")
  void shouldReturnVaccinationGridWithCategoryFilter() {
    // A pessoa tomou a BCG
    List<VaccinationRecord> records = List.of(recordBCG);
    List<Vaccine> filteredVaccines = List.of(vaccineBCG); 
    
    VaccineCategory filter = VaccineCategory.CARTEIRA_NACIONAL;

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    
    when(vaccineRepository.findByCategoryOrderByNameAsc(filter)).thenReturn(filteredVaccines); 
    
    when(vaccinationRecordRepository.findByPersonId(personId)).thenReturn(records);
    when(personMapper.toResponse(person)).thenReturn(personResponse);

    VaccinationCardGridDTO gridDTO = getVaccinationCardGridUseCase.execute(personId, filter);

    assertNotNull(gridDTO);
    assertEquals(1, gridDTO.getVaccines().size()); 
    assertEquals("BCG", gridDTO.getVaccines().get(0).getVaccineName());

    verify(vaccineRepository, times(1)).findByCategoryOrderByNameAsc(filter);
    verify(vaccineRepository, never()).findAllByOrderByNameAsc();
  }


  @Test
  @DisplayName("Deve lançar ResourceNotFoundException quando a pessoa não for encontrada")
  void shouldThrowResourceNotFoundExceptionWhenPersonNotFound() {
    when(personRepository.findById(personId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      getVaccinationCardGridUseCase.execute(personId, null);
    });

    assertEquals("Person with ID " + personId + " not found.", exception.getMessage());
    
    verify(personRepository, times(1)).findById(personId);
    verify(vaccineRepository, never()).findAllByOrderByNameAsc();
    verify(vaccinationRecordRepository, never()).findByPersonId(anyLong());
  }
}
