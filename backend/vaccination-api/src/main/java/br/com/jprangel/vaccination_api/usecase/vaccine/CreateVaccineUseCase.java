package br.com.jprangel.vaccination_api.usecase.vaccine;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jprangel.vaccination_api.dto.VaccineRequest;
import br.com.jprangel.vaccination_api.dto.VaccineResponse;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.mapper.VaccineMapper;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@Service
public class CreateVaccineUseCase {
  
  private final VaccineRepository vaccineRepository;
  private final VaccineMapper vaccineMapper;

  public CreateVaccineUseCase(VaccineRepository vaccineRepository, VaccineMapper vaccineMapper) {
    this.vaccineRepository = vaccineRepository;
    this.vaccineMapper = vaccineMapper;
  }

  @Transactional
  public VaccineResponse execute(VaccineRequest request) {
    if (vaccineRepository.existsByName(request.getName())) {
      throw new BusinessException("Vaccine with name " + request.getName() + " already exists.");
    }

    Vaccine vaccine = new Vaccine();
    vaccine.setName(request.getName());
    vaccine.setCategory(request.getCategory());
    vaccine.setDoseSchedule(request.getDoseSchedule());

    Vaccine savedVaccine = vaccineRepository.save(vaccine);
    return vaccineMapper.toResponse(savedVaccine);
  }
}
