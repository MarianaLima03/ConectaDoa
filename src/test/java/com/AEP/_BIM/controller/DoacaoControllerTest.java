package com.AEP._BIM.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import com.AEP._BIM.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.AEP._BIM.dto.*;
import com.AEP._BIM.exception.DoacaoNotFoundException;
import com.AEP._BIM.model.StatusDoacao;
import com.AEP._BIM.service.DoacaoService;

@WebMvcTest(DoacaoController.class)
class DoacaoControllerTest {

    private static final String BASE = "/api/doacoes";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    DoacaoService service;


    private DoacaoResponse response() {

        return new DoacaoResponse(
                "1",
                "Arroz",
                10.0,
                "kg",
                LocalDate.now().plusDays(10),
                "Mercado",
                "9999",
                StatusDoacao.DISPONIVEL,
                null,
                null
        );
    }


    @Test
    void deveListar() throws Exception {

        DoacaoSummaryResponse resumo =
                new DoacaoSummaryResponse(
                        "1",
                        "Arroz",
                        StatusDoacao.DISPONIVEL
                );

        when(service.listar())
                .thenReturn(List.of(resumo));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].alimento")
                        .value("Arroz"))
                .andExpect(jsonPath("$[0].status")
                        .value("DISPONIVEL"));
    }


    @Test
    void deveListarDisponiveis() throws Exception {

        DoacaoSummaryResponse resumo =
                new DoacaoSummaryResponse(
                        "1",
                        "Arroz",
                        StatusDoacao.DISPONIVEL
                );

        when(service.listarDisponiveis())
                .thenReturn(List.of(resumo));

        mockMvc.perform(
                        get(BASE + "/disponiveis")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].alimento")
                        .value("Arroz"))
                .andExpect(jsonPath("$[0].status")
                        .value("DISPONIVEL"));
    }


    @Test
    void deveBuscar() throws Exception {

        when(service.buscarPorId("1"))
                .thenReturn(response());

        mockMvc.perform(get(BASE + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alimento")
                        .value("Arroz"))
                .andExpect(jsonPath("$.status")
                        .value("DISPONIVEL"));
    }


    @Test
    void deveRetornar404() throws Exception {

        when(service.buscarPorId("x"))
                .thenThrow(
                        new DoacaoNotFoundException("x")
                );

        mockMvc.perform(get(BASE + "/x"))
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Doação não encontrada: x"
                                )
                );
    }


    @Test
    void deveCriar() throws Exception {

        DoacaoCreateRequest req =
                new DoacaoCreateRequest(
                        "Arroz",
                        10.0,
                        "kg",
                        LocalDate.now().plusDays(10),
                        "Mercado",
                        "9999"
                );

        when(service.criar(req))
                .thenReturn(response());

        mockMvc.perform(
                        post(BASE)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(req)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "http://localhost"
                                        + BASE
                                        + "/1"
                        )
                );
    }


    @Test
    void deveRejeitarCriacaoInvalida()
            throws Exception {

        mockMvc.perform(
                        post(BASE)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "alimento": "",
                                          "quantidade": 0
                                        }
                                        """
                                )
                )
                .andExpect(status().isBadRequest());

        verify(service, never())
                .criar(any());
    }


    @Test
    void deveAtualizar() throws Exception {

        DoacaoUpdateRequest req =
                new DoacaoUpdateRequest(
                        "Arroz",
                        10.0,
                        "kg",
                        LocalDate.now().plusDays(10),
                        "Mercado",
                        "9999"
                );

        when(service.atualizar("1", req))
                .thenReturn(response());

        mockMvc.perform(
                        put(BASE + "/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(req)
                                )
                )
                .andExpect(status().isOk());
    }


    @Test
    void deveReservar() throws Exception {

        ReservaRequest req =
                new ReservaRequest(
                        "Instituto Esperança",
                        "8888"
                );

        DoacaoResponse reservada =
                new DoacaoResponse(
                        "1",
                        "Arroz",
                        10.0,
                        "kg",
                        LocalDate.now().plusDays(10),
                        "Mercado",
                        "9999",
                        StatusDoacao.RESERVADA,
                        "Instituto Esperança",
                        "8888"
                );

        when(service.reservar("1", req))
                .thenReturn(reservada);

        mockMvc.perform(
                        put(BASE + "/1/reservar")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(req)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("RESERVADA")
                )
                .andExpect(
                        jsonPath("$.reservadoPor")
                                .value(
                                        "Instituto Esperança"
                                )
                )
                .andExpect(
                        jsonPath("$.contatoReserva")
                                .value("8888")
                );
    }


    @Test
    void deveExcluir() throws Exception {

        doNothing()
                .when(service)
                .excluir("1");

        mockMvc.perform(
                        delete(BASE + "/1")
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(service)
                .excluir("1");
    }
}