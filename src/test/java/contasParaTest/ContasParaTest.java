package contasParaTest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class ContasParaTest {

    private String baseUrl;
    private String token_base;

    public ContasParaTest() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties props = new Properties();
            props.load(input);
            this.baseUrl = props.getProperty("api.base.url");
            this.token_base = props.getProperty("api.token");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar configurações", e);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/produtosEmTeste/produtos_com_status_conta-venc-04-22.csv", delimiter = '\t', numLinesToSkip = 1)
    void getContasParaTest(int produto, int page, String dia_vencimento, String ano_mes, int status_conta) throws IOException {

        Random random = new Random();

        Response body_contas =
                getRequestSpec()
                        .log().uri()
                .when()
                        .get(baseUrl + "/contas?page=" + page + "&limit=50&idProduto=" + produto + "&idStatusConta=" + status_conta + "&diaVencimento=" + dia_vencimento)
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

                    FileWriter file = new FileWriter("src/test/resources/contasParaTest/" + "/" + "contasTest_venc_04-22" + ".csv", true);
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

        int total_de_elementos = pathContas.getInt("totalElements");
        int total_de_paginas;

        if (total_de_elementos >= 50){
            total_de_paginas = total_de_elementos / 50;
        } else {
            total_de_paginas = 1;
        }
        return total_de_paginas;
    }

    private RequestSpecification getRequestSpec() {
        return given()
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .header("access_token", token_base);
    }
}