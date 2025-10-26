package br.com.jprangel.vaccination_api.usecase.vaccine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.VaccineResponse;
import br.com.jprangel.vaccination_api.mapper.VaccineMapper;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.model.enuns.DoseType;
import br.com.jprangel.vaccination_api.model.enuns.VaccineCategory;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@ExtendWith(MockitoExtension.class)
public class FindAllVaccinesUseCaseTest {

  @Mock
  private VaccineRepository vaccineRepository;

  @Mock
  private VaccineMapper vaccineMapper;

  @InjectMocks
  private FindAllVaccinesUseCase findAllVaccinesUseCase;

  private Vaccine vaccine1;
  private Vaccine vaccine2;
  private VaccineResponse response1;
  private VaccineResponse response2;

  @BeforeEach
  void setUp() {
    List<DoseType> schedule1 = List.of(DoseType.PRIMEIRA_DOSE);
    List<DoseType> schedule2 = List.of(DoseType.PRIMEIRA_DOSE, DoseType.SEGUNDA_DOSE);

    vaccine1 = new Vaccine(1L, "Vacina A", VaccineCategory.CARTEIRA_NACIONAL, schedule1);
    vaccine2 = new Vaccine(2L, "Vacina B", VaccineCategory.OUTRA_VACINA, schedule2);

    response1 = new VaccineResponse(1L, "Vacina A", VaccineCategory.CARTEIRA_NACIONAL, schedule1);
    response2 = new VaccineResponse(2L, "Vacina B", VaccineCategory.OUTRA_VACINA, schedule2);
  }

  @Test
  @DisplayName("Deve buscar todas as vacinas com sucesso")
  void shouldFindAllVaccinesSuccessfully() {
    List<Vaccine> vaccinesInDb = List.of(vaccine1, vaccine2);

    when(vaccineRepository.findAll()).thenReturn(vaccinesInDb); 
    when(vaccineMapper.toResponse(vaccine1)).thenReturn(response1);
    when(vaccineMapper.toResponse(vaccine2)).thenReturn(response2);

    List<VaccineResponse> response = findAllVaccinesUseCase.execute();

    assertNotNull(response);
    assertEquals(2, response.size());

    assertEquals("Vacina A", response.get(0).getName());
    assertEquals(VaccineCategory.CARTEIRA_NACIONAL, response.get(0).getCategory());
    
    assertEquals("Vacina B", response.get(1).getName());
    assertEquals(VaccineCategory.OUTRA_VACINA, response.get(1).getCategory());

    verify(vaccineRepository, times(1)).findAll();
    verify(vaccineMapper, times(1)).toResponse(vaccine1);
    verify(vaccineMapper, times(1)).toResponse(vaccine2);
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não houver vacinas cadastradas")
  void shouldReturnEmptyListWhenNoVaccinesExist() {
    when(vaccineRepository.findAll()).thenReturn(Collections.emptyList());

    List<VaccineResponse> response = findAllVaccinesUseCase.execute();

    assertNotNull(response);
    assertTrue(response.isEmpty());

    verify(vaccineRepository, times(1)).findAll();
    verify(vaccineMapper, never()).toResponse(any(Vaccine.class));
  }
}
