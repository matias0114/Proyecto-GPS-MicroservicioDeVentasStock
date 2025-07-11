
package com.ProyectoGPS.Backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ProyectoGPS.Backend.dto.PacienteDTO;
import com.ProyectoGPS.Backend.model.Paciente;
import com.ProyectoGPS.Backend.repository.PacienteRepository;
import com.ProyectoGPS.Backend.service.PacienteService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository repo;

    @InjectMocks
    private PacienteService service;

    private Paciente entidad;
    private PacienteDTO dto;

    @BeforeEach
    void setUp() {
        entidad = new Paciente();
        entidad.setId(1L);
        entidad.setNombre("Juan");
        entidad.setApellido("Pérez");
        entidad.setDni("12345678");
        entidad.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        entidad.setDireccion("Calle Falsa 123");
        entidad.setTelefono("555-1234");

        dto = new PacienteDTO();
        dto.setId(1L);
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setDni("12345678");
        dto.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        dto.setDireccion("Calle Falsa 123");
        dto.setTelefono("555-1234");
    }

    @Test
    void findAll_debeDevolverListaDeDTOS() {
        when(repo.findAll()).thenReturn(List.of(entidad));

        List<PacienteDTO> resultado = service.findAll();

        assertThat(resultado)
            .hasSize(1)
            .first()
            .satisfies(d -> {
                assertThat(d.getId()).isEqualTo(1L);
                assertThat(d.getNombre()).isEqualTo("Juan");
                assertThat(d.getDni()).isEqualTo("12345678");
            });

        verify(repo).findAll();
    }

    @Test
    void findById_existente_retornaOptionalConDTO() {
        when(repo.findById(1L)).thenReturn(Optional.of(entidad));

        Optional<PacienteDTO> opt = service.findById(1L);

        assertThat(opt).isPresent();
        assertThat(opt.get().getApellido()).isEqualTo("Pérez");
        verify(repo).findById(1L);
    }

    @Test
    void findByDni_noExistente_retornaOptionalVacio() {
        when(repo.findByDni("0000")).thenReturn(Optional.empty());

        assertThat(service.findByDni("0000")).isEmpty();
        verify(repo).findByDni("0000");
    }

    @Test
    void save_deberiaGuardarEntidadYRetornarDTO() {
        when(repo.save(any(Paciente.class))).thenReturn(entidad);

        PacienteDTO guardado = service.save(dto);

        assertThat(guardado.getId()).isEqualTo(1L);
        assertThat(guardado.getDireccion()).isEqualTo("Calle Falsa 123");
        verify(repo).save(any(Paciente.class));
    }

    @Test
    void deleteById_debeInvocarRepositorio() {
        doNothing().when(repo).deleteById(1L);

        service.deleteById(1L);

        verify(repo).deleteById(1L);
    }
}