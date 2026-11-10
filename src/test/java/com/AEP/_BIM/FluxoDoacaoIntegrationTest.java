package com.AEP._BIM;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.InetSocketAddress;
import java.time.LocalDate;

import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

// Passa pelo fluxo inteiro usando um servidor Mongo em memória,
// para conferir o que realmente é gravado em cada coleção.
@SpringBootTest
@AutoConfigureMockMvc
class FluxoDoacaoIntegrationTest {

    private static final MongoServer SERVER =
            new MongoServer(new MemoryBackend());

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @DynamicPropertySource
    static void configurarMongo(DynamicPropertyRegistry registry) {

        InetSocketAddress endereco = SERVER.bind();

        registry.add(
                "spring.data.mongodb.uri",
                () -> "mongodb://localhost:" + endereco.getPort() + "/conectadoa"
        );
    }

    @AfterAll
    static void pararMongo() {
        SERVER.shutdown();
    }

    private String postar(String url, String json) throws Exception {

        String resposta = mockMvc.perform(
                        post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(resposta).get("id").asText();
    }

    @Test
    void deveCumprirFluxoCompletoDaDoacao() throws Exception {

        String doadorId = postar("/api/doadores", """
                {
                  "nome": "Mercado Central",
                  "contato": "(44) 99999-9999",
                  "enderecos": [
                    { "rua": "Av. Brasil", "cidade": "Maringá", "estado": "PR" },
                    { "rua": "Rua das Flores", "cidade": "Sarandi", "estado": "PR" }
                  ]
                }
                """);

        String instituicaoId = postar("/api/instituicoes", """
                {
                  "nome": "Instituto Esperança",
                  "contato": "(44) 98888-8888",
                  "endereco": { "rua": "Rua Paraná", "cidade": "Maringá", "estado": "PR" }
                }
                """);

        String doacaoId = postar("/api/doacoes", """
                {
                  "alimento": "Arroz",
                  "quantidade": 10,
                  "unidade": "kg",
                  "validade": "%s",
                  "doadorId": "%s"
                }
                """.formatted(LocalDate.now().plusDays(10), doadorId));

        // relacionamento: a doação mostra os dados do doador
        mockMvc.perform(get("/api/doacoes/" + doacaoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doadorNome").value("Mercado Central"))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));

        mockMvc.perform(get("/api/doacoes/doador/" + doadorId))
                .andExpect(jsonPath("$[0].id").value(doacaoId));

        // doador com doação não pode ser excluído
        mockMvc.perform(delete("/api/doadores/" + doadorId))
                .andExpect(status().isConflict());

        // não dá para retirar sem reservar antes
        mockMvc.perform(put("/api/doacoes/" + doacaoId + "/retirar"))
                .andExpect(status().isConflict());

        mockMvc.perform(
                        put("/api/doacoes/" + doacaoId + "/reservar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"instituicaoId\": \"" + instituicaoId + "\" }")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESERVADA"))
                .andExpect(jsonPath("$.reserva.instituicaoNome").value("Instituto Esperança"));

        // instituição com reserva não pode ser excluída
        mockMvc.perform(delete("/api/instituicoes/" + instituicaoId))
                .andExpect(status().isConflict());

        mockMvc.perform(put("/api/doacoes/" + doacaoId + "/retirar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETIRADA"));

        // confere como os documentos ficaram gravados no banco
        assertTrue(mongoTemplate.collectionExists("doadores"));
        assertTrue(mongoTemplate.collectionExists("instituicoes"));

        Document doador = mongoTemplate
                .getCollection("doadores")
                .find()
                .first();

        assertEquals(2, doador.getList("enderecos", Document.class).size());

        Document doacao = mongoTemplate
                .getCollection("doacoes")
                .find()
                .first();

        assertEquals(doadorId, doacao.getString("doadorId"));

        Document reserva = doacao.get("reserva", Document.class);

        assertEquals(instituicaoId, reserva.getString("instituicaoId"));
        assertNotNull(reserva.get("dataRetirada"));
    }
}
