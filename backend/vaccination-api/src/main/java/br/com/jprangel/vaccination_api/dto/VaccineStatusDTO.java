package br.com.jprangel.vaccination_api.dto;

import java.util.List;

import br.com.jprangel.vaccination_api.model.enuns.VaccineCategory;
import lombok.Data;

@Data
public class VaccineStatusDTO {
  private Long vaccineId;
  private String vaccineName;
  private VaccineCategory category;
  private List<DoseStatusDTO> doses;
}