package dadosFatura;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class ExtrairDadosFaturaContasAutomacaoTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/contasAutomacao/contasAutomacao.csv", delimiter = '\t')
    public void extrairDadosFaturaTest(String id_conta, String data_vencimento) throws IOException {

        Random random = new Random();

        int qtdParcelasVisualizar = (random.nextInt(7) + 1);

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
            // Retorna se as ofertas de parcelamento for COMPULSÓRIO
            nomeCampanha = pathParcelamento.getString("content[0].nomeCampanha");
            quantidadeParcelas = pathParcelamento.getString("content[0].quantidadeParcelas");
            valorEntrada = pathParcelamento.getFloat("content[0].valorEntrada");
        }

        //if(valorTotal >= 100 && (valorTotal > valorPagamentoMinimo) && valorPagamentoEfetuado == valorTotal){
            File pastaDeDados = new File("src/test/resources/" + "/arquivoDeDados/");
            if (!pastaDeDados.exists()) {
                pastaDeDados.mkdirs();
            }

            FileWriter file = new FileWriter("src/test/resources/arquivoDeDados/" + "/" + "contasAbril" + ".csv", true);
            file.write(id_conta + "\t" + dataVencimentoFatura + "\t" + valorTotal + "\t" + valorPagamentoMinimo + "\t"
                    + valorPagamentoEfetuado + "\t" + quantidadeParcelas + "\t" + valorEntrada + "\t" + nomeCampanha + "\n");
            file.flush();
            file.close();
        //}

    }
}