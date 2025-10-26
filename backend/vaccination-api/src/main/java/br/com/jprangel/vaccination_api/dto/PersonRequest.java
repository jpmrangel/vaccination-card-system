package br.com.jprangel.vaccination_api.dto;

import java.time.LocalDate;

import br.com.jprangel.vaccination_api.model.enuns.Sex;
import lombok.Data;

@Data
public class PersonRequest {
  private String name;
  private String cpf;
  private LocalDate dateOfBirth;
  private Sex sex;
}
