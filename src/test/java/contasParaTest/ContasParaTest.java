package contasParaTest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class ContasParaTest {

    private final String baseUrl = utils.ConfigUtil.get("api.base.url");
    private final String token_base = utils.ConfigUtil.get("api.token");

    @ParameterizedTest
    @CsvFileSource(resources = "/produtosEmTeste/produtos_com_status_conta-venc-25.csv", delimiter = '\t', numLinesToSkip = 1)
    void getContasParaTest(int produto, int page, String dia_vencimento, String ano_mes, int status_conta) throws IOException {

        Random random = new Random();

        int totalDePaginasPorProduto = getTotalDePaginasPorProduto(produto, status_conta, dia_vencimento);

        int randomPage = random.nextInt(totalDePaginasPorProduto);

        Response body_contas =
                getRequestSpec()
                        .log().uri()
                .when()
                        .get(baseUrl + "/contas?page=" + randomPage + "&limit=50&idProduto=" + produto + "&idStatusConta=" + status_conta + "&diaVencimento=" + dia_vencimento)
                .then()
                        .log().body()
                        .statusCode(HttpStatus.SC_OK)
                        .extract().response();

        JsonPath pathContas = body_contas.jsonPath();

        for (int i = 0; i < 50; i++) {

            String id_conta = pathContas.getString("content[" + i + "].id");
            String id_produto = pathContas.getString("content[" + i + "].idProduto");
            String data_ultimo_pagamento = pathContas.getString("content[" + i + "].dataUltimoPagamento");
            String id_status_conta = pathContas.getString("content[" + i + "].idStatusConta");

            if (data_ultimo_pagamento != null){

                Response body_contas_por_id =
                        getRequestSpec()
                                .log().uri()
                        .when()
                                .get(baseUrl + "/contas/" + id_conta)
                        .then()
                                .log().body()
                                .statusCode(HttpStatus.SC_OK)
                                .extract().response();

                JsonPath pathContas_por_id = body_contas_por_id.jsonPath();

                float saldoExtratoAnterior = pathContas_por_id.getFloat("saldoExtratoAnterior");
                float saldoAtualFinal = pathContas_por_id.getFloat("saldoAtualFinal");

                // saldoExtratoAnterior > 100 &&
                // Se o vencimento já cortou, o saldoAtualFinal da api /contas/{id} será o saldo da próxima fatura
                if (saldoAtualFinal > 50){
                    System.out.println(id_conta);

                    FileWriter file = new FileWriter("src/test/resources/contasParaTest/" + "/" + "contasTest_venc_25" + ".csv", true);
                    file.write(id_produto + "\t" + id_status_conta + "\t" + id_conta + "\t" + (random.nextInt(7) + 1) + "\t" + ano_mes + "-" + dia_vencimento + "\t"
                            + saldoAtualFinal + "\n");
                    file.flush();
                    file.close();
                }
            }
        }
    }

   int getTotalDePaginasPorProduto(int id_produto, int status_conta, String dia_vencimento){

        Response body_contas =
                getRequestSpec()
                        .log().uri()
                .when()
                        .get(baseUrl + "/contas?idProduto=" + id_produto + "&idStatusConta=" + status_conta + "&diaVencimento=" + dia_vencimento)
                .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract().response();

        JsonPath pathContas = body_contas.jsonPath();

       return pathContas.getInt("totalPages");

    }

    private RequestSpecification getRequestSpec() {
        return given()
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .header("access_token", token_base);
    }
}