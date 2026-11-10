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

import com.AEP._BIM.dto.DoadorRequest;
import com.AEP._BIM.dto.DoadorResponse;
import com.AEP._BIM.dto.EnderecoDto;
import com.AEP._BIM.exception.DoadorNotFoundException;
import com.AEP._BIM.exception.RegraNegocioException;
import com.AEP._BIM.service.DoadorService;

@WebMvcTest(DoadorController.class)
class DoadorControllerTest {

    private static final String BASE = "/api/doadores";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    DoadorService service;

    private final List<EnderecoDto> enderecos =
            List.of(new EnderecoDto("Rua A", "Maringá", "PR"));

    private DoadorResponse response() {
        return new DoadorResponse("d1", "Mercado", "9999", enderecos);
    }

    private DoadorRequest request() {
        return new DoadorRequest("Mercado", "9999", enderecos);
    }

    @Test
    void deveListar() throws Exception {

        when(service.listar())
                .thenReturn(List.of(response()));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].enderecos[0].cidade")
                        .value("Maringá"));
    }

    @Test
    void deveBuscar() throws Exception {

        when(service.buscarPorId("d1"))
                .thenReturn(response());

        mockMvc.perform(get(BASE + "/d1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Mercado"));
    }

    @Test
    void deveRetornar404() throws Exception {

        when(service.buscarPorId("x"))
                .thenThrow(new DoadorNotFoundException("x"));

        mockMvc.perform(get(BASE + "/x"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Doador não encontrado: x"));
    }

    @Test
    void deveCriar() throws Exception {

        DoadorRequest req = request();

        when(service.criar(req))
                .thenReturn(response());

        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost" + BASE + "/d1"
                ));
    }

    @Test
    void deveRejeitarDoadorSemEndereco() throws Exception {

        DoadorRequest req =
                new DoadorRequest("Mercado", "9999", List.of());

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
    void deveRejeitarEnderecoIncompleto() throws Exception {

        DoadorRequest req = new DoadorRequest(
                "Mercado",
                "9999",
                List.of(new EnderecoDto("", "Maringá", "PR"))
        );

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

        DoadorRequest req = request();

        when(service.atualizar("d1", req))
                .thenReturn(response());

        mockMvc.perform(
                        put(BASE + "/d1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk());
    }

    @Test
    void deveExcluir() throws Exception {

        mockMvc.perform(delete(BASE + "/d1"))
                .andExpect(status().isNoContent());

        verify(service).excluir("d1");
    }

    @Test
    void deveRetornar409AoExcluirComDoacoes() throws Exception {

        doThrow(new RegraNegocioException("Este doador possui doações cadastradas."))
                .when(service)
                .excluir("d1");

        mockMvc.perform(delete(BASE + "/d1"))
                .andExpect(status().isConflict());
    }
}
