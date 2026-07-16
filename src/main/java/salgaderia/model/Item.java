package salgaderia.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Item {

    private String nomeProduto;
    private int quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;
    private int quantidadeMaxima;

    public Item() {
    }

    public Item(String nomeProduto, int quantidade){
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
    }

    public Item(String nomeProduto, int quantidade, BigDecimal precoUnitario, BigDecimal subtotal, int quantidadeMaxima) {
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
        this.quantidadeMaxima = quantidadeMaxima;
    }

    public Item(String nomeProduto, int quantidade, BigDecimal precoUnitario) {
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
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

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public int getQuantidadeMaxima() {
        return quantidadeMaxima;
    }

    public void setQuantidadeMaxima(int quantidadeMaxima) {
        this.quantidadeMaxima = quantidadeMaxima;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item item)) return false;
        return getQuantidade() == item.getQuantidade() && getQuantidadeMaxima() == item.getQuantidadeMaxima() && Objects.equals(getNomeProduto(), item.getNomeProduto()) && Objects.equals(getPrecoUnitario(), item.getPrecoUnitario()) && Objects.equals(getSubtotal(), item.getSubtotal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNomeProduto(), getQuantidade(), getPrecoUnitario(), getSubtotal(), getQuantidadeMaxima());
    }
}
