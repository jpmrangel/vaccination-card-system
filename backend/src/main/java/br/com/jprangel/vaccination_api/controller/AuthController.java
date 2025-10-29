package br.com.jprangel.vaccination_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jprangel.vaccination_api.dto.AuthResponse;
import br.com.jprangel.vaccination_api.dto.LoginRequest;
import br.com.jprangel.vaccination_api.dto.RegisterRequest;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.service.JwtTokenProvider;
import br.com.jprangel.vaccination_api.usecase.auth.RegisterUserUseCase;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final RegisterUserUseCase registerUserUseCase;

  public AuthController(AuthenticationManager authenticationManager, 
                        JwtTokenProvider tokenProvider, 
                        RegisterUserUseCase registerUserUseCase) {
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    this.registerUserUseCase = registerUserUseCase;
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        loginRequest.getUsername(),
        loginRequest.getPassword()
      )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = tokenProvider.generateToken(authentication);

    return ResponseEntity.ok(new AuthResponse(jwt, "Bearer"));
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
    try {
      registerUserUseCase.execute(registerRequest);
      return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
    } catch (BusinessException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao registrar usuário.");
    }
  }
}