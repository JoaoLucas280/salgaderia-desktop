package salgaderia.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class Combo {
    private int id;
    private String nome;
    private List<ItemCombo> itens;
    private BigDecimal precoTotal;
    private int quantidadeMaximaDeItems;
    private int quantidadeMaximaDeFlavors;
    private BigDecimal precoOverride;


    private String adicionaisElegiveisIds; // ex: "1,2,3"
    private int quantidadeAdicionaisPermitidos;


    private transient List<Adicional> adicionaisElegiveisCache;

    public Combo() {
        this.adicionaisElegiveisIds = "";
        this.quantidadeAdicionaisPermitidos = 0;
        this.adicionaisElegiveisCache = new ArrayList<>();
    }

    public Combo(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal) {
        this.id = id;
        this.nome = nome;
        this.itens = itens;
        this.precoTotal = precoTotal;
        this.quantidadeMaximaDeItems = 100;
        this.quantidadeMaximaDeFlavors = itens != null ? itens.size() : 4;
        this.adicionaisElegiveisIds = "";
        this.quantidadeAdicionaisPermitidos = 0;
        this.adicionaisElegiveisCache = new ArrayList<>();
    }

    public Combo(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal,
                 int quantidadeMaximaDeItems, int quantidadeMaximaDeFlavors,
                 List<Adicional> adicionaisElegiveis, int quantidadeAdicionaisPermitidos) {
        this.id = id;
        this.nome = nome;
        this.itens = itens;
        this.precoTotal = precoTotal;
        this.quantidadeMaximaDeItems = quantidadeMaximaDeItems;
        this.quantidadeMaximaDeFlavors = quantidadeMaximaDeFlavors;
        this.quantidadeAdicionaisPermitidos = quantidadeAdicionaisPermitidos;
        this.adicionaisElegiveisCache = adicionaisElegiveis != null ? adicionaisElegiveis : new ArrayList<>();
        // ★ CONVERTE PARA STRING ★
        this.adicionaisElegiveisIds = this.adicionaisElegiveisCache.stream()
                .map(a -> String.valueOf(a.getId()))
                .collect(Collectors.joining(","));
    }

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

    public int getQuantidadeAdicionaisPermitidos() { return quantidadeAdicionaisPermitidos; }
    public void setQuantidadeAdicionaisPermitidos(int quantidadeAdicionaisPermitidos) {
        this.quantidadeAdicionaisPermitidos = quantidadeAdicionaisPermitidos;
    }


    @JsonProperty
    public String getAdicionaisElegiveisIds() {
        return adicionaisElegiveisIds != null ? adicionaisElegiveisIds : "";
    }

    @JsonProperty
    public void setAdicionaisElegiveisIds(String adicionaisElegiveisIds) {
        this.adicionaisElegiveisIds = adicionaisElegiveisIds != null ? adicionaisElegiveisIds : "";
    }


    public List<Adicional> getAdicionaisElegiveis() {
        return adicionaisElegiveisCache != null ? adicionaisElegiveisCache : new ArrayList<>();
    }

    public void setAdicionaisElegiveis(List<Adicional> adicionaisElegiveis) {
        this.adicionaisElegiveisCache = adicionaisElegiveis != null ? adicionaisElegiveis : new ArrayList<>();

        this.adicionaisElegiveisIds = this.adicionaisElegiveisCache.stream()
                .map(a -> String.valueOf(a.getId()))
                .collect(Collectors.joining(","));
    }


    public void carregarAdicionaisDoIds(List<Adicional> todosAdicionais) {
        if (adicionaisElegiveisIds == null || adicionaisElegiveisIds.isEmpty()) {
            this.adicionaisElegiveisCache = new ArrayList<>();
            return;
        }

        List<Integer> ids = Arrays.stream(adicionaisElegiveisIds.split(","))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        this.adicionaisElegiveisCache = todosAdicionais.stream()
                .filter(a -> ids.contains(a.getId()))
                .collect(Collectors.toList());
    }

    public boolean temAdicionaisElegiveis() {
        return adicionaisElegiveisCache != null && !adicionaisElegiveisCache.isEmpty();
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