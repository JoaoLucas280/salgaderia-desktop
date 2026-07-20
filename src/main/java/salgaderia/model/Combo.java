package salgaderia.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Combo {
    private int id;
    private String nome;
    private List<ItemCombo> itens;
    private BigDecimal precoTotal;
    private int quantidadeMaximaDeItems;
    private int quantidadeMaximaDeFlavors;
    private BigDecimal precoOverride;

    // ★ NOVOS ATRIBUTOS PARA ADICIONAIS ★
    private List<Adicional> adicionaisElegiveis;
    private int quantidadeAdicionaisPermitidos; // 0 = nenhum, 1 = um incluso, etc.

    public Combo() {
        this.adicionaisElegiveis = new ArrayList<>();
        this.quantidadeAdicionaisPermitidos = 0;
    }

    // Construtor original (sem adicionais) - mantido para compatibilidade
    public Combo(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal) {
        this.id = id;
        this.nome = nome;
        this.itens = itens;
        this.precoTotal = precoTotal;
        this.quantidadeMaximaDeItems = 100;
        this.quantidadeMaximaDeFlavors = itens != null ? itens.size() : 4;
        this.adicionaisElegiveis = new ArrayList<>();
        this.quantidadeAdicionaisPermitidos = 0;
    }

    // Construtor completo (com adicionais)
    public Combo(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal,
                 int quantidadeMaximaDeItems, int quantidadeMaximaDeFlavors,
                 List<Adicional> adicionaisElegiveis, int quantidadeAdicionaisPermitidos) {
        this.id = id;
        this.nome = nome;
        this.itens = itens;
        this.precoTotal = precoTotal;
        this.quantidadeMaximaDeItems = quantidadeMaximaDeItems;
        this.quantidadeMaximaDeFlavors = quantidadeMaximaDeFlavors;
        this.adicionaisElegiveis = adicionaisElegiveis != null ? adicionaisElegiveis : new ArrayList<>();
        this.quantidadeAdicionaisPermitidos = quantidadeAdicionaisPermitidos;
    }

    // Construtor simplificado (com adicionais, sem maxItems e maxFlavors)
    public Combo(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal,
                 List<Adicional> adicionaisElegiveis, int quantidadeAdicionaisPermitidos) {
        this(id, nome, itens, precoTotal, 100, itens != null ? itens.size() : 4,
                adicionaisElegiveis, quantidadeAdicionaisPermitidos);
    }

    // ===== GETTERS E SETTERS =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<ItemCombo> getItens() { return itens; }
    public void setItens(List<ItemCombo> itens) { this.itens = itens; }

    public BigDecimal getPrecoTotal() {
        if (precoOverride != null && precoOverride.compareTo(BigDecimal.ZERO) > 0) {
            return precoOverride;
        }
        return precoTotal;
    }
    public void setPrecoTotal(BigDecimal precoTotal) { this.precoTotal = precoTotal; }

    public int getQuantidadeMaximaDeItems() { return quantidadeMaximaDeItems; }
    public void setQuantidadeMaximaDeItems(int quantidadeMaximaDeItems) {
        this.quantidadeMaximaDeItems = quantidadeMaximaDeItems;
    }

    public int getQuantidadeMaximaDeFlavors() { return quantidadeMaximaDeFlavors; }
    public void setQuantidadeMaximaDeFlavors(int quantidadeMaximaDeFlavors) {
        this.quantidadeMaximaDeFlavors = quantidadeMaximaDeFlavors;
    }

    public BigDecimal getPrecoOverride() { return precoOverride; }
    public void setPrecoOverride(BigDecimal precoOverride) { this.precoOverride = precoOverride; }

    // ★ GETTERS E SETTERS DOS ADICIONAIS ★
    public List<Adicional> getAdicionaisElegiveis() { return adicionaisElegiveis; }
    public void setAdicionaisElegiveis(List<Adicional> adicionaisElegiveis) {
        this.adicionaisElegiveis = adicionaisElegiveis != null ? adicionaisElegiveis : new ArrayList<>();
    }

    public int getQuantidadeAdicionaisPermitidos() { return quantidadeAdicionaisPermitidos; }
    public void setQuantidadeAdicionaisPermitidos(int quantidadeAdicionaisPermitidos) {
        this.quantidadeAdicionaisPermitidos = quantidadeAdicionaisPermitidos;
    }

    // ★ MÉTODO AUXILIAR ★
    public boolean temAdicionaisElegiveis() {
        return adicionaisElegiveis != null && !adicionaisElegiveis.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Combo combo)) return false;
        return getId() == combo.getId() &&
                Objects.equals(getNome(), combo.getNome()) &&
                Objects.equals(getItens(), combo.getItens()) &&
                Objects.equals(getPrecoTotal(), combo.getPrecoTotal());
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