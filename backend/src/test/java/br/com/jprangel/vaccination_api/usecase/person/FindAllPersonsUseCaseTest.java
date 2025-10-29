package br.com.jprangel.vaccination_api.usecase.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.mapper.PersonMapper;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.model.enuns.Sex;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class FindAllPersonsUseCaseTest {
  
  @Mock
  private PersonRepository personRepository;

  @Mock
  private PersonMapper personMapper;

  @InjectMocks
  private FindAllPersonsUseCase findAllPersonsUseCase;

  private Person person1;
  private Person person2;
  private PersonResponse response1;
  private PersonResponse response2;

  @BeforeEach
  void setUp() {
    LocalDate birthDate1 = LocalDate.of(1990, 1, 1);
    LocalDate birthDate2 = LocalDate.of(1995, 2, 2);

    person1 = new Person(1L, "Ana", "12345678900", birthDate1, Sex.FEMININO, Collections.emptyList());
    person2 = new Person(2L, "Bia", "09876543211", birthDate2, Sex.FEMININO, Collections.emptyList());

    response1 = new PersonResponse(1L, "Ana", "12345678900", birthDate1, Sex.FEMININO);
    response2 = new PersonResponse(2L, "Bia", "09876543211", birthDate2, Sex.FEMININO);
  }

  @Test
  @DisplayName("Deve buscar todas as pessoas com sucesso")
  void shouldFindAllPersonsSuccessfully() {
    Pageable pageable = PageRequest.of(0, 10);
    
    List<Person> personsInDb = List.of(person1, person2);
    Page<Person> mockPersonPage = new PageImpl<>(personsInDb, pageable, personsInDb.size());

    when(personRepository.findAll(pageable)).thenReturn(mockPersonPage);
    
    when(personMapper.toResponse(person1)).thenReturn(response1);
    when(personMapper.toResponse(person2)).thenReturn(response2);

    Page<PersonResponse> resultPage = findAllPersonsUseCase.execute(pageable);

    assertNotNull(resultPage);
    assertEquals(2, resultPage.getTotalElements());
    assertEquals(1, resultPage.getTotalPages());
    
    List<PersonResponse> content = resultPage.getContent();
    assertEquals(2, content.size());
    assertEquals("Ana", content.get(0).getName());
    assertEquals("Bia", content.get(1).getName());
    
    verify(personRepository, times(1)).findAll(pageable);
    verify(personMapper, times(1)).toResponse(person1);
    verify(personMapper, times(1)).toResponse(person2);
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não houver pessoas cadastradas")
  void shouldReturnEmptyListWhenNoPersonsExist() {
    Pageable pageable = PageRequest.of(0, 10);
    
    when(personRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

    Page<PersonResponse> resultPage = findAllPersonsUseCase.execute(pageable);
  
    assertNotNull(resultPage);
    assertTrue(resultPage.isEmpty());
    assertEquals(0, resultPage.getTotalElements());

    verify(personRepository, times(1)).findAll(pageable);
    verify(personMapper, never()).toResponse(any());
  }
}
