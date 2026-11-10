package com.AEP._BIM.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.AEP._BIM.dto.*;
import com.AEP._BIM.exception.DoacaoNotFoundException;
import com.AEP._BIM.exception.RegraNegocioException;
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


    private DoacaoResponse response(StatusDoacao status, ReservaResponse reserva) {

        return new DoacaoResponse(
                "1",
                "Arroz",
                10.0,
                "kg",
                LocalDate.now().plusDays(10),
                "d1",
                "Mercado",
                "9999",
                status,
                reserva
        );
    }

    private DoacaoResponse response() {
        return response(StatusDoacao.DISPONIVEL, null);
    }

    private DoacaoRequest request() {

        return new DoacaoRequest(
                "Arroz",
                10.0,
                "kg",
                LocalDate.now().plusDays(10),
                "d1"
        );
    }

    private ReservaResponse reserva(LocalDate dataRetirada) {

        return new ReservaResponse(
                "i1",
                "Instituto Esperança",
                "8888",
                LocalDate.now(),
                dataRetirada
        );
    }

    private DoacaoSummaryResponse resumo() {

        return new DoacaoSummaryResponse(
                "1",
                "Arroz",
                StatusDoacao.DISPONIVEL
        );
    }


    @Test
    void deveListar() throws Exception {

        when(service.listar())
                .thenReturn(List.of(resumo()));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].alimento")
                        .value("Arroz"));
    }


    @Test
    void deveListarDisponiveis() throws Exception {

        when(service.listarDisponiveis())
                .thenReturn(List.of(resumo()));

        mockMvc.perform(
                        get(BASE + "/disponiveis")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status")
                        .value("DISPONIVEL"));
    }


    @Test
    void deveListarPorDoador() throws Exception {

        when(service.listarPorDoador("d1"))
                .thenReturn(List.of(resumo()));

        mockMvc.perform(
                        get(BASE + "/doador/d1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }


    @Test
    void deveBuscar() throws Exception {

        when(service.buscarPorId("1"))
                .thenReturn(response());

        mockMvc.perform(get(BASE + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alimento")
                        .value("Arroz"))
                .andExpect(jsonPath("$.doadorNome")
                        .value("Mercado"));
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

        DoacaoRequest req = request();

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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.doadorId")
                        .value("doadorId é obrigatório"));

        verify(service, never())
                .criar(any());
    }


    @Test
    void deveRejeitarCorpoInvalido()
            throws Exception {

        mockMvc.perform(
                        post(BASE)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("{ isso não é json")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Corpo da requisição inválido"));
    }


    @Test
    void deveAtualizar() throws Exception {

        DoacaoRequest req = request();

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
                new ReservaRequest("i1");

        when(service.reservar("1", req))
                .thenReturn(response(StatusDoacao.RESERVADA, reserva(null)));

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
                        jsonPath("$.reserva.instituicaoNome")
                                .value(
                                        "Instituto Esperança"
                                )
                );
    }


    @Test
    void deveRetornar409AoReservarIndisponivel() throws Exception {

        ReservaRequest req =
                new ReservaRequest("i1");

        when(service.reservar("1", req))
                .thenThrow(new RegraNegocioException(
                        "Esta doação não está disponível."
                ));

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
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Esta doação não está disponível."));
    }


    @Test
    void deveRejeitarReservaSemInstituicao() throws Exception {

        mockMvc.perform(
                        put(BASE + "/1/reservar")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(service, never())
                .reservar(any(), any());
    }


    @Test
    void deveRetirar() throws Exception {

        when(service.registrarRetirada("1"))
                .thenReturn(response(
                        StatusDoacao.RETIRADA,
                        reserva(LocalDate.now())
                ));

        mockMvc.perform(
                        put(BASE + "/1/retirar")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("RETIRADA"));
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
