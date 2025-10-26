package br.com.jprangel.vaccination_api.usecase.vaccine;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.VaccineResponse;
import br.com.jprangel.vaccination_api.mapper.VaccineMapper;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@Service
public class FindAllVaccinesUseCase {
  
  private final VaccineRepository vaccineRepository;
  private final VaccineMapper vaccineMapper;

  public FindAllVaccinesUseCase(VaccineRepository vaccineRepository, VaccineMapper vaccineMapper) {
    this.vaccineRepository = vaccineRepository;
    this.vaccineMapper = vaccineMapper;
  }

  @Transactional(readOnly = true)
  public List<VaccineResponse> execute() {
    return vaccineRepository.findAll()
            .stream()
            .map(vaccineMapper::toResponse)
            .collect(Collectors.toList());
  }
}
