package br.com.jprangel.vaccination_api.usecase.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.RegisterRequest;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.model.User;
import br.com.jprangel.vaccination_api.repository.UserRepository;

@Service
public class RegisterUserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User execute(RegisterRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new BusinessException("Username '" + request.getUsername() + "' already in use.");
    }

    User newUser = new User();
    newUser.setUsername(request.getUsername());
    
    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    
    return userRepository.save(newUser);
  }
}