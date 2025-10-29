package br.com.jprangel.vaccination_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jprangel.vaccination_api.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
  Optional<User> findByUsername(String username);
}
