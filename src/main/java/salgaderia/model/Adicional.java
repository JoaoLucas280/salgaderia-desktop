package salgaderia.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Adicional {
    private int id;
    private String nome;
    private BigDecimal preco;

    public Adicional() {
    }

    public Adicional(int id, String nome, BigDecimal preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Adicional adicional)) return false;
        return getId() == adicional.getId() && Objects.equals(getNome(), adicional.getNome()) && 
               Objects.equals(getPreco(), adicional.getPreco());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNome(), getPreco());
    }

    @Override
    public String toString() {
        return nome + " (R$ " + String.format("%.2f", preco.doubleValue()) + ")";
    }
}
