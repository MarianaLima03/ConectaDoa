package com.AEP._BIM.exception;

public class InstituicaoNotFoundException extends RecursoNaoEncontradoException {
    public InstituicaoNotFoundException(String id) {
        super("Instituição não encontrada: " + id);
    }
}
