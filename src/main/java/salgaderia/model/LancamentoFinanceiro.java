package salgaderia.model;

import salgaderia.model.enums.tipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class LancamentoFinanceiro {

    private int id;
    private tipoLancamento tipo;
    private String categoria;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private String formaPagamento;
    private String observacao;
    private Integer pedidoId; // nullable - preenchido quando o lançamento vem de um pedido

    public LancamentoFinanceiro() {
    }

    public LancamentoFinanceiro(int id, tipoLancamento tipo, String categoria, String descricao,
                                BigDecimal valor, LocalDate data, String formaPagamento,
                                String observacao, Integer pedidoId) {
        this.id = id;
        this.tipo = tipo;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.formaPagamento = formaPagamento;
        this.observacao = observacao;
        this.pedidoId = pedidoId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public tipoLancamento getTipo() { return tipo; }
    public void setTipo(tipoLancamento tipo) { this.tipo = tipo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LancamentoFinanceiro that)) return false;
        return getId() == that.getId() &&
                getTipo() == that.getTipo() &&
                Objects.equals(getCategoria(), that.getCategoria()) &&
                Objects.equals(getDescricao(), that.getDescricao()) &&
                Objects.equals(getValor(), that.getValor()) &&
                Objects.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTipo(), getCategoria(), getDescricao(), getValor(), getData());
    }
}