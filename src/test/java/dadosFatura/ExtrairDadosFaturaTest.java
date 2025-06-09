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

    @ParameterizedTest
    @CsvFileSource(resources = "/contasParaTest/contasTest_venc_04-22.csv", delimiter = '\t')
    public void extrairDadosFaturaTest(String id_produto, int status_conta, String id_conta, int qtdParcelasVisualizar, String data_vencimento, float valorProxFatura) throws IOException {

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

        System.out.println("______________________________________________________________________________________________________________________________________________________");

        Response body_parcelamentos =
                given().
                        header("Content-Type", "application/json").
                        header("accept", "application/json").
                        header("access_token", "88bmgAWS130new").
                when().
                        get("http://10.75.33.122:7005/v2/api/contas/" + id_conta + "/faturas/planos-parcelamento?sort=quantidadeParcelas&sort=asc&dataVencimentoPadrao=" + data_vencimento).
                then().
                        log().status().
                        log().body().
                        extract().response();

        JsonPath pathFatura = body_fatura.jsonPath();
        JsonPath pathParcelamento = body_parcelamentos.jsonPath();

        int statusCodeParcelamento = body_parcelamentos.getStatusCode();

        // Dados da Fatura para serem escritos no arquivo
        float valorTotal = pathFatura.getFloat("content[0].valorTotal");
        float valorPagamentoMinimo = pathFatura.getFloat("content[0].valorPagamentoMinimo");
        float valorPagamentoEfetuado = pathFatura.getFloat("content[0].valorPagamentoEfetuado");
        String dataVencimentoFatura = pathFatura.getString("content[0].dataVencimentoFatura");

        // Dados do Parcelamento para serem escritos no arquivo
        String descricaoServicoTipo = pathParcelamento.getString("content[0].descricaoServicoTipo");
        String quantidadeParcelas = null;
        float valorEntrada = 0;
        String nomeCampanha = null;

        if (statusCodeParcelamento == 200) {
            // Dados do Parcelamento para serem escritos no arquivo
            if (descricaoServicoTipo.equals("Parcelamento")) {  // Retorna se as ofertas de parcelamento for CAMPANHA
                nomeCampanha = pathParcelamento.getString("content[" + (qtdParcelasVisualizar - 1) + "].nomeCampanha");
                quantidadeParcelas = pathParcelamento.getString("content[" + (qtdParcelasVisualizar - 1) + "].quantidadeParcelas");
                valorEntrada = pathParcelamento.getFloat("content[" + (qtdParcelasVisualizar - 1) + "].valorEntrada");
            } else {                                            // Retorna se as ofertas de parcelamento for COMPULSÓRIO
                nomeCampanha = pathParcelamento.getString("content[0].nomeCampanha");
                quantidadeParcelas = pathParcelamento.getString("content[0].quantidadeParcelas");
                valorEntrada = pathParcelamento.getFloat("content[0].valorEntrada");
            }
        }

        if(valorTotal > valorPagamentoMinimo && (valorPagamentoEfetuado == valorTotal || valorPagamentoEfetuado == valorEntrada)){

                File pastaDeDados = new File("src/test/resources/" + "/arquivoDeDados/");
                if (!pastaDeDados.exists()) {
                    pastaDeDados.mkdirs();
                }

                FileWriter file = new FileWriter("src/test/resources/arquivoDeDados/" + "/" + "arquivo_vencimento_04-22" + ".csv", true);
                file.write(id_produto + "\t" + status_conta + "\t" + id_conta + "\t" + dataVencimentoFatura + "\t" + valorTotal + "\t" + valorPagamentoMinimo + "\t"
                        + valorPagamentoEfetuado + "\t" + quantidadeParcelas + "\t" + valorEntrada + "\t" + nomeCampanha + "\t" + valorProxFatura + "\n");
                file.flush();
                file.close();
            }
        }
}