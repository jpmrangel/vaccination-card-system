package br.com.jprangel.vaccination_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.jprangel.vaccination_api.model.Vaccine;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
  
  Optional<Vaccine> findByName(String name);
  boolean existsByName(String name);
}
