package br.com.jprangel.vaccination_api.usecase.person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.mapper.PersonMapper;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@Service
public class FindAllPersonsUseCase {
  
  private final PersonRepository personRepository;
  private final PersonMapper personMapper;

  public FindAllPersonsUseCase(PersonRepository personRepository, PersonMapper personMapper) {
    this.personRepository = personRepository;
    this.personMapper = personMapper;
  }

  @Transactional(readOnly = true)
  public Page<PersonResponse> execute(Pageable pageable) {
    Page<Person> personPage = personRepository.findAll(pageable);
    return personPage.map(personMapper::toResponse);
  }
}
