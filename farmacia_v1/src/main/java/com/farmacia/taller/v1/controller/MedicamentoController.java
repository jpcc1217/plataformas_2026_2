package com.farmacia.taller.v1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.farmacia.taller.v1.model.Medicamento;
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
    public ResponseEntity<Medicamento> guardarMedicamento(@RequestBody Medicamento medicamento) {
        Medicamento medicamentoGuardado = medicamentoService.guardarMedicamento(medicamento);
        return new ResponseEntity<>(medicamentoGuardado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Medicamento>> obtenerTodosLosMedicamentos() {
        List<Medicamento> medicamentos = medicamentoService.obtenerTodosLosMedicamentos();
        return new ResponseEntity<>(medicamentos, HttpStatus.OK);
    }


    

}
