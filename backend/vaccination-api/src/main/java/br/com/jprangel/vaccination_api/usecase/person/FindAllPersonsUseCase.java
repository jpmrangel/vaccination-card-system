package br.com.jprangel.vaccination_api.usecase.person;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@Service
public class FindAllPersonsUseCase {
  
  private final PersonRepository personRepository;

  public FindAllPersonsUseCase(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  @Transactional(readOnly = true)
  public List<PersonResponse> execute() {
    return personRepository.findAll()
            .stream()
            .map(this::mapToPersonResponse)
            .collect(Collectors.toList());
  }

  private PersonResponse mapToPersonResponse(Person person) {
    PersonResponse response = new PersonResponse();
    response.setId(person.getId());
    response.setName(person.getName());
    response.setCpf(person.getCpf());
    return response;
  }
}
