package salgaderia.dao;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import salgaderia.model.Pedido;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DadosDAO {

    private final ObjectMapper mapper;

    private final String pastaDados;

    private final String arquivoPedidos;

    private final String arquivoFinanceiro;

    public DadosDAO (){
        this.mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        this.pastaDados = System.getProperty("user.dir") + "/dados/";

        this.arquivoPedidos = this.pastaDados + "pedidos.json";

        this.arquivoFinanceiro = this.pastaDados + "financeiro.json";

        criarPastaSeNaoExistir();
    }

    public List<Pedido> carregarPedidos() {
        try {
            File arquivo = new File(arquivoPedidos);

            if  (!arquivo.exists()) {
                return new ArrayList<>();
            }

            return mapper.readValue(arquivo, mapper.getTypeFactory().constructCollectionType(List.class, Pedido.class));

        }catch (IOException e) {
            System.err.println("Erro ao carregar pedidos: " + e.getMessage());
            return List.of();
        }
    }

    public void salvarPedidos(List<Pedido> pedidos) {
        try {

            mapper.writeValue(new File(arquivoPedidos), pedidos);

        } catch (IOException e) {
            System.err.println("Erro ao salvar pedidos: " + e.getMessage());
        }
    }

    private void criarPastaSeNaoExistir() {
        File pasta = new File(pastaDados);

        if  (!pasta.exists()) {
            boolean criada = pasta.mkdirs();
            if  (criada) {
                System.out.println("Pasta criada com sucesso: " + pastaDados);
            }
        }
    }







}
