package com.AEP._BIM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AEP._BIM.dto.*;
import com.AEP._BIM.exception.DoacaoNotFoundException;
import com.AEP._BIM.exception.DoadorNotFoundException;
import com.AEP._BIM.exception.InstituicaoNotFoundException;
import com.AEP._BIM.exception.RegraNegocioException;
import com.AEP._BIM.mapper.DoacaoMapper;
import com.AEP._BIM.model.Doacao;
import com.AEP._BIM.model.Doador;
import com.AEP._BIM.model.Endereco;
import com.AEP._BIM.model.Instituicao;
import com.AEP._BIM.model.StatusDoacao;
import com.AEP._BIM.repository.DoacaoRepository;
import com.AEP._BIM.repository.DoadorRepository;
import com.AEP._BIM.repository.InstituicaoRepository;

@ExtendWith(MockitoExtension.class)
class DoacaoServiceTest {

    @Mock
    DoacaoRepository repository;

    @Mock
    DoadorRepository doadorRepository;

    @Mock
    InstituicaoRepository instituicaoRepository;

    DoacaoService service;

    @BeforeEach
    void setUp() {
        service = new DoacaoService(
                repository,
                doadorRepository,
                instituicaoRepository,
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
                "d1",
                StatusDoacao.DISPONIVEL
        );
    }

    private Doador doador() {

        return new Doador(
                "d1",
                "Mercado",
                "9999",
                List.of(new Endereco("Rua A", "Maringá", "PR"))
        );
    }

    private Instituicao instituicao() {

        return new Instituicao(
                "i1",
                "Instituto Esperança",
                "8888",
                new Endereco("Rua B", "Maringá", "PR")
        );
    }

    private DoacaoRequest request() {

        return new DoacaoRequest(
                "Feijão",
                5.0,
                "kg",
                LocalDate.now().plusDays(5),
                "d1"
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
    }

    @Test
    void deveListarPorDoador() {

        when(doadorRepository.findById("d1"))
                .thenReturn(Optional.of(doador()));

        when(repository.findByDoadorId("d1"))
                .thenReturn(List.of(doacao()));

        assertEquals(
                1,
                service.listarPorDoador("d1").size()
        );
    }

    @Test
    void naoDeveListarPorDoadorInexistente() {

        when(doadorRepository.findById("x"))
                .thenReturn(Optional.empty());

        assertThrows(
                DoadorNotFoundException.class,
                () -> service.listarPorDoador("x")
        );
    }

    @Test
    void deveBuscarComDadosDoDoador() {

        when(repository.findById("1"))
                .thenReturn(Optional.of(doacao()));

        when(doadorRepository.findById("d1"))
                .thenReturn(Optional.of(doador()));

        DoacaoResponse resultado =
                service.buscarPorId("1");

        assertEquals("Arroz", resultado.alimento());
        assertEquals("Mercado", resultado.doadorNome());
        assertEquals("9999", resultado.doadorContato());
        assertNull(resultado.reserva());
    }

    @Test
    void deveCriar() {

        when(doadorRepository.findById("d1"))
                .thenReturn(Optional.of(doador()));

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DoacaoResponse resultado =
                service.criar(request());

        assertEquals(
                StatusDoacao.DISPONIVEL,
                resultado.status()
        );

        assertEquals("Mercado", resultado.doadorNome());
    }

    @Test
    void naoDeveCriarComDoadorInexistente() {

        when(doadorRepository.findById("d1"))
                .thenReturn(Optional.empty());

        assertThrows(
                DoadorNotFoundException.class,
                () -> service.criar(request())
        );

        verify(repository, never())
                .save(any());
    }

    @Test
    void deveAtualizar() {

        Doacao d = doacao();

        when(repository.findById("1"))
                .thenReturn(Optional.of(d));

        when(doadorRepository.findById("d1"))
                .thenReturn(Optional.of(doador()));

        when(repository.save(d))
                .thenReturn(d);

        assertEquals(
                "Feijão",
                service
                        .atualizar("1", request())
                        .alimento()
        );
    }

    @Test
    void deveReservar() {

        Doacao d = doacao();

        when(repository.findById("1"))
                .thenReturn(Optional.of(d));

        when(doadorRepository.findById("d1"))
                .thenReturn(Optional.of(doador()));

        when(instituicaoRepository.findById("i1"))
                .thenReturn(Optional.of(instituicao()));

        when(repository.save(d))
                .thenReturn(d);

        DoacaoResponse resultado =
                service.reservar(
                        "1",
                        new ReservaRequest("i1")
                );

        assertEquals(
                StatusDoacao.RESERVADA,
                resultado.status()
        );

        assertEquals(
                "Instituto Esperança",
                resultado.reserva().instituicaoNome()
        );

        assertEquals(
                LocalDate.now(),
                resultado.reserva().dataReserva()
        );
    }

    @Test
    void naoDeveReservarComInstituicaoInexistente() {

        when(repository.findById("1"))
                .thenReturn(Optional.of(doacao()));

        when(instituicaoRepository.findById("x"))
                .thenReturn(Optional.empty());

        ReservaRequest req = new ReservaRequest("x");

        assertThrows(
                InstituicaoNotFoundException.class,
                () -> service.reservar("1", req)
        );

        verify(repository, never())
                .save(any());
    }

    @Test
    void naoDeveReservarDoacaoIndisponivel() {

        Doacao d = doacao();

        d.reservar("i1");

        when(repository.findById("1"))
                .thenReturn(Optional.of(d));

        when(instituicaoRepository.findById("i2"))
                .thenReturn(Optional.of(instituicao()));

        ReservaRequest req = new ReservaRequest("i2");

        RegraNegocioException erro =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.reservar("1", req)
                );

        assertEquals(
                "Esta doação não está disponível.",
                erro.getMessage()
        );

        verify(repository, never())
                .save(any());
    }

    @Test
    void naoDeveReservarDoacaoVencida() {

        Doacao d = doacao();

        d.setValidade(LocalDate.now().minusDays(1));

        when(repository.findById("1"))
                .thenReturn(Optional.of(d));

        when(instituicaoRepository.findById("i1"))
                .thenReturn(Optional.of(instituicao()));

        ReservaRequest req = new ReservaRequest("i1");

        RegraNegocioException erro =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.reservar("1", req)
                );

        assertEquals(
                "Esta doação está vencida.",
                erro.getMessage()
        );
    }

    @Test
    void deveRegistrarRetirada() {

        Doacao d = doacao();

        d.reservar("i1");

        when(repository.findById("1"))
                .thenReturn(Optional.of(d));

        when(doadorRepository.findById("d1"))
                .thenReturn(Optional.of(doador()));

        when(instituicaoRepository.findById("i1"))
                .thenReturn(Optional.of(instituicao()));

        when(repository.save(d))
                .thenReturn(d);

        DoacaoResponse resultado =
                service.registrarRetirada("1");

        assertEquals(
                StatusDoacao.RETIRADA,
                resultado.status()
        );

        assertEquals(
                LocalDate.now(),
                resultado.reserva().dataRetirada()
        );
    }

    @Test
    void naoDeveRetirarSemReserva() {

        when(repository.findById("1"))
                .thenReturn(Optional.of(doacao()));

        assertThrows(
                RegraNegocioException.class,
                () -> service.registrarRetirada("1")
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
