package br.com.jprangel.vaccination_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.jprangel.vaccination_api.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
  
  Optional<Person> findByCpf(String cpf);
  boolean existsByCpf(String cpf);
}
