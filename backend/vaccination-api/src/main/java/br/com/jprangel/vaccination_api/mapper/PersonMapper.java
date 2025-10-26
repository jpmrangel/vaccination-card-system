package br.com.jprangel.vaccination_api.mapper;

import org.springframework.stereotype.Component;

import br.com.jprangel.vaccination_api.dto.PersonResponse;
import br.com.jprangel.vaccination_api.model.Person;

@Component
public class PersonMapper {
  public PersonResponse toResponse(Person person) {
    if (person == null) {
      return null;
    }
    
    PersonResponse response = new PersonResponse();
    response.setId(person.getId());
    response.setName(person.getName());
    response.setCpf(person.getCpf());
    response.setDateOfBirth(person.getDateOfBirth());
    response.setSex(person.getSex());
    return response;
  }
}
