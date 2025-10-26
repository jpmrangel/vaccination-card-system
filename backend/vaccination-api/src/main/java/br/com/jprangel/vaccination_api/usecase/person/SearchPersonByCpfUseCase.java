package br.com.jprangel.vaccination_api.usecase.person;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.mapper.PersonMapper;
import br.com.jprangel.vaccination_api.model.Person;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@Service
public class SearchPersonByCpfUseCase {
  
  private final PersonRepository personRepository;
  private final PersonMapper personMapper;

  public SearchPersonByCpfUseCase(PersonRepository personRepository, PersonMapper personMapper) {
    this.personRepository = personRepository;
    this.personMapper = personMapper;
  }

  @Transactional(readOnly = true)
  public PersonResponse execute(String cpf) {
    Person person = personRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Person with CPF " + cpf + " not found."));
    
    return personMapper.toResponse(person);
  }
}
