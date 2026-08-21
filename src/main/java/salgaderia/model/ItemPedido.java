package salgaderia.model;

import salgaderia.model.enums.TipoItemPedido;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemPedido {

    private String nomeProduto;
    private int quantidade;
    private BigDecimal precoUnitario;
    private TipoItemPedido tipo;

    public ItemPedido() {
    }

    public ItemPedido(String nomeProduto, int quantidade) {
        this(nomeProduto, quantidade, BigDecimal.ZERO, TipoItemPedido.SABOR);
    }

    public ItemPedido(String nomeProduto, int quantidade, BigDecimal precoUnitario) {
        this(nomeProduto, quantidade, precoUnitario, TipoItemPedido.PRODUTO);
    }

    public ItemPedido(String nomeProduto, int quantidade, TipoItemPedido tipo) {
        this(nomeProduto, quantidade, BigDecimal.ZERO, tipo);
    }

    public ItemPedido(String nomeProduto, int quantidade, BigDecimal precoUnitario, TipoItemPedido tipo) {
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.tipo = tipo;
    }

    public ItemPedido(Produto produto, int quantidade) {
        this(produto.getNomeProduto(), quantidade, produto.getPrecoUnitario(), TipoItemPedido.PRODUTO);
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public TipoItemPedido getTipo() {
        return tipo;
    }

    public void setTipo(TipoItemPedido tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemPedido that)) return false;
        return getQuantidade() == that.getQuantidade() && Objects.equals(getNomeProduto(), that.getNomeProduto()) && Objects.equals(getPrecoUnitario(), that.getPrecoUnitario()) && getTipo() == that.getTipo();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNomeProduto(), getQuantidade(), getPrecoUnitario(), getTipo());
    }
}
