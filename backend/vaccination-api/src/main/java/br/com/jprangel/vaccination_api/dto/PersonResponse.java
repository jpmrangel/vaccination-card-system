package br.com.jprangel.vaccination_api.dto;

import lombok.Data;

@Data
public class PersonResponse {
    private Long id;
    private String name;
    private String cpf;
}