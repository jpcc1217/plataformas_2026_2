package com.farmacia.taller.v1.service;
import com.farmacia.taller.v1.repository.MedicamentoRepository;
import com.farmacia.taller.v1.model.Medicamento;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MedicamentoService {
    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    public Medicamento guardarMedicamento(Medicamento medicamento)
     {
        
        if (medicamento.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio del medicamento debe ser mayor que cero");
        }

        if (medicamento.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad del medicamento no puede ser negativa");
        }

        if (medicamento.getFechaExpedicion() == null) {
            throw new IllegalArgumentException("La fecha de expedición del medicamento no puede ser nula");
        }


        
        return medicamentoRepository.save(medicamento);
    }

    public List<Medicamento> obtenerTodosLosMedicamentos() {
        return medicamentoRepository.findAll();
    }



}
