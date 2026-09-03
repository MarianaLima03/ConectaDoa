package com.AEP._BIM.exception;

public class DoacaoNotFoundException extends RuntimeException {
    public DoacaoNotFoundException(String id) {
        super("Doação não encontrada: " + id);
    }
}
