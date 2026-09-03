package com.AEP._BIM.dto;

import com.AEP._BIM.model.StatusDoacao;

public record DoacaoSummaryResponse(
        String id,
        String alimento,
        StatusDoacao status) {
}