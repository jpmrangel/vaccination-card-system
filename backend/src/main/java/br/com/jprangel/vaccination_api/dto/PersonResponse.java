package br.com.jprangel.vaccination_api.dto;

import java.time.LocalDate;

import br.com.jprangel.vaccination_api.model.enuns.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {
  private Long id;
  private String name;
  private String cpf;
  private LocalDate dateOfBirth;
  private Sex sex;
}