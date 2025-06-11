package utils;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GerarListasComPaginas {

    @Test
    void gerarListarComPaginas() throws IOException {

        List<String> listaDeProdutos = Arrays.asList("526", "539", "548", "560", "563");
        int numeroDePaginas = 200;

        System.out.println(listaDeProdutos);

        for (int i = 0; i <= numeroDePaginas; i++) {

            FileWriter file = new FileWriter("src/test/resources/produtosEmTeste/" + "/" + "produtos_com_status_conta_venc25_e" + numeroDePaginas +  "-paginas.csv", true);
            file.write(listaDeProdutos.get(0) + "\t" + i + "\t" + "2025-05-25" + "\t" + 0 + "\n" +
                           listaDeProdutos.get(1) + "\t" + i + "\t" + "2025-05-25" + "\t" + 0 + "\n" +
                           listaDeProdutos.get(2) + "\t" + i + "\t" + "2025-05-25" + "\t" + 0 + "\n" +
                           listaDeProdutos.get(3) + "\t" + i + "\t" + "2025-05-25" + "\t" + 0 + "\n" +
                           listaDeProdutos.get(4) + "\t" + i + "\t" + "2025-05-25" + "\t" + 0 + "\n");
            file.flush();
            file.close();
        }

    }

}
