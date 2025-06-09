package utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GerarLinhasPagamentos {

    @ParameterizedTest
    @CsvFileSource(resources = "/pagamentos/pagamentos.csv", delimiter = '\t')
    void geraListasDeContasAutomacao(String id_conta, float valor_pagamento, String tipo_pagamento) throws IOException {

        String nome_arquivo_pagamento = "pgt_comp_igual_05_03.csv";
        String data_pagamento = "2025-03-05 00:00:00";
        Random numero_random = new Random();

        File pasta_pagamentos = new File("src/test/resources/" + "/pagamentos");
        if (!pasta_pagamentos.exists()){
            pasta_pagamentos.mkdirs();
        }

        FileWriter pagamentos = new FileWriter("src/test/resources/pagamentos/" + nome_arquivo_pagamento, true);
        switch (tipo_pagamento){
            case "IGUAL":
                pagamentos.write("EXEC SPR_REALIZA_PAGAMENTO NULL, '" + data_pagamento + "', " + id_conta + ", '" + valor_pagamento + "', 99\n");
                pagamentos.flush();
                pagamentos.close();
                break;
            case "MAIOR":
                pagamentos.write("EXEC SPR_REALIZA_PAGAMENTO NULL,  '" + data_pagamento + "', " + id_conta + ", '" + (valor_pagamento + numero_random.nextInt(10)) + "', 99\n");
                pagamentos.flush();
                pagamentos.close();
                break;
            case "MENOR":
                pagamentos.write("EXEC SPR_REALIZA_PAGAMENTO NULL,  '" + data_pagamento + "', " + id_conta + ", '" + (valor_pagamento - numero_random.nextInt(10)) + "', 99\n");
                pagamentos.flush();
                pagamentos.close();
                break;
        }
    }
}