package br.com.jprangel.vaccination_api.mapper;

import org.springframework.stereotype.Component;

import br.com.jprangel.vaccination_api.dto.VaccineResponse;
import br.com.jprangel.vaccination_api.model.Vaccine;

@Component
public class VaccineMapper {
  public VaccineResponse toResponse(Vaccine vaccine) {
    if (vaccine == null) {
      return null;
    }

    VaccineResponse response = new VaccineResponse();
    response.setId(vaccine.getId());
    response.setName(vaccine.getName());
    response.setCategory(vaccine.getCategory());
    response.setDoseSchedule(vaccine.getDoseSchedule());
    return response;
  }
}
