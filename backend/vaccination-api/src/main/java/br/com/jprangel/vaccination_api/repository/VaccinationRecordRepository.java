package br.com.jprangel.vaccination_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.jprangel.vaccination_api.model.VaccinationRecord;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
  
  List<VaccinationRecord> findByPersonId(Long personId);
}
