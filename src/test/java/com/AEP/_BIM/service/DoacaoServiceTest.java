package com.AEP._BIM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.AEP._BIM.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AEP._BIM.dto.*;
import com.AEP._BIM.exception.DoacaoNotFoundException;
import com.AEP._BIM.mapper.DoacaoMapper;
import com.AEP._BIM.model.Doacao;
import com.AEP._BIM.model.StatusDoacao;
import com.AEP._BIM.repository.DoacaoRepository;

@ExtendWith(MockitoExtension.class)
class DoacaoServiceTest {

    @Mock
    DoacaoRepository repository;

    DoacaoService service;

    @BeforeEach
    void setUp() {
        service = new DoacaoService(
                repository,
                new DoacaoMapper()
        );
    }

    private Doacao doacao() {

        return new Doacao(
                "1",
                "Arroz",
                10.0,
                "kg",
                LocalDate.now().plusDays(10),
                "Mercado",
                "9999",
                StatusDoacao.DISPONIVEL
        );
    }

    @Test
    void deveListar() {

        when(repository.findAll())
                .thenReturn(List.of(doacao()));

        assertEquals(
                1,
                service.listar().size()
        );
    }

    @Test
    void deveListarDisponiveis() {

        when(
                repository.findByStatus(
                        StatusDoacao.DISPONIVEL
                )
        ).thenReturn(
                List.of(doacao())
        );

        List<DoacaoSummaryResponse> resultado =
                service.listarDisponiveis();

        assertEquals(1, resultado.size());

        assertEquals(
                StatusDoacao.DISPONIVEL,
                resultado.get(0).status()
        );

        verify(repository)
                .findByStatus(
                        StatusDoacao.DISPONIVEL
                );
    }

    @Test
    void deveBuscar() {

        when(repository.findById("1"))
                .thenReturn(
                        Optional.of(doacao())
                );

        assertEquals(
                "Arroz",
                service.buscarPorId("1").alimento()
        );
    }

    @Test
    void deveCriar() {

        DoacaoCreateRequest req =
                new DoacaoCreateRequest(
                        "Arroz",
                        10.0,
                        "kg",
                        LocalDate.now().plusDays(10),
                        "Mercado",
                        "9999"
                );

        when(repository.save(any()))
                .thenAnswer(invocation -> {

                    Doacao d =
                            invocation.getArgument(0);

                    return new Doacao(
                            "1",
                            d.getAlimento(),
                            d.getQuantidade(),
                            d.getUnidade(),
                            d.getValidade(),
                            d.getDoadorNome(),
                            d.getDoadorContato(),
                            d.getStatus()
                    );
                });

        DoacaoResponse resultado =
                service.criar(req);

        assertEquals(
                "1",
                resultado.id()
        );

        assertEquals(
                StatusDoacao.DISPONIVEL,
                resultado.status()
        );
    }

    @Test
    void deveAtualizar() {

        Doacao d = doacao();

        when(repository.findById("1"))
                .thenReturn(Optional.of(d));

        when(repository.save(d))
                .thenReturn(d);

        DoacaoUpdateRequest req =
                new DoacaoUpdateRequest(
                        "Feijão",
                        5.0,
                        "kg",
                        LocalDate.now().plusDays(5),
                        "Padaria",
                        "8888"
                );

        assertEquals(
                "Feijão",
                service
                        .atualizar("1", req)
                        .alimento()
        );
    }

    @Test
    void deveReservar() {

        Doacao d = doacao();

        when(repository.findById("1"))
                .thenReturn(Optional.of(d));

        when(repository.save(d))
                .thenReturn(d);

        ReservaRequest req =
                new ReservaRequest(
                        "Instituto Esperança",
                        "8888"
                );

        DoacaoResponse resultado =
                service.reservar(
                        "1",
                        req
                );

        assertEquals(
                StatusDoacao.RESERVADA,
                resultado.status()
        );

        assertEquals(
                "Instituto Esperança",
                resultado.reservadoPor()
        );

        assertEquals(
                "8888",
                resultado.contatoReserva()
        );
    }

    @Test
    void naoDeveReservarDoacaoIndisponivel() {

        Doacao d = doacao();

        d.setStatus(
                StatusDoacao.RESERVADA
        );

        when(repository.findById("1"))
                .thenReturn(Optional.of(d));

        ReservaRequest req =
                new ReservaRequest(
                        "Instituto",
                        "8888"
                );

        RuntimeException erro =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                service.reservar(
                                        "1",
                                        req
                                )
                );

        assertEquals(
                "Esta doação não está disponível.",
                erro.getMessage()
        );

        verify(repository, never())
                .save(any());
    }

    @Test
    void deveExcluir() {

        Doacao d = doacao();

        when(repository.findById("1"))
                .thenReturn(Optional.of(d));

        service.excluir("1");

        verify(repository)
                .delete(d);
    }

    @Test
    void deveFalharQuandoNaoEncontrar() {

        when(repository.findById("x"))
                .thenReturn(Optional.empty());

        assertThrows(
                DoacaoNotFoundException.class,
                () ->
                        service.buscarPorId("x")
        );
    }
}