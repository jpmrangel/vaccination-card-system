package br.com.jprangel.vaccination_api.usecase.vaccine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.VaccineRequest;
import br.com.jprangel.vaccination_api.dto.VaccineResponse;
import br.com.jprangel.vaccination_api.exception.BusinessException;
import br.com.jprangel.vaccination_api.mapper.VaccineMapper;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import br.com.jprangel.vaccination_api.model.enuns.VaccineCategory;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@ExtendWith(MockitoExtension.class)
public class CreateVaccineUseCaseTest {

  @Mock
  private VaccineRepository vaccineRepository;

  @Mock
  private VaccineMapper vaccineMapper;

  @InjectMocks
  private CreateVaccineUseCase createVaccineUseCase;

  private VaccineRequest request;
  private Vaccine savedVaccine;
  private VaccineResponse expectedResponse;

  @BeforeEach
  void setUp() {
    List<DoseType> schedule = List.of(DoseType.PRIMEIRA_DOSE);
    
    request = new VaccineRequest();
    request.setName("BCG");
    request.setCategory(VaccineCategory.CARTEIRA_NACIONAL);
    request.setDoseSchedule(schedule);

    savedVaccine = new Vaccine(
      1L, 
      "BCG", 
      VaccineCategory.CARTEIRA_NACIONAL, 
      schedule
    );

    expectedResponse = new VaccineResponse(
      1L, 
      "BCG", 
      VaccineCategory.CARTEIRA_NACIONAL, 
      schedule
    );
  }
  
  @Test
  @DisplayName("Deve criar uma vacina com sucesso")
  void shouldCreateVaccineSuccessfully() {
    ArgumentCaptor<Vaccine> vaccineCaptor = ArgumentCaptor.forClass(Vaccine.class);

    when(vaccineRepository.existsByName("BCG")).thenReturn(false);
    
    when(vaccineRepository.save(vaccineCaptor.capture())).thenReturn(savedVaccine);
    
    when(vaccineMapper.toResponse(savedVaccine)).thenReturn(expectedResponse);
    
    VaccineResponse response = createVaccineUseCase.execute(request);

    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals("BCG", response.getName());
    assertEquals(VaccineCategory.CARTEIRA_NACIONAL, response.getCategory());

    Vaccine capturedVaccine = vaccineCaptor.getValue();
    assertEquals("BCG", capturedVaccine.getName());
    assertEquals(VaccineCategory.CARTEIRA_NACIONAL, capturedVaccine.getCategory());
    assertEquals(1, capturedVaccine.getDoseSchedule().size());

    verify(vaccineRepository, times(1)).existsByName("BCG");
    verify(vaccineRepository, times(1)).save(any(Vaccine.class));
    verify(vaccineMapper, times(1)).toResponse(savedVaccine);
  }
  
  @Test
  @DisplayName("Deve lançar BusinessException ao tentar criar vacina com nome já existente")
  void shouldThrowBusinessExceptionWhenVaccineNameAlreadyExists() {
    when(vaccineRepository.existsByName("BCG")).thenReturn(true);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      createVaccineUseCase.execute(request);
    });

    assertEquals("Vaccine with name " + request.getName() + " already exists.", exception.getMessage());
  
    verify(vaccineRepository, times(1)).existsByName("BCG");
    verify(vaccineRepository, never()).save(any(Vaccine.class));
    verify(vaccineMapper, never()).toResponse(any(Vaccine.class));
  }
  
}
