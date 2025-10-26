package br.com.jprangel.vaccination_api.dto;

import java.util.List;

import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import br.com.jprangel.vaccination_api.model.enuns.VaccineCategory;
import lombok.Data;

@Data
public class VaccineRequest {
  private String name;
  private VaccineCategory category;
  private List<DoseType> doseSchedule;
}
