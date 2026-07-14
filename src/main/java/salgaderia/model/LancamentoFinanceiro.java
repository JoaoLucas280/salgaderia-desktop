package salgaderia.model;

import salgaderia.model.enums.tipoLancamentoFinanceiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class LancamentoFinanceiro {

    private Long id;
    private tipoLancamentoFinanceiro tipo;
    private String categoria;
    private String descricao;
    private BigDecimal valor;
    private LocalDateTime data;
    private String formaDePagamento;
    private String observacao;

    public LancamentoFinanceiro() {
    }

    public LancamentoFinanceiro(Long id, tipoLancamentoFinanceiro tipo, String categoria,
                                String descricao, BigDecimal valor, LocalDateTime data) {
        this.id = id;
        this.tipo = tipo;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public tipoLancamentoFinanceiro getTipo() {
        return tipo;
    }

    public void setTipo(tipoLancamentoFinanceiro tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(String formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LancamentoFinanceiro that)) return false;
        return Objects.equals(getId(), that.getId()) && getTipo() == that.getTipo() && Objects.equals(getCategoria(), that.getCategoria()) && Objects.equals(getDescricao(), that.getDescricao()) && Objects.equals(getValor(), that.getValor()) && Objects.equals(getData(), that.getData()) && Objects.equals(getFormaDePagamento(), that.getFormaDePagamento()) && Objects.equals(getObservacao(), that.getObservacao());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTipo(), getCategoria(), getDescricao(), getValor(), getData(), getFormaDePagamento(), getObservacao());
    }
}
