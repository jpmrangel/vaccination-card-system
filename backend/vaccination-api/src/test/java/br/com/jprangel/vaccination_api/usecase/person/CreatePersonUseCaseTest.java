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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.PersonRequest;
import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.mapper.PersonMapper;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.enuns.Sex;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class CreatePersonUseCaseTest {

  @Mock
  private PersonRepository personRepository;

  @Mock
  private PersonMapper personMapper;

  @InjectMocks
  private CreatePersonUseCase createPersonUseCase;

  private PersonRequest request;
  private Person savedPerson;
  private PersonResponse expectedResponse;
  private LocalDate birthDate;

  @BeforeEach
  void setUp() {
    birthDate = LocalDate.of(1990, 5, 15);

    request = new PersonRequest();
    request.setName("João Teste");
    request.setCpf("12345678900");
    request.setDateOfBirth(birthDate);
    request.setSex(Sex.MASCULINO);

    savedPerson = new Person(
      1L, 
      "João Teste", 
      "12345678900", 
      birthDate, 
      Sex.MASCULINO, 
      null
    );

    expectedResponse = new PersonResponse(
      1L, 
      "João Teste", 
      "12345678900", 
      birthDate, 
      Sex.MASCULINO
    );
  }

  @Test
  @DisplayName("Deve criar uma pessoa com sucesso")
  void shouldCreatePersonSuccessfully() {
    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);

    when(personRepository.existsByCpf("12345678900"))
        .thenReturn(false);

    when(personRepository.save(personCaptor.capture()))
        .thenReturn(savedPerson);
    
    when(personMapper.toResponse(savedPerson))
        .thenReturn(expectedResponse);

    PersonResponse response = createPersonUseCase.execute(request);

    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals("João Teste", response.getName());
    assertEquals("12345678900", response.getCpf());
    assertEquals(birthDate, response.getDateOfBirth());
    assertEquals(Sex.MASCULINO, response.getSex());
    
    Person capturedPerson = personCaptor.getValue();
    assertNotNull(capturedPerson);
    assertEquals("João Teste", capturedPerson.getName());
    assertEquals("12345678900", capturedPerson.getCpf());
    assertEquals(birthDate, capturedPerson.getDateOfBirth());
    assertEquals(Sex.MASCULINO, capturedPerson.getSex());

    verify(personRepository, times(1)).existsByCpf("12345678900");
    verify(personRepository, times(1)).save(any(Person.class));
    verify(personMapper, times(1)).toResponse(savedPerson);
  }

  @Test
  @DisplayName("Deve lançar BusinessException quando CPF já existir")
  void shouldThrowBusinessExceptionWhenCpfAlreadyExists() {
    when(personRepository.existsByCpf(request.getCpf()))
      .thenReturn(true);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      createPersonUseCase.execute(request);
    });

    assertEquals("Person with CPF " + request.getCpf() + " already exists.", exception.getMessage());

    verify(personRepository, times(1)).existsByCpf(request.getCpf());
    verify(personRepository, never()).save(any(Person.class));
    verify(personMapper, never()).toResponse(any(Person.class));
  }
}
