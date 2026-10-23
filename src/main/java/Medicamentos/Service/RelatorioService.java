package Medicamentos.Service;

import Medicamentos.Model.Medicamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioService {
    private final EstoqueService estoqueService;

    public RelatorioService(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    public List<Medicamento> proximosDoVencimento() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30);

        return estoqueService.listar().stream()
                .filter(m -> !m.getDataValidade().isBefore(hoje))
                .filter(m -> !m.getDataValidade().isAfter(limite))
                .toList();
    }

    public List<Medicamento> estoqueBaixo() {
        return estoqueService.listar().stream()
                .filter(m -> m.getQuantidadeEstoque() < 5)
                .toList();
    }

    public Map<String, BigDecimal> valorPorFornecedor() {
        return estoqueService.listar().stream()
                .collect(Collectors.groupingBy(
                        m -> m.getFornecedor().getRazaoSocial(),
                        Collectors.mapping(
                                Medicamento::getValorTotal,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

    public Map<Boolean, List<Medicamento>> controlados() {
        return estoqueService.listar().stream()
                .collect(Collectors.partitioningBy(Medicamento::isControlado));
    }

    public BigDecimal valorTotalEstoque() {
        return estoqueService.listar().stream()
                .map(Medicamento::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
