package br.com.jprangel.vaccination_api.usecase.person;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.PersonRequest;
import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@Service
public class CreatePersonUseCase {

  private final PersonRepository personRepository;

  public CreatePersonUseCase(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  @Transactional
  public PersonResponse execute(PersonRequest request) {

    if (personRepository.existsByCpf(request.getCpf())) {
      throw new BusinessException("Person with CPF " + request.getCpf() + " already exists.");
    }

    Person person = new Person();
    person.setName(request.getName());
    person.setCpf(request.getCpf());

    Person savedPerson = personRepository.save(person);

    return mapToPersonResponse(savedPerson);
  }

  private PersonResponse mapToPersonResponse(Person person) {
    PersonResponse response = new PersonResponse();
    response.setId(person.getId());
    response.setName(person.getName());
    response.setCpf(person.getCpf());
    return response;
  }
}
