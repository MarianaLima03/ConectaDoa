package com.AEP._BIM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AEP._BIM.dto.DoadorRequest;
import com.AEP._BIM.dto.DoadorResponse;
import com.AEP._BIM.dto.EnderecoDto;
import com.AEP._BIM.exception.DoadorNotFoundException;
import com.AEP._BIM.exception.RegraNegocioException;
import com.AEP._BIM.mapper.DoadorMapper;
import com.AEP._BIM.model.Doador;
import com.AEP._BIM.model.Endereco;
import com.AEP._BIM.repository.DoacaoRepository;
import com.AEP._BIM.repository.DoadorRepository;

@ExtendWith(MockitoExtension.class)
class DoadorServiceTest {

    @Mock
    DoadorRepository repository;

    @Mock
    DoacaoRepository doacaoRepository;

    DoadorService service;

    @BeforeEach
    void setUp() {
        service = new DoadorService(
                repository,
                doacaoRepository,
                new DoadorMapper()
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

    private DoadorRequest request() {

        return new DoadorRequest(
                "Padaria",
                "8888",
                List.of(
                        new EnderecoDto("Rua B", "Maringá", "PR"),
                        new EnderecoDto("Rua C", "Sarandi", "PR")
                )
        );
    }

    @Test
    void deveListar() {

        when(repository.findAll())
                .thenReturn(List.of(doador()));

        assertEquals(1, service.listar().size());
    }

    @Test
    void deveBuscarComEnderecos() {

        when(repository.findById("d1"))
                .thenReturn(Optional.of(doador()));

        DoadorResponse resultado =
                service.buscarPorId("d1");

        assertEquals("Mercado", resultado.nome());
        assertEquals("Rua A", resultado.enderecos().get(0).rua());
    }

    @Test
    void deveCriarComVariosEnderecos() {

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DoadorResponse resultado =
                service.criar(request());

        assertEquals(2, resultado.enderecos().size());
    }

    @Test
    void deveAtualizar() {

        Doador d = doador();

        when(repository.findById("d1"))
                .thenReturn(Optional.of(d));

        when(repository.save(d))
                .thenReturn(d);

        DoadorResponse resultado =
                service.atualizar("d1", request());

        assertEquals("Padaria", resultado.nome());
        assertEquals(2, resultado.enderecos().size());
    }

    @Test
    void deveExcluirDoadorSemDoacoes() {

        Doador d = doador();

        when(repository.findById("d1"))
                .thenReturn(Optional.of(d));

        when(doacaoRepository.existsByDoadorId("d1"))
                .thenReturn(false);

        service.excluir("d1");

        verify(repository).delete(d);
    }

    @Test
    void naoDeveExcluirDoadorComDoacoes() {

        when(repository.findById("d1"))
                .thenReturn(Optional.of(doador()));

        when(doacaoRepository.existsByDoadorId("d1"))
                .thenReturn(true);

        assertThrows(
                RegraNegocioException.class,
                () -> service.excluir("d1")
        );

        verify(repository, never())
                .delete(any());
    }

    @Test
    void deveFalharQuandoNaoEncontrar() {

        when(repository.findById("x"))
                .thenReturn(Optional.empty());

        assertThrows(
                DoadorNotFoundException.class,
                () -> service.buscarPorId("x")
        );
    }
}
