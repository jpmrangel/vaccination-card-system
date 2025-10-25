package br.com.jprangel.vaccination_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.jprangel.vaccination_api.dto.PersonRequest;
import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.usecase.person.CreatePersonUseCase;
import br.com.jprangel.vaccination_api.usecase.person.DeletePersonUseCase;
import br.com.jprangel.vaccination_api.usecase.person.FindAllPersonsUseCase;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping ("/api/persons")
public class PersonController {
  
  private final CreatePersonUseCase createPersonUseCase;
  private final DeletePersonUseCase deletePersonUseCase;
  private final FindAllPersonsUseCase findAllPersonsUseCase;

  public PersonController(CreatePersonUseCase createPersonUseCase,
                          DeletePersonUseCase deletePersonUseCase,
                          FindAllPersonsUseCase findAllPersonsUseCase) {
    this.createPersonUseCase = createPersonUseCase;
    this.deletePersonUseCase = deletePersonUseCase;
    this.findAllPersonsUseCase = findAllPersonsUseCase;
  }

  @PostMapping
  public ResponseEntity<PersonResponse> createPerson(@RequestBody PersonRequest request) {
    PersonResponse response = createPersonUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<PersonResponse>> getAllPersons() {
    List<PersonResponse> response = findAllPersonsUseCase.execute();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePerson(@PathVariable Long id) {
    deletePersonUseCase.execute(id);
  }

  
}
