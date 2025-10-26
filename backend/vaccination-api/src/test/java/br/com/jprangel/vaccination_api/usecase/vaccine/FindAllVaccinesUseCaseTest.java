package br.com.jprangel.vaccination_api.usecase.vaccine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jprangel.vaccination_api.dto.VaccineDTO;
import br.com.jprangel.vaccination_api.model.Vaccine;
import br.com.jprangel.vaccination_api.repository.VaccineRepository;

@ExtendWith(MockitoExtension.class)
public class FindAllVaccinesUseCaseTest {

  @Mock
  private VaccineRepository vaccineRepository;

  @InjectMocks
  private FindAllVaccinesUseCase findAllVaccinesUseCase;

  @Test
  @DisplayName("Deve buscar todas as vacinas com sucesso")
  void shouldFindAllVaccinesSuccessfully() {
    List<Vaccine> vaccinesInDb = List.of(
        new Vaccine(1L, "Vacina A"),
        new Vaccine(2L, "Vacina B")
    );

    List<VaccineDTO> expectedResponse = List.of(
        new VaccineDTO(1L, "Vacina A"),
        new VaccineDTO(2L, "Vacina B")
    );

    when(vaccineRepository.findAll()).thenReturn(vaccinesInDb); 

    List<VaccineDTO> response = findAllVaccinesUseCase.execute();

    assertNotNull(response);
    assertEquals(expectedResponse.size(), response.size());

    for (int i = 0; i < expectedResponse.size(); i++) {
      assertEquals(expectedResponse.get(i).getId(), response.get(i).getId());
      assertEquals(expectedResponse.get(i).getName(), response.get(i).getName());
    }

    verify(vaccineRepository).findAll();
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não houver vacinas cadastradas")
  void shouldReturnEmptyListWhenNoVaccinesExist() {
    when(vaccineRepository.findAll()).thenReturn(List.of());

    List<VaccineDTO> response = findAllVaccinesUseCase.execute();

    assertTrue(response.isEmpty());
  }
}
