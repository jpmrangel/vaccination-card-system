package br.com.jprangel.vaccination_api.usecase.vaccine;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.VaccineDTO;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@Service
public class CreateVaccineUseCase {
  
  private final VaccineRepository vaccineRepository;

  public CreateVaccineUseCase(VaccineRepository vaccineRepository) {
    this.vaccineRepository = vaccineRepository;
  }

  @Transactional
  public VaccineDTO execute(VaccineDTO request) {

    if (vaccineRepository.existsByName(request.getName())) {
      throw new BusinessException("Vaccine with name " + request.getName() + " already exists.");
    }

    Vaccine vaccine = new Vaccine();
    vaccine.setName(request.getName());

    Vaccine savedVaccine = vaccineRepository.save(vaccine);
    return mapToVaccineDTO(savedVaccine);
  }

  private VaccineDTO mapToVaccineDTO(Vaccine vaccine) {
    VaccineDTO response = new VaccineDTO();
    response.setId(vaccine.getId());
    response.setName(vaccine.getName());
    return response;
  }
}
