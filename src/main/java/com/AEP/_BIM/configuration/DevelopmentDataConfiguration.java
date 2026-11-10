package com.AEP._BIM.configuration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.AEP._BIM.model.Doacao;
import com.AEP._BIM.model.Doador;
import com.AEP._BIM.model.Endereco;
import com.AEP._BIM.model.Instituicao;
import com.AEP._BIM.model.StatusDoacao;
import com.AEP._BIM.repository.DoacaoRepository;
import com.AEP._BIM.repository.DoadorRepository;
import com.AEP._BIM.repository.InstituicaoRepository;


@Configuration
@Profile("dev")
public class DevelopmentDataConfiguration {

    @Bean
    CommandLineRunner loadDevelopmentData(
            DoacaoRepository doacaoRepository,
            DoadorRepository doadorRepository,
            InstituicaoRepository instituicaoRepository) {

        return args -> {

            sampleDoadores().stream()
                    .filter(doador -> !doadorRepository.existsById(doador.getId()))
                    .forEach(doadorRepository::save);

            sampleInstituicoes().stream()
                    .filter(instituicao -> !instituicaoRepository.existsById(instituicao.getId()))
                    .forEach(instituicaoRepository::save);

            sampleDoacoes().stream()
                    .filter(doacao -> !doacaoRepository.existsById(doacao.getId()))
                    .forEach(doacaoRepository::save);
        };
    }

    private List<Doador> sampleDoadores() {
        return List.of(
                new Doador(
                        "mercado-central",
                        "Mercado Central",
                        "(44) 99999-9999",
                        List.of(
                                new Endereco("Av. Brasil, 100", "Maringá", "PR"),
                                new Endereco("Rua das Flores, 25", "Sarandi", "PR")
                        )
                ),
                new Doador(
                        "padaria-esperanca",
                        "Padaria Esperança",
                        "(44) 98888-7777",
                        List.of(new Endereco("Rua Santos Dumont, 50", "Maringá", "PR"))
                )
        );
    }

    private List<Instituicao> sampleInstituicoes() {
        return List.of(
                new Instituicao(
                        "instituto-esperanca",
                        "Instituto Esperança",
                        "(44) 98888-8888",
                        new Endereco("Rua Paraná, 300", "Maringá", "PR")
                )
        );
    }

    private List<Doacao> sampleDoacoes() {
        return List.of(
                new Doacao(
                        "arroz-10kg",
                        "Arroz",
                        10.0,
                        "kg",
                        LocalDate.now().plusDays(30),
                        "mercado-central",
                        StatusDoacao.DISPONIVEL
                ),
                new Doacao(
                        "feijao-5kg",
                        "Feijão",
                        5.0,
                        "kg",
                        LocalDate.now().plusDays(20),
                        "padaria-esperanca",
                        StatusDoacao.DISPONIVEL
                )
        );
    }
}
