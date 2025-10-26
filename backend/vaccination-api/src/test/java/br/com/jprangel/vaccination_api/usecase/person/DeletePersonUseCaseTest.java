package br.com.jprangel.vaccination_api.usecase.person;

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
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class DeletePersonUseCaseTest {
  
  @Mock
  private PersonRepository personRepository;

  @InjectMocks
  private DeletePersonUseCase deletePersonUseCase;

  @Test
  @DisplayName("Deve deletar uma pessoa com sucesso")
  void shouldDeletePersonSuccessfully() {
    Long personId = 1L;

    when(personRepository.existsById(personId))
        .thenReturn(true);

    deletePersonUseCase.execute(personId);

    verify(personRepository).existsById(personId);
    verify(personRepository).deleteById(personId);
  }

  @Test
  @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar uma pessoa inexistente")
  void shouldThrowResourceNotFoundExceptionWhenPersonDoesNotExist() {
    Long personId = 1L;

    when(personRepository.existsById(personId))
        .thenReturn(false);

    var exception = assertThrows(ResourceNotFoundException.class, () -> {
      deletePersonUseCase.execute(personId);
    });

    assertEquals("Person with ID " + personId + " not found.", exception.getMessage());

    verify(personRepository).existsById(personId);
    verify(personRepository, never()).deleteById(personId);
  }
}
