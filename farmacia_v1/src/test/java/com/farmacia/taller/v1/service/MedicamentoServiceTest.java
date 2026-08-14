package com.farmacia.taller.v1.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.farmacia.taller.v1.dto.MedicamentoResponseDTO;
import com.farmacia.taller.v1.model.Medicamento;
import com.farmacia.taller.v1.repository.MedicamentoRepository;

import java.util.Optional;
@ExtendWith(MockitoExtension.class)
public class MedicamentoServiceTest {

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @InjectMocks
    private MedicamentoService medicamentoService;

    @Test
    void obtenerPorId_rutaFeliz() {
        //arrange
        Long idTest = 1L;
        Medicamento medicamentoSimulado = new Medicamento();
        medicamentoSimulado.setId(idTest);
        medicamentoSimulado.setNombre("Paracetamol");
        medicamentoSimulado.setPrecio(5.0);

        when(medicamentoRepository.findById(idTest)).thenReturn(Optional.of(medicamentoSimulado));

        //act
        MedicamentoResponseDTO resultado = medicamentoService.obtenerPorId(idTest);


        // Assert
        // assertNotNull(resultado);
        assertEquals(idTest, resultado.getId());
        assertEquals("Paracetamol", resultado.getNombre());
        assertEquals(5.0, resultado.getPrecio());


        //verify
        verify(medicamentoRepository, times(1)).findById(idTest);




        
    }


    
}
