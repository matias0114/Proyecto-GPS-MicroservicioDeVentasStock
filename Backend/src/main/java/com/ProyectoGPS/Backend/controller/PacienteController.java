package com.ProyectoGPS.Backend.controller;

import com.ProyectoGPS.Backend.dto.PacienteDTO;
import com.ProyectoGPS.Backend.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;            // <— Asegúrate de esto
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // dentro de PacienteController
@PostMapping
    public ResponseEntity<PacienteDTO> crearPaciente(@RequestBody PacienteDTO pacienteDTO) {
        if (pacienteService.findByDni(pacienteDTO.getDni()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(pacienteService.save(pacienteDTO));
    }

    // 2. Listar todos los pacientes (READ - collection)
    @GetMapping
    public List<PacienteDTO> listarPacientes() {
        return pacienteService.findAll();
    }

    // 3. Buscar paciente por DNI (READ - individual)
    @GetMapping("/{dni}")
    public ResponseEntity<PacienteDTO> obtenerPacientePorDni(@PathVariable String dni) {
        return pacienteService.findByDni(dni)
                              .map(ResponseEntity::ok)
                              .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4. Bulk upload de pacientes (JSON array)
    @PostMapping("/upload")
    public ResponseEntity<List<PacienteDTO>> uploadPacientes(
            @RequestBody List<PacienteDTO> pacientesDto) {

        List<PacienteDTO> guardados = pacientesDto.stream()
                                                  .map(pacienteService::save)
                                                  .collect(Collectors.toList());

        return ResponseEntity.ok(guardados);
    }
}
