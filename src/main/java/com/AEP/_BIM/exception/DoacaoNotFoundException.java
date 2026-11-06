package com.AEP._BIM.exception;

public class DoacaoNotFoundException extends RecursoNaoEncontradoException {
    public DoacaoNotFoundException(String id) {
        super("Doação não encontrada: " + id);
    }
}
