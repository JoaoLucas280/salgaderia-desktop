package salgaderia.service;

import salgaderia.dao.DadosDAO;
import salgaderia.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoService {

    private DadosDAO dao = DadosDAO.getInstance();

    public void salvarPedido(Pedido pedido) {

        validarPedido(pedido);


        pedido.setTotal(calcularTotal(pedido));


        pedido.setDataHora(LocalDateTime.now());


        dao.salvarPedido(pedido);
    }

    public void validarPedido(Pedido pedido) {
        if (pedido.getNomeCliente() == null || pedido.getNomeCliente().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente é obrigatório.");
        }
        if (pedido.getTelefone() == null || pedido.getTelefone().isEmpty()) {
            throw new IllegalArgumentException("O telefone do cliente é obrigatório.");
        }
    }

    public BigDecimal calcularTotal(Pedido pedido) {
        var subtotal = pedido.getItens() != null ?
                pedido.getItens().stream()
                        .map(item -> {
                            BigDecimal preco = item.getPrecoUnitario() != null ?
                                    item.getPrecoUnitario() : BigDecimal.ZERO;
                            return preco.multiply(BigDecimal.valueOf(item.getQuantidade()));
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        if (pedido.getTaxaEntrega() != null && pedido.getTaxaEntrega().compareTo(BigDecimal.ZERO) > 0) {
            return subtotal.add(pedido.getTaxaEntrega());
        }

        return subtotal;
    }

    public void validarCombo(Pedido pedido) {
        int total = pedido.getItens().stream()
                .mapToInt(item -> item.getQuantidade())
                .sum();

        int maximo = 100;
        if (total > maximo) {
            throw new IllegalArgumentException("Total de salgados excede o limite do combo!");
        }
    }

}