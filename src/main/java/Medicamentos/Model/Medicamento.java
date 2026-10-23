package Medicamentos.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class Medicamento {
    private String codigo;
    private String nome;
    private String descricao;
    private String principioAtivo;
    private LocalDate dataValidade;
    private int quantidadeEstoque;
    private BigDecimal preco;
    private boolean controlado;
    private Fornecedor fornecedor;

    public Medicamento(String codigo, String nome, String descricao, String principioAtivo,
                       LocalDate dataValidade, int quantidadeEstoque, BigDecimal preco,
                       boolean controlado, Fornecedor fornecedor) {
        setCodigo(codigo);
        setNome(nome);
        setDescricao(descricao);
        setPrincipioAtivo(principioAtivo);
        setDataValidade(dataValidade);
        setQuantidadeEstoque(quantidadeEstoque);
        setPreco(preco);
        setControlado(controlado);
        setFornecedor(fornecedor);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || !Pattern.matches("[A-Za-z0-9]{7}", codigo.trim())) {
            throw new IllegalArgumentException("O código deve conter exatamente 7 caracteres alfanuméricos.");
        }
        this.codigo = codigo.trim().toUpperCase();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().length() < 2) {
            throw new IllegalArgumentException("O nome deve ter pelo menos 2 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getDescricao() {
        return textoObrigatorio(descricao, "Descrição");
    }

    public void setDescricao(String descricao) {
        this.descricao = textoObrigatorio(descricao, "Descrição");
    }

    public String getPrincipioAtivo() {
        return principioAtivo;
    }

    public void setPrincipioAtivo(String principioAtivo) {
        this.principioAtivo = textoObrigatorio(principioAtivo, "Princípio ativo");
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        if (dataValidade == null || dataValidade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A validade não pode ser uma data passada.");
        }
        this.dataValidade = dataValidade;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException("A quantidade em estoque não pode ser negativa.");
        }
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        this.preco = preco;
    }

    public boolean isControlado() {
        return controlado;
    }

    public void setControlado(boolean controlado) {
        this.controlado = controlado;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        if (fornecedor == null) {
            throw new IllegalArgumentException("O fornecedor é obrigatório.");
        }
        this.fornecedor = fornecedor;
    }

    public BigDecimal getValorTotal() {
        return preco.multiply(BigDecimal.valueOf(quantidadeEstoque));
    }

    private String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return valor.trim();
    }
}
