package com.AEP._BIM.configuration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.AEP._BIM.model.Doacao;
import com.AEP._BIM.repository.DoacaoRepository;
import com.AEP._BIM.model.StatusDoacao;


@Configuration
@Profile("dev")
public class DevelopmentDataConfiguration {

    @Bean
    CommandLineRunner loadDevelopmentData(DoacaoRepository repository) {
        return args -> sampleDoacoes().stream()
                .filter(doacao -> !repository.existsById(doacao.getId()))
                .forEach(repository::save);
    }

    private List<Doacao> sampleDoacoes() {
        return List.of(
                new Doacao(
                        "arroz-10kg",
                        "Arroz",
                        10.0,
                        "kg",
                        LocalDate.now().plusDays(30),
                        "Mercado Central",
                        "(44) 99999-9999",
                        StatusDoacao.DISPONIVEL
                ),
                new Doacao(
                        "feijao-5kg",
                        "Feijão",
                        5.0,
                        "kg",
                        LocalDate.now().plusDays(20),
                        "Padaria Esperança",
                        "(44) 98888-7777",
                        StatusDoacao.DISPONIVEL
                )
        );
    }
}
