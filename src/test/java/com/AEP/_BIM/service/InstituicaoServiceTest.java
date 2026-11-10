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

import com.AEP._BIM.dto.EnderecoDto;
import com.AEP._BIM.dto.InstituicaoRequest;
import com.AEP._BIM.dto.InstituicaoResponse;
import com.AEP._BIM.exception.InstituicaoNotFoundException;
import com.AEP._BIM.exception.RegraNegocioException;
import com.AEP._BIM.mapper.InstituicaoMapper;
import com.AEP._BIM.model.Endereco;
import com.AEP._BIM.model.Instituicao;
import com.AEP._BIM.repository.DoacaoRepository;
import com.AEP._BIM.repository.InstituicaoRepository;

@ExtendWith(MockitoExtension.class)
class InstituicaoServiceTest {

    @Mock
    InstituicaoRepository repository;

    @Mock
    DoacaoRepository doacaoRepository;

    InstituicaoService service;

    @BeforeEach
    void setUp() {
        service = new InstituicaoService(
                repository,
                doacaoRepository,
                new InstituicaoMapper()
        );
    }

    private Instituicao instituicao() {

        return new Instituicao(
                "i1",
                "Instituto Esperança",
                "8888",
                new Endereco("Rua A", "Maringá", "PR")
        );
    }

    private InstituicaoRequest request() {

        return new InstituicaoRequest(
                "Lar Feliz",
                "7777",
                new EnderecoDto("Rua B", "Sarandi", "PR")
        );
    }

    @Test
    void deveListar() {

        when(repository.findAll())
                .thenReturn(List.of(instituicao()));

        assertEquals(1, service.listar().size());
    }

    @Test
    void deveBuscar() {

        when(repository.findById("i1"))
                .thenReturn(Optional.of(instituicao()));

        InstituicaoResponse resultado =
                service.buscarPorId("i1");

        assertEquals("Instituto Esperança", resultado.nome());
        assertEquals("Maringá", resultado.endereco().cidade());
    }

    @Test
    void deveCriar() {

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(
                "Lar Feliz",
                service.criar(request()).nome()
        );
    }

    @Test
    void deveAtualizar() {

        Instituicao i = instituicao();

        when(repository.findById("i1"))
                .thenReturn(Optional.of(i));

        when(repository.save(i))
                .thenReturn(i);

        InstituicaoResponse resultado =
                service.atualizar("i1", request());

        assertEquals("Lar Feliz", resultado.nome());
        assertEquals("Sarandi", resultado.endereco().cidade());
    }

    @Test
    void deveExcluirInstituicaoSemReservas() {

        Instituicao i = instituicao();

        when(repository.findById("i1"))
                .thenReturn(Optional.of(i));

        when(doacaoRepository.existsByReservaInstituicaoId("i1"))
                .thenReturn(false);

        service.excluir("i1");

        verify(repository).delete(i);
    }

    @Test
    void naoDeveExcluirInstituicaoComReservas() {

        when(repository.findById("i1"))
                .thenReturn(Optional.of(instituicao()));

        when(doacaoRepository.existsByReservaInstituicaoId("i1"))
                .thenReturn(true);

        assertThrows(
                RegraNegocioException.class,
                () -> service.excluir("i1")
        );

        verify(repository, never())
                .delete(any());
    }

    @Test
    void deveFalharQuandoNaoEncontrar() {

        when(repository.findById("x"))
                .thenReturn(Optional.empty());

        assertThrows(
                InstituicaoNotFoundException.class,
                () -> service.buscarPorId("x")
        );
    }
}
