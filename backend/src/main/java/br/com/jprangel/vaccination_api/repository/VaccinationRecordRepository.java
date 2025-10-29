package br.com.jprangel.vaccination_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.jprangel.vaccination_api.model.VaccinationRecord;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
  
  List<VaccinationRecord> findByPersonId(Long personId);
  boolean existsByPersonIdAndVaccineIdAndDose(Long personId, Long vaccineId, DoseType dose);
}
