package salgaderia.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cento {
    private int id;
    private String nome;
    private BigDecimal precoTotal;
    private int maxSabores;
    private List<ItemCombo> itens; // Produto + quantidade máxima

    public Cento() {
        this.itens = new ArrayList<>();
        this.maxSabores = 0;
    }

    public Cento(int id, String nome, BigDecimal precoTotal, int maxSabores, List<ItemCombo> itens) {
        this.id = id;
        this.nome = nome;
        this.precoTotal = precoTotal;
        this.maxSabores = maxSabores;
        this.itens = itens != null ? itens : new ArrayList<>();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPrecoTotal() { return precoTotal; }
    public void setPrecoTotal(BigDecimal precoTotal) { this.precoTotal = precoTotal; }

    public int getMaxSabores() { return maxSabores; }
    public void setMaxSabores(int maxSabores) { this.maxSabores = maxSabores; }

    public List<ItemCombo> getItens() { return itens; }
    public void setItens(List<ItemCombo> itens) { this.itens = itens != null ? itens : new ArrayList<>(); }

    @Override
    public String toString() {
        return nome + " (R$ " + String.format("%.2f", precoTotal) + ")";
    }
}