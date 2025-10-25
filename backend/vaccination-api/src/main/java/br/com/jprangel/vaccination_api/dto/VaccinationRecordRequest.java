package br.com.jprangel.vaccination_api.dto;

import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class VaccinationRecordRequest {
    private Long vaccineId;
    private LocalDate applicationDate;
    private DoseType dose;
}