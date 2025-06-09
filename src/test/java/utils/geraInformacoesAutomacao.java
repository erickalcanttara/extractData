package utils;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class geraInformacoesAutomacao {

    @Test
    void geraListasDeContasAutomacao(){

        Path pathArquivo = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\extractData\\src\\test\\resources\\contasAutomacao\\contasAutomacao.csv");

        try (BufferedReader arquivoComContas = Files.newBufferedReader(pathArquivo)) {

            int numero_de_contas_por_cenario = 2;

            List<String> contasAutomacao = new ArrayList<>();

            String linhaArquivo = "";
            while ((linhaArquivo = arquivoComContas.readLine()) != null){
                contasAutomacao.add(linhaArquivo);
            }

            if (numero_de_contas_por_cenario == 2){
                for (int i = 0; i < contasAutomacao.size(); i+=2) {
                    if (i == ((contasAutomacao).size())-2){
                        System.out.println("['" + contasAutomacao.get(i) + "', '" + contasAutomacao.get(i+1) + "']");
                    }else {
                        System.out.println("['" + contasAutomacao.get(i) + "', '" + contasAutomacao.get(i+1) + "'],");
                    }
                }
            } else {
                for (int i = 0; i < contasAutomacao.size(); i+=5) {
                    if (i == ((contasAutomacao).size())-5){
                        System.out.println("['" + contasAutomacao.get(i) + "', '" + contasAutomacao.get(i+1) + "', '" + contasAutomacao.get(i+2) + "', '" +
                                contasAutomacao.get(i+3) + "', '" + contasAutomacao.get(i+4) + "']");
                    }else {
                        System.out.println("['" + contasAutomacao.get(i) + "', '" + contasAutomacao.get(i+1) + "', '" + contasAutomacao.get(i+2) + "', '" +
                                contasAutomacao.get(i+3) + "', '" + contasAutomacao.get(i+4) + "'],");
                    }
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void geraListasNomesCenariosAutomacaoTest(){

        Path pathArquivo = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\extractData\\src\\test\\resources\\contasAutomacao\\cenariosAutomacao.csv");

        try (BufferedReader arquivoComContas = Files.newBufferedReader(pathArquivo)) {

            List<String> cenariosAutomacao = new ArrayList<>();

            String linhaArquivo = "";
            while ((linhaArquivo = arquivoComContas.readLine()) != null){
                cenariosAutomacao.add(linhaArquivo);
            }

            for (int i = 0; i < cenariosAutomacao.size(); i++) {
                if (i == ((cenariosAutomacao).size())-1){
                    System.out.println("['" + "Cenário - " + (i + 1) + " - " + cenariosAutomacao.get(i) + "']");
                }else {
                    System.out.println("['" + "Cenário - " + (i + 1) + " - " + cenariosAutomacao.get(i) + "'],");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void geraListaDeContasAutomacao(){

        Path pathArquivo = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\extractData\\src\\test\\resources\\contasAutomacao\\contasAutomacao.csv");

        try (BufferedReader arquivoComContas = Files.newBufferedReader(pathArquivo)) {

            List<String> contasAutomacao = new ArrayList<>();

            String linhaArquivo = "";
            while ((linhaArquivo = arquivoComContas.readLine()) != null){
                contasAutomacao.add(linhaArquivo);
            }

            StringBuilder lista_de_contas = new StringBuilder("[");

            for (String conta : contasAutomacao) {
                lista_de_contas.append("'").append(conta).append("', ");
            }

            lista_de_contas.append("]");
            System.out.println(lista_de_contas);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

