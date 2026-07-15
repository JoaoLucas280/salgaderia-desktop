package salgaderia.model;

import java.util.Objects;

public class ItemCombo {

    private Produto produto;
    private int quantidadeMaxima;

    public ItemCombo() {
    }

    public ItemCombo(Produto produto, int quantidadeMaxima) {
        this.produto = produto;
        this.quantidadeMaxima = quantidadeMaxima;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidadeMaxima() {
        return quantidadeMaxima;
    }

    public void setQuantidadeMaxima(int quantidadeMaxima) {
        this.quantidadeMaxima = quantidadeMaxima;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemCombo itemCombo)) return false;
        return getQuantidadeMaxima() == itemCombo.getQuantidadeMaxima() && Objects.equals(getProduto(), itemCombo.getProduto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProduto(), getQuantidadeMaxima());
    }
}
