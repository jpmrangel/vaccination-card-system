package br.com.jprangel.vaccination_api.usecase.person;

import org.springframework.stereotype.Service;

import br.com.jprangel.vaccination_api.exception.ResourceNotFoundException;
import br.com.jprangel.vaccination_api.repository.PersonRepository;

@Service
public class DeletePersonUseCase {
  
  private final PersonRepository personRepository;

  public DeletePersonUseCase(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  public void execute(Long personId) {
    
    if (!personRepository.existsById(personId)) {
      throw new ResourceNotFoundException("Person with ID " + personId + " not found.");
      
    }
    personRepository.deleteById(personId);
  }
}
