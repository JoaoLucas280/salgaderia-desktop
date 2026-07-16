package salgaderia.model;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemPedido {

    private String nomeProduto;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemPedido() {
    }

    public ItemPedido(String nomeProduto, int quantidade) {
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
    }

    public ItemPedido(String nomeProduto, int quantidade, BigDecimal precoUnitario) {
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public ItemPedido(Produto produto, int quantidade) {
        this.nomeProduto = produto.getNomeProduto();
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPrecoUnitario();
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemPedido that)) return false;
        return getQuantidade() == that.getQuantidade() && Objects.equals(getNomeProduto(), that.getNomeProduto()) && Objects.equals(getPrecoUnitario(), that.getPrecoUnitario());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNomeProduto(), getQuantidade(), getPrecoUnitario());
    }
}
