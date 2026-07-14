package salgaderia;

import salgaderia.dao.DadosDAO;
import salgaderia.model.Item;
import salgaderia.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DadosDAO dao = new DadosDAO();

       List<Pedido> pedidos = dao.carregarPedidos();
        System.out.println("Pedidos carregados: " + pedidos.size());

       List<Item> itens = new ArrayList<Item>();
       itens.add(new Item("coxinha", 2, new BigDecimal("5.00")));

         Pedido pedido = new Pedido(1L, "João", itens, new BigDecimal("10.00"), LocalDateTime.now());
          pedidos.add(pedido);

          dao.salvarPedidos(pedidos);
          System.out.println("Pedido salvo com sucesso!");
    }
}