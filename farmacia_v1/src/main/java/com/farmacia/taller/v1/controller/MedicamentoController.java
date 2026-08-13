package com.farmacia.taller.v1.controller;
import com.farmacia.taller.v1.dto.MedicamentoRequestDTO;
import com.farmacia.taller.v1.dto.MedicamentoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.farmacia.taller.v1.service.MedicamentoService;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/v1/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> guardarMedicamento(@Valid @RequestBody MedicamentoRequestDTO request) {
        MedicamentoResponseDTO response = medicamentoService.guardarMedicamento(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return new ResponseEntity<>(medicamentoService.obtenerPorId(id), HttpStatus.OK);
    }
    
    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> obtenerTodosLosMedicamentos() {
        List<MedicamentoResponseDTO> medicamentos = medicamentoService.obtenerTodosLosMedicamentos();
        return new ResponseEntity<>(medicamentos, HttpStatus.OK);
    }
}
