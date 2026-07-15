package salgaderia.model;

import salgaderia.model.enums.tipoProduto;

import java.math.BigDecimal;
import java.util.Objects;

public class Produto {

    private Long id;
    private String nomeProduto;
    private BigDecimal preco;
    private tipoProduto tipoProduto;

    public Produto() {
    }

    public Produto(Long id, String nomeProduto, BigDecimal preco, tipoProduto tipoProduto) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.preco = preco;
        this.tipoProduto = tipoProduto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public tipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(tipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Produto produto)) return false;
        return Objects.equals(getId(), produto.getId()) && Objects.equals(getNomeProduto(), produto.getNomeProduto()) && Objects.equals(getPreco(), produto.getPreco()) && getTipoProduto() == produto.getTipoProduto();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNomeProduto(), getPreco(), getTipoProduto());
    }

    @Override
    public String toString() {
        return nomeProduto + " (R$ " + String.format("%.2f", preco.doubleValue()) + ")";
    }
}
