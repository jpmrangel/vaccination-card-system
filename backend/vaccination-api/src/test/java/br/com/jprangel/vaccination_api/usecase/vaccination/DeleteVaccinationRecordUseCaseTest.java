package br.com.jprangel.vaccination_api.usecase.vaccination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.repository.VaccinationRecordRepository;

@ExtendWith(MockitoExtension.class)
public class DeleteVaccinationRecordUseCaseTest {
  
  @Mock
  private VaccinationRecordRepository vaccinationRecordRepository;

  @InjectMocks
  private DeleteVaccinationRecordUseCase deleteVaccinationRecordUseCase;

  @Test
  @DisplayName("Deve deletar um registro de vacinação com sucesso")
  void shouldDeleteVaccinationRecordSuccessfully() {
    Long recordId = 1L;

    when(vaccinationRecordRepository.existsById(recordId))
        .thenReturn(true);
    
    deleteVaccinationRecordUseCase.execute(recordId);

    verify(vaccinationRecordRepository).existsById(recordId);
    verify(vaccinationRecordRepository).deleteById(recordId);
  }

  @Test
  @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar um registro de vacinação inexistente")
  void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentRecord() {
    Long recordId = 1L;

    when(vaccinationRecordRepository.existsById(recordId))
        .thenReturn(false);

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      deleteVaccinationRecordUseCase.execute(recordId);
    });

    assertEquals("Vaccination record with ID " + recordId + " not found.", exception.getMessage());

    verify(vaccinationRecordRepository).existsById(recordId);
    verify(vaccinationRecordRepository, never()).deleteById(recordId);
  }
}
