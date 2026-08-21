package salgaderia.model;

import java.util.Objects;

public class ItemCombo {

    private Produto produto;


    public ItemCombo() {
    }

    public ItemCombo(Produto produto) {
        this.produto = produto;

    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemCombo itemCombo)) return false;
        return  Objects.equals(getProduto(), itemCombo.getProduto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProduto());
    }
}
