package salgaderia.model;

import java.math.BigDecimal;
import java.util.List;

public class Oferta {
    private int id;
    private String nome;
    private List<ItemCombo> itens;
    private BigDecimal precoTotal;
    private int quantidadeMaximaDeItems;    // Ex: 50 itens no total
    private int quantidadeMaximaDeFlavors;  // Ex: máximo 3 sabores diferentes
    private boolean ativo;

    public Oferta() {}


    public Oferta(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal,
                  int quantidadeMaximaDeItems, int quantidadeMaximaDeFlavors, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.itens = itens;
        this.precoTotal = precoTotal;
        this.quantidadeMaximaDeItems = quantidadeMaximaDeItems;
        this.quantidadeMaximaDeFlavors = quantidadeMaximaDeFlavors;
        this.ativo = ativo;
    }

    public Oferta(int id, String nome, List<ItemCombo> itens, BigDecimal precoTotal) {
        this(id, nome, itens, precoTotal, 50, 3, true);
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<ItemCombo> getItens() { return itens; }
    public void setItens(List<ItemCombo> itens) { this.itens = itens; }

    public BigDecimal getPrecoTotal() { return precoTotal; }
    public void setPrecoTotal(BigDecimal precoTotal) { this.precoTotal = precoTotal; }

    public int getQuantidadeMaximaDeItems() { return quantidadeMaximaDeItems; }
    public void setQuantidadeMaximaDeItems(int quantidadeMaximaDeItems) {
        this.quantidadeMaximaDeItems = quantidadeMaximaDeItems;
    }

    public int getQuantidadeMaximaDeFlavors() { return quantidadeMaximaDeFlavors; }
    public void setQuantidadeMaximaDeFlavors(int quantidadeMaximaDeFlavors) {
        this.quantidadeMaximaDeFlavors = quantidadeMaximaDeFlavors;
    }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public int getMaximoItems() { return quantidadeMaximaDeItems; }
    public int getMaximoFlavors() { return quantidadeMaximaDeFlavors; }

    @Override
    public String toString() {
        return nome + " (R$ " + String.format("%.2f", precoTotal.doubleValue()) + ")";
    }
}