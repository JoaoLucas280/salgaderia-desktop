package salgaderia.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Cento {
    private int id;
    private String nome;
    private List<ItemCombo> itens;
    private BigDecimal precoTotal;
    private int quantidadeMaxima;
    private int quantidadeMaximaDeSabores;
    private BigDecimal precoOverride;

    public Cento() {
    }

    public Cento(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal) {
        this.id = id;
        this.nome = nome;
        this.itens = itens;
        this.precoTotal = precoTotal;
        this.quantidadeMaxima = 100;
        this.quantidadeMaximaDeSabores = itens != null ? itens.size() : 5;
    }

    public Cento(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal, 
                 int quantidadeMaximaDeSabores) {
        this.id = id;
        this.nome = nome;
        this.itens = itens;
        this.precoTotal = precoTotal;
        this.quantidadeMaxima = 100;
        this.quantidadeMaximaDeSabores = quantidadeMaximaDeSabores;
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
        // Se houver override, retorna o preço override
        if (precoOverride != null && precoOverride.compareTo(BigDecimal.ZERO) > 0) {
            return precoOverride;
        }
        return precoTotal;
    }

    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
    }

    public int getQuantidadeMaxima() {
        return quantidadeMaxima;
    }

    public void setQuantidadeMaxima(int quantidadeMaxima) {
        this.quantidadeMaxima = quantidadeMaxima;
    }

    public int getQuantidadeMaximaDeSabores() {
        return quantidadeMaximaDeSabores;
    }

    public void setQuantidadeMaximaDeSabores(int quantidadeMaximaDeSabores) {
        this.quantidadeMaximaDeSabores = quantidadeMaximaDeSabores;
    }

    public BigDecimal getPrecoOverride() {
        return precoOverride;
    }

    public void setPrecoOverride(BigDecimal precoOverride) {
        this.precoOverride = precoOverride;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cento cento)) return false;
        return getId() == cento.getId() && Objects.equals(getNome(), cento.getNome()) && 
               Objects.equals(getItens(), cento.getItens()) && 
               Objects.equals(getPrecoTotal(), cento.getPrecoTotal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNome(), getItens(), getPrecoTotal());
    }

    @Override
    public String toString() {
        return nome;
    }
}
