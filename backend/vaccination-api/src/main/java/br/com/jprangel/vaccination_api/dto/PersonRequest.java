package br.com.jprangel.vaccination_api.dto;

import lombok.Data;

@Data
public class PersonRequest {
    private String name;
    private String cpf;
}
