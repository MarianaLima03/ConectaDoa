package com.AEP._BIM.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.AEP._BIM.dto.DoadorRequest;
import com.AEP._BIM.dto.DoadorResponse;
import com.AEP._BIM.service.DoadorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doadores")
public class DoadorController {

    private final DoadorService service;

    public DoadorController(DoadorService service) {
        this.service = service;
    }

    @GetMapping
    public List<DoadorResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public DoadorResponse buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<DoadorResponse> criar(@Valid @RequestBody DoadorRequest request) {
        DoadorResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public DoadorResponse atualizar(@PathVariable String id, @Valid @RequestBody DoadorRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
