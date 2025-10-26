package br.com.jprangel.vaccination_api.dto;

import java.time.LocalDate;

import br.com.jprangel.vaccination_api.model.enuns.DoseStatus;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoseStatusDTO {
  private DoseType doseType;
  private DoseStatus status;
  private Long recordId;
  private LocalDate applicationDate;
}
