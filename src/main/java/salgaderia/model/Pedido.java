package salgaderia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Pedido {

    private Long id;
    private String nomeCliente;
    private String telefone;
    private String endereco;
    private BigDecimal taxaEntrega;
    private List<Item> itens;
    private BigDecimal total;
    private LocalDateTime dataHora;

    public Pedido() {
    }

    public Pedido(Long id, String nomeCliente,List<Item> itens,
                  BigDecimal total, LocalDateTime dataHora) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.itens = itens;
        this.total = total;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public BigDecimal getTaxaEntrega() {
        return taxaEntrega;
    }

    public void setTaxaEntrega(BigDecimal taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }

    public List<Item> getItens() {
        return itens;
    }

    public void setItens(List<Item> itens) {
        this.itens = itens;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pedido pedido)) return false;
        return Objects.equals(getId(), pedido.getId()) && Objects.equals(getNomeCliente(), pedido.getNomeCliente()) && Objects.equals(getTelefone(), pedido.getTelefone()) && Objects.equals(getEndereco(), pedido.getEndereco()) && Objects.equals(getTaxaEntrega(), pedido.getTaxaEntrega()) && Objects.equals(getItens(), pedido.getItens()) && Objects.equals(getTotal(), pedido.getTotal()) && Objects.equals(getDataHora(), pedido.getDataHora());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNomeCliente(), getTelefone(), getEndereco(), getTaxaEntrega(), getItens(), getTotal(), getDataHora());
    }
}
