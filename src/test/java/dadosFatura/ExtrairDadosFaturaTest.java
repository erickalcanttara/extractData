package dadosFatura;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class ExtrairDadosFaturaTest {

    private static final String baseUrl = utils.ConfigUtil.get("api.base.url");
    private static final String token = utils.ConfigUtil.get("api.token");
    private static final String outputDir = utils.ConfigUtil.get("output.dir");
    private static final String outputFile = utils.ConfigUtil.get("output.file");

    @ParameterizedTest
    @CsvFileSource(resources = "/contasParaTest/contasTest_venc_04-22-copia.csv", delimiter = '\t')
    public void extrairDadosFaturaTest(String idProduto, int statusConta, String idConta, int qtdParcelasVisualizar, String dataVencimento, float valorProxFatura) throws IOException {

        Response faturaResponse = buscarFatura(idConta);
        Response parcelamentoResponse = buscarParcelamentos(idConta, dataVencimento);

        JsonPath faturaJson = faturaResponse.jsonPath();
        JsonPath parcelamentoJson = parcelamentoResponse.jsonPath();

        float valorTotal = faturaJson.getFloat("content[0].valorTotal");
        float valorPagamentoMinimo = faturaJson.getFloat("content[0].valorPagamentoMinimo");
        float valorPagamentoEfetuado = faturaJson.getFloat("content[0].valorPagamentoEfetuado");
        String dataVencimentoFatura = faturaJson.getString("content[0].dataVencimentoFatura");

        String descricaoServicoTipo = parcelamentoJson.getString("content[0].descricaoServicoTipo");
        String quantidadeParcelas = null;
        float valorEntrada = 0;
        String nomeCampanha = null;

        if (parcelamentoResponse.getStatusCode() == HttpStatus.SC_OK) {
            if ("Parcelamento".equals(descricaoServicoTipo)) {
                int idx = qtdParcelasVisualizar - 1;
                nomeCampanha = parcelamentoJson.getString("content[" + idx + "].nomeCampanha");
                quantidadeParcelas = parcelamentoJson.getString("content[" + idx + "].quantidadeParcelas");
                valorEntrada = parcelamentoJson.getFloat("content[" + idx + "].valorEntrada");
            } else {
                nomeCampanha = parcelamentoJson.getString("content[0].nomeCampanha");
                quantidadeParcelas = parcelamentoJson.getString("content[0].quantidadeParcelas");
                valorEntrada = parcelamentoJson.getFloat("content[0].valorEntrada");
            }
        }

        if (valorTotal > valorPagamentoMinimo && (valorPagamentoEfetuado == valorTotal || valorPagamentoEfetuado == valorEntrada)) {
            salvarDados(idProduto, statusConta, idConta, dataVencimentoFatura, valorTotal, valorPagamentoMinimo,
                    valorPagamentoEfetuado, quantidadeParcelas, valorEntrada, nomeCampanha, valorProxFatura);
        }
    }

    private Response buscarFatura(String idConta) {
        return given()
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("access_token", token)
               .when()
                    .get(baseUrl + "/" + idConta + "/faturas/fechadas")
               .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract().response();
    }

    private Response buscarParcelamentos(String idConta, String dataVencimento) {
        return given()
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("access_token", token)
               .when()
                    .get(baseUrl + "/" + idConta + "/faturas/planos-parcelamento?sort=quantidadeParcelas&sort=asc&dataVencimentoPadrao=" + dataVencimento)
                .then()
                    .extract().response();
    }

    private void salvarDados(String idProduto, int statusConta, String idConta, String dataVencimentoFatura, float valorTotal,
                             float valorPagamentoMinimo, float valorPagamentoEfetuado, String quantidadeParcelas, float valorEntrada,
                             String nomeCampanha, float valorProxFatura) throws IOException {

        File pastaDeDados = new File(outputDir);
        if (!pastaDeDados.exists()) {
            pastaDeDados.mkdirs();
        }

        try (FileWriter file = new FileWriter(outputDir + outputFile, true)) {
            file.write(idProduto + "\t" + statusConta + "\t" + idConta + "\t" + dataVencimentoFatura + "\t" + valorTotal + "\t"
                    + valorPagamentoMinimo + "\t" + valorPagamentoEfetuado + "\t" + quantidadeParcelas + "\t" + valorEntrada + "\t"
                    + nomeCampanha + "\t" + valorProxFatura + "\n");
        }
    }
}