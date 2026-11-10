package com.AEP._BIM.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.AEP._BIM.dto.InstituicaoRequest;
import com.AEP._BIM.dto.InstituicaoResponse;
import com.AEP._BIM.service.InstituicaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/instituicoes")
public class InstituicaoController {

    private final InstituicaoService service;

    public InstituicaoController(InstituicaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<InstituicaoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public InstituicaoResponse buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<InstituicaoResponse> criar(@Valid @RequestBody InstituicaoRequest request) {
        InstituicaoResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public InstituicaoResponse atualizar(@PathVariable String id, @Valid @RequestBody InstituicaoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
