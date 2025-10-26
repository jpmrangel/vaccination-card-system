package br.com.jprangel.vaccination_api.dto;

import java.util.List;

import lombok.Data;

@Data
public class VaccinationCardGridDTO {
  private PersonResponse person;
  private List<VaccineStatusDTO> vaccines;
}
