package com.farmacia.taller.v1.repository;

import com.farmacia.taller.v1.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
}