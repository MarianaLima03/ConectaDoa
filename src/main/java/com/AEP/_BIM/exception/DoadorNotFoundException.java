package com.AEP._BIM.exception;

public class DoadorNotFoundException extends RecursoNaoEncontradoException {
    public DoadorNotFoundException(String id) {
        super("Doador não encontrado: " + id);
    }
}
