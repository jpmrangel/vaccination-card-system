package br.com.jprangel.vaccination_api.usecase.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.mapper.PersonMapper;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.enuns.Sex;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class SearchPersonByCpfUseCaseTest {

  @Mock
  private PersonRepository personRepository;

  @Mock
  private PersonMapper personMapper;
  
  @InjectMocks
  private SearchPersonByCpfUseCase searchPersonByCpfUseCase;
  
  private Person person;
  private PersonResponse expectedResponse;
  private String cpf;

  @BeforeEach
  void setUp() {
    cpf = "12345678900";
    LocalDate birthDate = LocalDate.of(1992, 3, 10);

    person = new Person(
      1L, 
      "Carlos Silva", 
      cpf, 
      birthDate, 
      Sex.MASCULINO, 
      Collections.emptyList()
    );

    expectedResponse = new PersonResponse(
      1L, 
      "Carlos Silva", 
      cpf, 
      birthDate, 
      Sex.MASCULINO
    );
  }
  
  @Test
  @DisplayName("Deve encontrar uma pessoa pelo CPF com sucesso")
  void shouldFindPersonByCpfSuccessfully() {    
    when(personRepository.findByCpf(cpf))
        .thenReturn(Optional.of(person));
    
    when(personMapper.toResponse(person))
        .thenReturn(expectedResponse);

    PersonResponse actualResponse = searchPersonByCpfUseCase.execute(cpf);

    assertNotNull(actualResponse);
    assertEquals(expectedResponse.getId(), actualResponse.getId());
    assertEquals(expectedResponse.getName(), actualResponse.getName());
    assertEquals(expectedResponse.getCpf(), actualResponse.getCpf());

    verify(personRepository, times(1)).findByCpf(cpf);
    verify(personMapper, times(1)).toResponse(person);
  }

  @Test
  @DisplayName("Deve lançar ResourceNotFoundException quando o CPF não for encontrado")
  void shouldThrowResourceNotFoundWhenCpfNotFound() {
    when(personRepository.findByCpf(cpf))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
        searchPersonByCpfUseCase.execute(cpf);
    });

    assertEquals("Person with CPF " + cpf + " not found.", exception.getMessage());

    verify(personRepository, times(1)).findByCpf(cpf);
    verify(personMapper, never()).toResponse(any(Person.class));
  }
}
