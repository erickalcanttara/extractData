package validaOfertasParcelamento;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import java.io.IOException;
import static io.restassured.RestAssured.given;

public class ValidaOfertasParcelamentoTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/arquivoDeDados/arquivo_vencimentos_adicionais_21-25.csv", delimiter = '\t')
    public void extrairDadosFaturaTest(String id_produto, int status_conta, String id_conta, String data_vencimento, float valorTotal, float valorMinimo,
                                       String valorPagamento, String qtdParcelas, String valorEntrada, String nomeCampanha, float valorProxFatura ) throws IOException {

        System.out.println("id_produto = " + id_produto + ", status_conta = " + status_conta + ", id_conta = " + id_conta +
                ", data_vencimento = " + data_vencimento + ", valorTotal = " + valorTotal +
                ", valorMinimo = " + valorMinimo + ", valorPagamento = " + valorPagamento +
                ", qtdParcelas = " + qtdParcelas + ", valorEntrada = " + valorEntrada +
                ", nomeCampanha = " + nomeCampanha + ", valorProxFatura = " + valorProxFatura);


        Response body_parcelamentos =
            given().
                    log().uri().
                    header("Content-Type", "application/json").
                    header("accept", "application/json").
                    header("access_token", "88bmgAWS130new").
            when().
                    get("http://10.75.33.122:7005/v2/api/contas/" + id_conta + "/faturas/planos-parcelamento?sort=quantidadeParcelas&sort=asc&dataVencimentoPadrao=" + data_vencimento).
            then().
                    log().status().
                    log().body().
                    extract().response();

        JsonPath pathParcelamento = body_parcelamentos.jsonPath();

        int statusCodeParcelamento = body_parcelamentos.getStatusCode();
        String descricaoServicoTipo = pathParcelamento.getString("content[0].descricaoServicoTipo");

        if (statusCodeParcelamento != 200){

            Response body_fatura =
                    given().
                            header("Content-Type", "application/json").
                            header("accept", "application/json").
                            header("access_token", "88bmgAWS130new").
                    when().
                            get("http://10.75.33.122:7005/v2/api/contas/" + id_conta + "/faturas/fechadas").
                    then().
                            log().body().
                            statusCode(HttpStatus.SC_OK).
                            extract().response();

            JsonPath pathFatura = body_fatura.jsonPath();

            String dataVencimentoFatura = pathFatura.getString("content[0].dataVencimentoFatura");

            Response planos_parc =
            given().
                    log().uri().
                    header("Content-Type", "application/json").
                    header("accept", "application/json").
                    header("access_token", "88bmgAWS130new").
            when().
                    get("http://10.75.33.122:7005/v2/api/contas/" + id_conta + "/faturas/planos-parcelamento?sort=quantidadeParcelas&sort=asc&dataVencimentoPadrao=" + dataVencimentoFatura).
            then().
                    log().status().
                    log().body().
                    extract().response();

            JsonPath json_planos = planos_parc.jsonPath();

            int statusCodeParcelamento_sc = planos_parc.getStatusCode();
            String descricaoServicoTipo_pc = json_planos.getString("content[0].descricaoServicoTipo");

            Assertions.assertEquals(200, statusCodeParcelamento_sc, "Não há parcelamentos! Verificar!");
            Assertions.assertEquals("Parcelamento", descricaoServicoTipo_pc, "O Parcelamento não é Campanha");

        } else {
            Assertions.assertEquals(200, statusCodeParcelamento, "Não há parcelamentos! Verificar!");
            Assertions.assertEquals("Parcelamento", descricaoServicoTipo, "O Parcelamento não é Campanha");
        }
    }
}
