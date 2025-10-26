package br.com.jprangel.vaccination_api.usecase.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.PersonRequest;
import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class CreatePersonUseCaseTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private CreatePersonUseCase createPersonUseCase;

    @Test
    @DisplayName("Deve criar uma pessoa com sucesso")
    void shouldCreatePersonSuccessfully() {
      PersonRequest request = new PersonRequest();
      request.setName("João Teste");
      request.setCpf("12345678900");

      Person savedPerson = new Person();
      savedPerson.setId(1L);
      savedPerson.setName("João Teste");
      savedPerson.setCpf("12345678900");

      when(personRepository.existsByCpf("12345678900"))
          .thenReturn(false);

      when(personRepository.save(any(Person.class)))
          .thenReturn(savedPerson);

      PersonResponse response = createPersonUseCase.execute(request);

      assertNotNull(response);
      assertEquals(1L, response.getId());
      assertEquals("João Teste", response.getName());
      assertEquals("12345678900", response.getCpf());

      verify(personRepository).existsByCpf("12345678900");
      verify(personRepository).save(any(Person.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando CPF já existir")
    void shouldThrowBusinessExceptionWhenCpfAlreadyExists() {
      PersonRequest request = new PersonRequest();
      request.setName("Maria Teste");
      request.setCpf("11122233344");

      when(personRepository.existsByCpf("11122233344"))
          .thenReturn(true);

      BusinessException exception = assertThrows(BusinessException.class, () -> {
          createPersonUseCase.execute(request);
      });

      assertEquals("Person with CPF " + request.getCpf() + " already exists.", exception.getMessage());

      verify(personRepository).existsByCpf("11122233344");
      verify(personRepository, never()).save(any(Person.class));
    }
}
