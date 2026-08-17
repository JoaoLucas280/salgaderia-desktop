package salgaderia.model;

import salgaderia.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Pedido {

    private int id;
    private String nomeCliente;
    private String telefone;
    private String endereco;
    private BigDecimal taxaEntrega;
    private List<ItemPedido> itens;
    private BigDecimal total;
    private LocalDateTime dataHora;
    private StatusPedido status;
    private String formaPagamento;

    public Pedido() {
        this.status = StatusPedido.PENDENTE;
    }

    public Pedido(int id, String nomeCliente, List<ItemPedido> itens,
                  BigDecimal total, LocalDateTime dataHora) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.itens = itens;
        this.total = total;
        this.dataHora = dataHora;
        this.status = StatusPedido.PENDENTE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
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

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pedido pedido)) return false;
        return Objects.equals(getId(), pedido.getId()) && Objects.equals(getNomeCliente(), pedido.getNomeCliente()) && Objects.equals(getTelefone(), pedido.getTelefone()) && Objects.equals(getEndereco(), pedido.getEndereco()) && Objects.equals(getTaxaEntrega(), pedido.getTaxaEntrega()) && Objects.equals(getItens(), pedido.getItens()) && Objects.equals(getTotal(), pedido.getTotal()) && Objects.equals(getDataHora(), pedido.getDataHora()) && getStatus() == pedido.getStatus() && Objects.equals(getFormaPagamento(), pedido.getFormaPagamento());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNomeCliente(), getTelefone(), getEndereco(), getTaxaEntrega(), getItens(), getTotal(), getDataHora(), getStatus(), getFormaPagamento());
    }
}