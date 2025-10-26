package br.com.jprangel.vaccination_api.usecase.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class FindAllPersonsUseCaseTest {
  
  @Mock
  private PersonRepository personRepository;

  @InjectMocks
  private FindAllPersonsUseCase findAllPersonsUseCase;

  @Test
  @DisplayName("Deve buscar todas as pessoas com sucesso")
  void shouldFindAllPersonsSuccessfully() {
    List<Person> personsInDb = List.of(
        new Person(1L, "Ana", "12345678900", List.of()),
        new Person(2L, "Bia", "09876543211", List.of())
    );

    List<PersonResponse> expectedResponses = List.of(
        new PersonResponse(1L, "Ana", "12345678900"),
        new PersonResponse(2L, "Bia", "09876543211")
    );

    when(personRepository.findAll()).thenReturn(personsInDb);
    
    List<PersonResponse> response = findAllPersonsUseCase.execute();

    assertNotNull(response);
    assertEquals(expectedResponses.size(), response.size());

    for (int i = 0; i < expectedResponses.size(); i++) {
      assertEquals(expectedResponses.get(i).getId(), response.get(i).getId());
      assertEquals(expectedResponses.get(i).getName(), response.get(i).getName());
      assertEquals(expectedResponses.get(i).getCpf(), response.get(i).getCpf());
    }
    
    verify(personRepository).findAll();
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não houver pessoas cadastradas")
  void shouldReturnEmptyListWhenNoPersonsExist() {
    when(personRepository.findAll()).thenReturn(List.of());

    List<PersonResponse> response = findAllPersonsUseCase.execute();
  
    assertTrue(response.isEmpty());
  }
}
