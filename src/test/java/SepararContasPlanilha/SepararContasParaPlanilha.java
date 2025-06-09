package SepararContasPlanilha;

import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SepararContasParaPlanilha {

    @Test
    public void separarContasParaPlanilha(){

        Path pathArquivo = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\extractData\\src\\test\\resources\\arquivoDeDados\\arquivo_vencimento_25.csv");

        try (BufferedReader arquivoComContas = Files.newBufferedReader(pathArquivo)) {

            List<String> produto_526 = new ArrayList<>();
            List<String> produto_539 = new ArrayList<>();
            List<String> produto_548 = new ArrayList<>();
            List<String> produto_560 = new ArrayList<>();
            List<String> produto_563 = new ArrayList<>();

            String linhaArquivo = "";
            while ((linhaArquivo = arquivoComContas.readLine()) != null){

                String produto = linhaArquivo.split("\t")[0];
                switch (produto){
                    case "526":
                        produto_526.add(linhaArquivo);
                        break;
                    case "539":
                        produto_539.add(linhaArquivo);
                        break;
                    case "548":
                        produto_548.add(linhaArquivo);
                        break;
                    case "560":
                        produto_560.add(linhaArquivo);
                        break;
                    case "563":
                        produto_563.add(linhaArquivo);
                        break;
                }
            }

            List<Integer> tamanhosListas = new ArrayList<>();
            tamanhosListas.add(produto_526.size());
            tamanhosListas.add(produto_539.size());
            tamanhosListas.add(produto_548.size());
            tamanhosListas.add(produto_560.size());
            tamanhosListas.add(produto_563.size());

            System.out.println("O tamanho da lista com id_produto 526: " + produto_526.size());
            System.out.println("O tamanho da lista com id_produto 539: " + produto_539.size());
            System.out.println("O tamanho da lista com id_produto 548: " + produto_548.size());
            System.out.println("O tamanho da lista com id_produto 560: " + produto_560.size());
            System.out.println("O tamanho da lista com id_produto 563: " + produto_563.size());

            int menorValorLista = Collections.min(tamanhosListas);
            System.out.println(menorValorLista);

            File dadosPlanilha = new File("src/test/resources/" + "dadosPlanilha");
            if (!dadosPlanilha.exists()){
                dadosPlanilha.mkdirs();
            }

            for (int i = 0; i < menorValorLista; i++) {
                FileWriter linhaLista = new FileWriter("src/test/resources/dadosPlanilha/" + "dadosPlanilha" + ".csv", true);
                linhaLista.write(produto_526.get(i) + "\n");
                linhaLista.write(produto_539.get(i) + "\n");
                linhaLista.write(produto_548.get(i) + "\n");
                linhaLista.write(produto_560.get(i) + "\n");
                linhaLista.write(produto_563.get(i) + "\n");
                linhaLista.flush();
                linhaLista.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
