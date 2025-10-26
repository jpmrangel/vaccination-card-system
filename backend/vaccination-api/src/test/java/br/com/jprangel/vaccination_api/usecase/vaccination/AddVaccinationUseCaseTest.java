package br.com.jprangel.vaccination_api.usecase.vaccination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.VaccinationCardGridDTO;
import br.com.jprangel.vaccination_api.dto.VaccinationRecordRequest;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.VaccinationRecord;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import br.com.jprangel.vaccination_api.model.enuns.Sex;
import br.com.jprangel.vaccination_api.model.enuns.VaccineCategory;
import br.com.jprangel.vaccination_api.repository.PersonRepository;
import br.com.jprangel.vaccination_api.repository.VaccinationRecordRepository;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@ExtendWith(MockitoExtension.class)
public class AddVaccinationUseCaseTest {
  
  @Mock
  private PersonRepository personRepository;

  @Mock
  private VaccineRepository vaccineRepository;

  @Mock
  private VaccinationRecordRepository vaccinationRecordRepository;

  @Mock
  private GetVaccinationCardGridUseCase getVaccinationCardGridUseCase;

  @InjectMocks
  private AddVaccinationUseCase addVaccinationUseCase;

  private Person person;
  private Vaccine vaccine;
  private VaccinationRecordRequest request;
  private VaccinationCardGridDTO mockGridResponse;
  private Long personId;
  private Long vaccineId;

  @BeforeEach
  void setUp() {
    personId = 1L;
    vaccineId = 1L;

    person = new Person(
      personId, "Maria", "98765432100", 
      LocalDate.of(1990, 1, 1), Sex.FEMININO, Collections.emptyList()
    );

    List<DoseType> schedule = List.of(
      DoseType.PRIMEIRA_DOSE, 
      DoseType.SEGUNDA_DOSE, 
      DoseType.PRIMEIRO_REFORCO, 
      DoseType.SEGUNDO_REFORCO
    );
    vaccine = new Vaccine(
      vaccineId, "Hepatite B", 
      VaccineCategory.CARTEIRA_NACIONAL, schedule
    );

    request = new VaccinationRecordRequest();
    request.setVaccineId(vaccineId);
    request.setApplicationDate(LocalDate.now());
    
    mockGridResponse = new VaccinationCardGridDTO();
  }

  // --- TESTES DOS CAMINHOS FELIZES ---

