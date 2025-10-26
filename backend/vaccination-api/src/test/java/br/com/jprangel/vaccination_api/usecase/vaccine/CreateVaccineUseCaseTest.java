package br.com.jprangel.vaccination_api.usecase.vaccine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.VaccineDTO;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@ExtendWith(MockitoExtension.class)
public class CreateVaccineUseCaseTest {

  @Mock
  private VaccineRepository vaccineRepository;

  @InjectMocks
  private CreateVaccineUseCase createVaccineUseCase;
  
  @Test
  @DisplayName("Deve criar uma vacina com sucesso")
  void shouldCreateVaccineSuccessfully() {
    VaccineDTO request = new VaccineDTO();
    request.setName("COVID-19");
    
    Vaccine savedVaccine = new Vaccine();
    savedVaccine.setId(1L);
    savedVaccine.setName("COVID-19");

    when(vaccineRepository.existsByName("COVID-19")).thenReturn(false);
    when(vaccineRepository.save(any(Vaccine.class))).thenReturn(savedVaccine);
    
    VaccineDTO response = createVaccineUseCase.execute(request);

    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals("COVID-19", response.getName());

    verify(vaccineRepository).existsByName("COVID-19");
    verify(vaccineRepository).save(any(Vaccine.class));
  }
  
  @Test
  @DisplayName("Deve lançar BusinessException ao tentar criar vacina com nome já existente")
  void shouldThrowBusinessExceptionWhenVaccineNameAlreadyExists() {
    VaccineDTO request = new VaccineDTO();
    request.setName("COVID-19");

    when(vaccineRepository.existsByName("COVID-19")).thenReturn(true);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      createVaccineUseCase.execute(request);
    });

    assertEquals("Vaccine with name " + request.getName() + " already exists.", exception.getMessage());
  }
  
}
