package br.com.jprangel.vaccination_api.dto;

import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class VaccinationCardResponse {
    
    private Long personId;
    private String personName;
    private String personCpf;
    private List<VaccinationRecordInfo> records;

    @Data
    public static class VaccinationRecordInfo {
        private Long recordId;
        private String vaccineName;
        private DoseType dose;
        private LocalDate applicationDate;
    }
}