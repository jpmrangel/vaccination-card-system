package br.com.jprangel.vaccination_api.usecase.vaccination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.VaccinationCardResponse;
import br.com.jprangel.vaccination_api.dto.VaccinationRecordRequest;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.VaccinationRecord;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;
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
  private GetVaccinationCardUseCase getVaccinationCardUseCase;

  @InjectMocks
  private AddVaccinationUseCase addVaccinationUseCase;
  
  @Test
  @DisplayName("Deve adicionar um registro de vacinação com sucesso")
  void shouldAddVaccinationRecordSuccessfully() {
    Long personId = 1L;
    LocalDate applicationDate = LocalDate.of(2024, 6, 15);

    Person person = new Person(1L, "Maria", "98765432100", List.of());
    Vaccine vaccine = new Vaccine(1L, "Hepatite B");
    
    VaccinationRecordRequest request = new VaccinationRecordRequest();
    request.setVaccineId(1L);
    request.setDose(DoseType.PRIMEIRA_DOSE);
    request.setApplicationDate(applicationDate);

    VaccinationCardResponse mockCardResponse = new VaccinationCardResponse();
    mockCardResponse.setPersonId(personId);

    ArgumentCaptor<VaccinationRecord> recordCaptor = ArgumentCaptor.forClass(VaccinationRecord.class);
    
    when(personRepository.findById(personId))
        .thenReturn(Optional.of(person));
    
    when(vaccineRepository.findById(1L))
        .thenReturn(Optional.of(vaccine));
    
    when(getVaccinationCardUseCase.execute(personId))
        .thenReturn(mockCardResponse);

    VaccinationCardResponse response = addVaccinationUseCase.execute(personId, request);

    assertNotNull(response);
    assertEquals(personId, response.getPersonId());

    verify(personRepository).findById(personId);
    verify(vaccineRepository).findById(1L);
    verify(getVaccinationCardUseCase).execute(personId);

    verify(vaccinationRecordRepository).save(recordCaptor.capture());
    VaccinationRecord capturedRecord = recordCaptor.getValue();

    assertNotNull(capturedRecord);
    assertEquals(person, capturedRecord.getPerson());
    assertEquals(vaccine, capturedRecord.getVaccine());
    assertEquals(DoseType.PRIMEIRA_DOSE, capturedRecord.getDose());
    assertEquals(applicationDate, capturedRecord.getApplicationDate());
  }

  @Test
  @DisplayName("Deve lançar ResourceNotFoundException quando a pessoa não for encontrada")
  void shouldThrowResourceNotFoundExceptionWhenPersonNotFound() {
    Long personId = 1L;

    VaccinationRecordRequest request = new VaccinationRecordRequest();
    request.setVaccineId(1L);
    request.setDose(DoseType.PRIMEIRA_DOSE);
    request.setApplicationDate(LocalDate.now());

    when(personRepository.findById(personId))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      addVaccinationUseCase.execute(personId, request);
    });

    assertEquals("Person with ID " + personId + " not found.", exception.getMessage());

    verify(personRepository).findById(personId);
    verify(vaccineRepository, never()).findById(any(Long.class));
    verify(vaccinationRecordRepository, never()).save(any(VaccinationRecord.class));
    verify(getVaccinationCardUseCase, never()).execute(any(Long.class));
  }

  @Test
  @DisplayName("Deve lançar ResourceNotFoundException quando a vacina não for encontrada")
  void shouldThrowResourceNotFoundExceptionWhenVaccineNotFound() {
    Long personId = 1L;
    Person person = new Person(1L, "Maria", "98765432100", List.of());
    
    VaccinationRecordRequest request = new VaccinationRecordRequest();
    request.setVaccineId(1L);
    request.setDose(DoseType.PRIMEIRA_DOSE);
    request.setApplicationDate(LocalDate.now());

    when(personRepository.findById(personId))
        .thenReturn(Optional.of(person));

    when(vaccineRepository.findById(1L))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      addVaccinationUseCase.execute(personId, request);
    });

    assertEquals("Vaccine with ID " + request.getVaccineId() + " not found.", exception.getMessage());

    verify(personRepository).findById(personId);
    verify(vaccineRepository).findById(1L);
    verify(vaccinationRecordRepository, never()).save(any(VaccinationRecord.class));
    verify(getVaccinationCardUseCase, never()).execute(any(Long.class));
  }
}
