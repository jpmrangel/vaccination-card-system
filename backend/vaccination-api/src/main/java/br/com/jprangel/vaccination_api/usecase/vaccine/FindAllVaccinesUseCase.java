package br.com.jprangel.vaccination_api.usecase.vaccine;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.VaccineDTO;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@Service
public class FindAllVaccinesUseCase {
  
  private final VaccineRepository vaccineRepository;

  public FindAllVaccinesUseCase(VaccineRepository vaccineRepository) {
    this.vaccineRepository = vaccineRepository;
  }

  @Transactional(readOnly = true)
  public List<VaccineDTO> execute() {
    return vaccineRepository.findAll()
            .stream()
            .map(this::mapToVaccineDTO)
            .collect(Collectors.toList());
  }

  private VaccineDTO mapToVaccineDTO(Vaccine vaccine) {
    VaccineDTO response = new VaccineDTO();
    response.setId(vaccine.getId());
    response.setName(vaccine.getName());
    return response;
  }
}
