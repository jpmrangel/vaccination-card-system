package br.com.jprangel.vaccination_api.usecase.vaccination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.VaccinationCardResponse;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.VaccinationRecord;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import br.com.jprangel.vaccination_api.repository.PersonRepository;
import br.com.jprangel.vaccination_api.repository.VaccinationRecordRepository;

@ExtendWith(MockitoExtension.class)
public class GetVaccinationCardUseCaseTest {
  
  @Mock
  private PersonRepository personRepository;

  @Mock
  private VaccinationRecordRepository vaccinationRecordRepository;

  @InjectMocks
  private GetVaccinationCardUseCase getVaccinationCardUseCase;

  @Test
  @DisplayName("Deve retornar o cartão de vacinação com registros")
  void shouldReturnVaccinationCardWithRecords() {
    Long personId = 1L;
    Person person = new Person(personId, "João Teste", "12345678900", List.of());
    Vaccine vaccine1 = new Vaccine(1L, "BCG");
    Vaccine vaccine2 = new Vaccine(2L, "Hepatite B");
    
    List<VaccinationRecord> records = List.of(
      new VaccinationRecord(
        10L, 
        LocalDate.of(2023, 1, 1), 
        DoseType.DOSE_UNICA, 
        person, 
        vaccine1
      ),
      new VaccinationRecord(
        11L, 
        LocalDate.of(2023, 1, 1), 
        DoseType.DOSE_UNICA, 
        person, 
        vaccine2
      )
    );

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccinationRecordRepository.findByPersonId(personId)).thenReturn(records);

    VaccinationCardResponse response = getVaccinationCardUseCase.execute(personId);

    assertNotNull(response);
    assertEquals(personId, response.getPersonId());
    assertEquals("João Teste", response.getPersonName());
    assertEquals("12345678900", response.getPersonCpf());
    
    assertNotNull(response.getRecords());
    assertEquals(2, response.getRecords().size());
    
    for (int i = 0; i < response.getRecords().size(); i++) {
      VaccinationCardResponse.VaccinationRecordInfo info = response.getRecords().get(i);

      VaccinationRecord record = records.get(i);
      assertEquals(record.getId(), info.getRecordId());
      assertEquals(record.getVaccine().getName(), info.getVaccineName());
      assertEquals(record.getDose(), info.getDose());
      assertEquals(record.getApplicationDate(), info.getApplicationDate());
    }

    verify(personRepository).findById(personId);
    verify(vaccinationRecordRepository).findByPersonId(personId);
  }

  @Test
  @DisplayName("Deve retornar o cartão de vacinação vazio quando não houver registros")
  void shouldReturnEmptyVaccinationCardWhenNoRecords() {
    Long personId = 2L;
    Person person = new Person(personId, "Maria Teste", "09876543211", List.of());

    when(personRepository.findById(personId)).thenReturn(Optional.of(person));
    when(vaccinationRecordRepository.findByPersonId(personId)).thenReturn(List.of());
    
    VaccinationCardResponse response = getVaccinationCardUseCase.execute(personId);

    assertNotNull(response);
    assertEquals(personId, response.getPersonId());
    assertEquals("Maria Teste", response.getPersonName());
    assertEquals("09876543211", response.getPersonCpf());
    assertNotNull(response.getRecords());
    assertEquals(0, response.getRecords().size());

    verify(personRepository).findById(personId);
    verify(vaccinationRecordRepository).findByPersonId(personId);
  }

  @Test
  @DisplayName("Deve lançar ResourceNotFoundException quando a pessoa não for encontrada")
  void shouldThrowResourceNotFoundExceptionWhenPersonNotFound() {
    Long personId = 3L;

    when(personRepository.findById(personId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      getVaccinationCardUseCase.execute(personId);
    });

    assertEquals("Person with ID " + personId + " not found.", exception.getMessage());

    verify(personRepository).findById(personId);
    verify(vaccinationRecordRepository, never()).findByPersonId(personId);
  }
}
