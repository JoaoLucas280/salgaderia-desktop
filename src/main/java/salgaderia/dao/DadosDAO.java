package salgaderia.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import salgaderia.model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DadosDAO {

    private final ObjectMapper mapper;

    private final String pastaDados;

    private final String arquivoPedidos;

    private final String arquivoFinanceiro;

    public DadosDAO() {
        this.mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        this.pastaDados = System.getProperty("user.dir") + "/dados/";

        this.arquivoPedidos = this.pastaDados + "pedidos.json";

        this.arquivoFinanceiro = this.pastaDados + "financeiro.json";

        criarPastaSeNaoExistir();
    }

    public List<Pedido> carregarPedidos() {
        try {
            File arquivo = new File(arquivoPedidos);

            if (!arquivo.exists()) {
                return new ArrayList<>();
            }

            List<Pedido> pedidos = mapper.readValue(arquivo, mapper.getTypeFactory().constructCollectionType(List.class, Pedido.class));
            return new ArrayList<>(pedidos);

        } catch (IOException e) {
            System.err.println("Erro ao carregar pedidos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
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

        if (!pasta.exists()) {
            boolean criada = pasta.mkdirs();
            if (criada) {
                System.out.println("Pasta criada com sucesso: " + pastaDados);
            }
        }
    }

    public List<Combo> carregarCombos() {
        try {
            File arquivo = new File(pastaDados + "combos.json");
            if (!arquivo.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(arquivo,
                    mapper.getTypeFactory().constructCollectionType(List.class, Combo.class));
        } catch (IOException e) {
            System.err.println("Erro ao carregar combos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void salvarCombos(List<Combo> combos) {
        try {
            mapper.writeValue(new File(pastaDados + "combos.json"), combos);
        } catch (IOException e) {
            System.err.println("Erro ao salvar combos: " + e.getMessage());
        }
    }


    public List<Cento> carregarCentos() {
        try {
            File arquivo = new File(pastaDados + "centos.json");
            if (!arquivo.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(arquivo,
                    mapper.getTypeFactory().constructCollectionType(List.class, Cento.class));
        } catch (IOException e) {
            System.err.println("Erro ao carregar centos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void salvarCentos(List<Cento> centos) {
        try {
            mapper.writeValue(new File(pastaDados + "centos.json"), centos);
        } catch (IOException e) {
            System.err.println("Erro ao salvar centos: " + e.getMessage());
        }
    }

    public List<Produto> carregarUnitarios() {
        try {
            File arquivo = new File(pastaDados + "unitarios.json");
            if (!arquivo.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(arquivo,
                    mapper.getTypeFactory().constructCollectionType(List.class, Produto.class));
        } catch (IOException e) {
            System.err.println("Erro ao carregar unitários: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void salvarUnitarios(List<Produto> unitarios) {
        try {
            mapper.writeValue(new File(pastaDados + "unitarios.json"), unitarios);
        } catch (IOException e) {
            System.err.println("Erro ao salvar unitários: " + e.getMessage());
        }
    }
}