package com.AEP._BIM.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.AEP._BIM.dto.EnderecoDto;
import com.AEP._BIM.dto.InstituicaoRequest;
import com.AEP._BIM.dto.InstituicaoResponse;
import com.AEP._BIM.exception.InstituicaoNotFoundException;
import com.AEP._BIM.service.InstituicaoService;

@WebMvcTest(InstituicaoController.class)
class InstituicaoControllerTest {

    private static final String BASE = "/api/instituicoes";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    InstituicaoService service;

    private final EnderecoDto endereco =
            new EnderecoDto("Rua A", "Maringá", "PR");

    private InstituicaoResponse response() {
        return new InstituicaoResponse("i1", "Instituto", "8888", endereco);
    }

    private InstituicaoRequest request() {
        return new InstituicaoRequest("Instituto", "8888", endereco);
    }

    @Test
    void deveListar() throws Exception {

        when(service.listar())
                .thenReturn(List.of(response()));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].endereco.cidade")
                        .value("Maringá"));
    }

    @Test
    void deveBuscar() throws Exception {

        when(service.buscarPorId("i1"))
                .thenReturn(response());

        mockMvc.perform(get(BASE + "/i1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Instituto"));
    }

    @Test
    void deveRetornar404() throws Exception {

        when(service.buscarPorId("x"))
                .thenThrow(new InstituicaoNotFoundException("x"));

        mockMvc.perform(get(BASE + "/x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveCriar() throws Exception {

        InstituicaoRequest req = request();

        when(service.criar(req))
                .thenReturn(response());

        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void deveRejeitarInstituicaoSemEndereco() throws Exception {

        InstituicaoRequest req =
                new InstituicaoRequest("Instituto", "8888", null);

        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isBadRequest());

        verify(service, never())
                .criar(any());
    }

    @Test
    void deveAtualizar() throws Exception {

        InstituicaoRequest req = request();

        when(service.atualizar("i1", req))
                .thenReturn(response());

        mockMvc.perform(
                        put(BASE + "/i1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk());
    }

    @Test
    void deveExcluir() throws Exception {

        mockMvc.perform(delete(BASE + "/i1"))
                .andExpect(status().isNoContent());

        verify(service).excluir("i1");
    }
}
