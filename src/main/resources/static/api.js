// Faz a chamada na API e, se der erro, lança a mensagem que o backend devolveu.
async function chamarApi(url, metodo = "GET", dados = null) {

    const opcoes = { method: metodo };

    if (dados) {
        opcoes.headers = { "Content-Type": "application/json" };
        opcoes.body = JSON.stringify(dados);
    }

    const resposta = await fetch(url, opcoes);

    if (!resposta.ok) {

        const erro = await resposta.json().catch(() => ({}));

        const camposInvalidos = Object.values(erro.fieldErrors || {});

        throw new Error(
            camposInvalidos.length > 0
                ? camposInvalidos.join(", ")
                : erro.message || "Não foi possível concluir a operação."
        );
    }

    return resposta.status === 204 ? null : resposta.json();
}
