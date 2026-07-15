package salgaderia.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Combo {
    private int id;
    private String nome;
    private List<ItemCombo> itens;
    private BigDecimal precoTotal;

    public Combo() {
    }

    public Combo(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal) {
        this.id = id;
        this.nome = nome;
        this.itens = itens;
        this.precoTotal = precoTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<ItemCombo> getItens() {
        return itens;
    }

    public void setItens(List<ItemCombo> itens) {
        this.itens = itens;
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Combo combo)) return false;
        return getId() == combo.getId() && Objects.equals(getNome(), combo.getNome()) && Objects.equals(getItens(), combo.getItens()) && Objects.equals(getPrecoTotal(), combo.getPrecoTotal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNome(), getItens(), getPrecoTotal());
    }
}
