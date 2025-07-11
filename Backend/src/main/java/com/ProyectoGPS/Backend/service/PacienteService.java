package com.ProyectoGPS.Backend.service;

import com.ProyectoGPS.Backend.dto.PacienteDTO;
import com.ProyectoGPS.Backend.model.Paciente;
import com.ProyectoGPS.Backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PacienteService {
    @Autowired
    private PacienteRepository pacienteRepository;

    public List<PacienteDTO> findAll() {
        return pacienteRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<PacienteDTO> findById(Long id) {
        return pacienteRepository.findById(id).map(this::toDTO);
    }

    public Optional<PacienteDTO> findByDni(String dni) {
        return pacienteRepository.findByDni(dni).map(this::toDTO);
    }

    public PacienteDTO save(PacienteDTO dto) {
        Paciente paciente = toEntity(dto);
        Paciente saved = pacienteRepository.save(paciente);
        return toDTO(saved);
    }

    public void deleteById(Long id) {
        pacienteRepository.deleteById(id);
    }

    // Métodos de conversión
    private PacienteDTO toDTO(Paciente paciente) {
        PacienteDTO dto = new PacienteDTO();
        dto.setId(paciente.getId());
        dto.setNombre(paciente.getNombre());
        dto.setApellido(paciente.getApellido());
        dto.setDni(paciente.getDni());
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        dto.setDireccion(paciente.getDireccion());
        dto.setTelefono(paciente.getTelefono());
        return dto;
    }

    private Paciente toEntity(PacienteDTO dto) {
        Paciente paciente = new Paciente();
        paciente.setId(dto.getId());
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setDni(dto.getDni());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setDireccion(dto.getDireccion());
        paciente.setTelefono(dto.getTelefono());
        return paciente;
    }
}
