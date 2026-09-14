package com.farmacia.taller.v1.service;
import com.farmacia.taller.v1.repository.MedicamentoRepository;
import com.farmacia.taller.v1.repository.CategoriaRepository;
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
    private final CategoriaRepository categoriaRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository, CategoriaRepository categoriaRepository) {
        this.medicamentoRepository = medicamentoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public MedicamentoResponseDTO guardarMedicamento(MedicamentoRequestDTO request) {
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(request.getNombre());
        medicamento.setPrecio(request.getPrecio());
        medicamento.setCantidad(request.getCantidad());
        medicamento.setFechaExpiracion(request.getFechaExpiracion());
        
        if (request.getCategoriaId() != null) {
            categoriaRepository.findById(request.getCategoriaId())
                .ifPresent(medicamento::setCategoria);
        }
        
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

    public MedicamentoResponseDTO actualizarMedicamento(Long id, MedicamentoRequestDTO request) {
        Medicamento medicamento = medicamentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medicamento con ID " + id + " no encontrado"));

        medicamento.setNombre(request.getNombre());
        medicamento.setPrecio(request.getPrecio());
        medicamento.setCantidad(request.getCantidad());
        medicamento.setFechaExpiracion(request.getFechaExpiracion());

        if (request.getCategoriaId() != null) {
            categoriaRepository.findById(request.getCategoriaId())
                .ifPresent(medicamento::setCategoria);
        }

        Medicamento actualizado = medicamentoRepository.save(medicamento);
        return mapToDTO(actualizado);
    }

    public void eliminarMedicamento(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medicamento con ID " + id + " no encontrado"));
        medicamentoRepository.delete(medicamento);
    }

    private MedicamentoResponseDTO mapToDTO(Medicamento medicamento) {
        String categoriaNombre = (medicamento.getCategoria() != null) ? medicamento.getCategoria().getNombre() : null;
        return new MedicamentoResponseDTO(
            medicamento.getId(),
            medicamento.getNombre(),
            medicamento.getFechaExpiracion(),
            medicamento.getPrecio(),
            medicamento.getCantidad(),
            categoriaNombre
        );
    }
}


