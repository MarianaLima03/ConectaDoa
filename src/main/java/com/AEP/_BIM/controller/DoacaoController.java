package com.AEP._BIM.controller;

import java.net.URI;
import java.util.List;

import com.AEP._BIM.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.AEP._BIM.service.DoacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doacoes")
public class DoacaoController {

    private final DoacaoService service;

    public DoacaoController(DoacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<DoacaoSummaryResponse> listar() {
        return service.listar();
    }

    @GetMapping("/disponiveis")
    public List<DoacaoSummaryResponse> listarDisponiveis() {
        return service.listarDisponiveis();
    }

    @GetMapping("/doador/{doadorId}")
    public List<DoacaoSummaryResponse> listarPorDoador(@PathVariable String doadorId) {
        return service.listarPorDoador(doadorId);
    }

    @GetMapping("/{id}")
    public DoacaoResponse buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<DoacaoResponse> criar(@Valid @RequestBody DoacaoRequest request) {
        DoacaoResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public DoacaoResponse atualizar(@PathVariable String id, @Valid @RequestBody DoacaoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reservar")
    public DoacaoResponse reservar(
            @PathVariable String id,
            @Valid @RequestBody ReservaRequest request) {

        return service.reservar(id, request);
    }

    @PutMapping("/{id}/retirar")
    public DoacaoResponse retirar(@PathVariable String id) {
        return service.registrarRetirada(id);
    }
}