  @Test
  @DisplayName("Deve adicionar um registro de 1ª dose com sucesso")
  void shouldAddFirstDoseSuccessfully() {
    request.setDose(DoseType.PRIMEIRA_DOSE);

    ArgumentCaptor<VaccinationRecord> recordCaptor = ArgumentCaptor.forClass(VaccinationRecord.class);
    
    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccineRepository.findById(vaccineId)).thenReturn(Optional.of(vaccine));
    
    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.PRIMEIRA_DOSE))
        .thenReturn(false);

    when(getVaccinationCardGridUseCase.execute(personId, null))
        .thenReturn(mockGridResponse);

    VaccinationCardGridDTO response = addVaccinationUseCase.execute(personId, request);

    assertNotNull(response);

    verify(personRepository).findById(personId);
    verify(vaccineRepository).findById(vaccineId);
    verify(vaccinationRecordRepository).existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.PRIMEIRA_DOSE);
    verify(vaccinationRecordRepository).save(recordCaptor.capture());
    verify(getVaccinationCardGridUseCase).execute(personId, null);

    VaccinationRecord capturedRecord = recordCaptor.getValue();
    assertNotNull(capturedRecord);
    assertEquals(person, capturedRecord.getPerson());
    assertEquals(vaccine, capturedRecord.getVaccine());
    assertEquals(DoseType.PRIMEIRA_DOSE, capturedRecord.getDose());
    assertEquals(request.getApplicationDate(), capturedRecord.getApplicationDate());
  }

  @Test
  @DisplayName("Deve adicionar 1º Reforço com sucesso se a última dose primária existir")
  void shouldAddBoosterSuccessfullyWhenLastPrimaryDoseExists() {
    request.setDose(DoseType.PRIMEIRO_REFORCO);

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccineRepository.findById(vaccineId)).thenReturn(Optional.of(vaccine));

    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.PRIMEIRO_REFORCO))
        .thenReturn(false);
    
    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.SEGUNDA_DOSE))
        .thenReturn(true);
    
    when(getVaccinationCardGridUseCase.execute(personId, null))
        .thenReturn(mockGridResponse);

    addVaccinationUseCase.execute(personId, request);

    verify(vaccinationRecordRepository, times(1)).save(any(VaccinationRecord.class));
  }

  @Test
  @DisplayName("Deve adicionar 2ª dose com sucesso se a 1ª existir")
  void shouldAddSecondDoseSuccessfully() {
    request.setDose(DoseType.SEGUNDA_DOSE);

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccineRepository.findById(vaccineId)).thenReturn(Optional.of(vaccine));

    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.SEGUNDA_DOSE))
        .thenReturn(false);
    
    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.PRIMEIRA_DOSE))
        .thenReturn(true);
    
    when(getVaccinationCardGridUseCase.execute(personId, null))
        .thenReturn(mockGridResponse);

    addVaccinationUseCase.execute(personId, request);

    verify(vaccinationRecordRepository, times(1)).save(any(VaccinationRecord.class));
  }

  // --- TESTES DOS CAMINHOS TRISTES ---

  @Test
  @DisplayName("Deve lançar BusinessException se a dose não for aplicável")
  void shouldThrowBusinessExceptionWhenDoseIsNotApplicable() {
    request.setDose(DoseType.TERCEIRA_DOSE);

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccineRepository.findById(vaccineId)).thenReturn(Optional.of(vaccine));

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      addVaccinationUseCase.execute(personId, request);
    });

    assertEquals("The requested dose: " + request.getDose().getDescricao() + 
            " is not applicable for the vaccine: " + vaccine.getName() + ".", exception.getMessage());
    verify(vaccinationRecordRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar BusinessException se a dose já estiver registrada")
  void shouldThrowBusinessExceptionWhenDoseIsAlreadyRegistered() {
    request.setDose(DoseType.PRIMEIRA_DOSE);

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccineRepository.findById(vaccineId)).thenReturn(Optional.of(vaccine));

    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.PRIMEIRA_DOSE))
        .thenReturn(true);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      addVaccinationUseCase.execute(personId, request);
    });

    assertEquals("This dose (" + request.getDose().getDescricao() + ") has already been recorded for this person.", exception.getMessage());
    verify(vaccinationRecordRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar BusinessException se 2ª dose for registrada sem a 1ª")
  void shouldThrowBusinessExceptionWhenSecondDoseIsMissingFirst() {
    request.setDose(DoseType.SEGUNDA_DOSE);

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccineRepository.findById(vaccineId)).thenReturn(Optional.of(vaccine));

    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.SEGUNDA_DOSE))
        .thenReturn(false);
    
    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.PRIMEIRA_DOSE))
        .thenReturn(false);
    
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      addVaccinationUseCase.execute(personId, request);
    });

    assertEquals("1st dose is required before registering the 2nd dose.", exception.getMessage());
    verify(vaccinationRecordRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar BusinessException se 1º Reforço for registrado sem a última dose primária")
  void shouldThrowBusinessExceptionWhenBoosterIsMissingLastPrimaryDose() {
    request.setDose(DoseType.PRIMEIRO_REFORCO);

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccineRepository.findById(vaccineId)).thenReturn(Optional.of(vaccine));

    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.PRIMEIRO_REFORCO))
        .thenReturn(false);
    
    when(vaccinationRecordRepository.existsByPersonIdAndVaccineIdAndDose(personId, vaccineId, DoseType.SEGUNDA_DOSE))
        .thenReturn(false);
    
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      addVaccinationUseCase.execute(personId, request);
    });

    assertEquals(DoseType.SEGUNDA_DOSE.getDescricao() + " is required before registering a booster dose.", exception.getMessage());
    verify(vaccinationRecordRepository, never()).save(any());
  }

  // --- TESTES DE EXCEÇÃO 404 ---

  @Test
  @DisplayName("Deve lançar ResourceNotFoundException quando a pessoa não for encontrada")
  void shouldThrowResourceNotFoundExceptionWhenPersonNotFound() {
    request.setDose(DoseType.PRIMEIRA_DOSE);
    when(personRepository.findById(personId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      addVaccinationUseCase.execute(personId, request);
    });

    assertEquals("Person with ID " + personId + " not found.", exception.getMessage());
    verify(vaccineRepository, never()).findById(anyLong());
    verify(vaccinationRecordRepository, never()).save(any());
    verify(getVaccinationCardGridUseCase, never()).execute(anyLong(), any());
  }

  @Test
  @DisplayName("Deve lançar ResourceNotFoundException quando a vacina não for encontrada")
  void shouldThrowResourceNotFoundExceptionWhenVaccineNotFound() {
    request.setDose(DoseType.PRIMEIRA_DOSE);

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccineRepository.findById(vaccineId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      addVaccinationUseCase.execute(personId, request);
    });

    assertEquals("Vaccine with ID " + request.getVaccineId() + " not found.", exception.getMessage());
    verify(personRepository, times(1)).findById(personId);
    verify(vaccinationRecordRepository, never()).save(any());
    verify(getVaccinationCardGridUseCase, never()).execute(anyLong(), any());
  }
  
}
