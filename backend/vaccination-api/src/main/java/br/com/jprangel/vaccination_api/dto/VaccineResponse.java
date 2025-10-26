package br.com.jprangel.vaccination_api.dto;

import java.util.List;

import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import br.com.jprangel.vaccination_api.model.enuns.VaccineCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineResponse {
  private Long id;
  private String name;
  private VaccineCategory category;
  private List<DoseType> doseSchedule;
}