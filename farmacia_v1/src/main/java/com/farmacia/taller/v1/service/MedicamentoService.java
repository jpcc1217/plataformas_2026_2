package com.farmacia.taller.v1.service;
import com.farmacia.taller.v1.repository.MedicamentoRepository;
import com.farmacia.taller.v1.model.Medicamento;
import com.farmacia.taller.v1.dto.MedicamentoResponseDTO;
import com.farmacia.taller.v1.exception.ResourceNotFoundException;
import com.farmacia.taller.v1.dto.MedicamentoRequestDTO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicamentoService {
    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    public MedicamentoResponseDTO guardarMedicamento(MedicamentoRequestDTO request) {
        
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(request.getNombre());
        medicamento.setPrecio(request.getPrecio());
        medicamento.setCantidad(request.getCantidad());
        medicamento.setFechaExpedicion(request.getFechaExpedicion());
        
        Medicamento guardado = medicamentoRepository.save(medicamento);
        return mapToDTO(guardado);
    }

    public MedicamentoResponseDTO obtenerPorId(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medicamento con ID " + id + " no encontrado"));
        return mapToDTO(medicamento);
    }
    public List<MedicamentoResponseDTO> obtenerTodosLosMedicamentos() {
        return medicamentoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MedicamentoResponseDTO mapToDTO(Medicamento medicamento) {
        String categoriaNombre = (medicamento.getCategoria() != null) ? medicamento.getCategoria().getNombre() : null;
        return new MedicamentoResponseDTO(
            medicamento.getId(),
            medicamento.getNombre(),
            medicamento.getFechaExpedicion(),
            medicamento.getPrecio(),
            medicamento.getCantidad(),
            categoriaNombre
        );
    }
}


