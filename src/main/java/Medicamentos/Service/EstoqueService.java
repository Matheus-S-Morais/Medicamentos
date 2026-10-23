package Medicamentos.Service;

import Medicamentos.Model.Medicamento;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EstoqueService {
    private final List<Medicamento> medicamentos;
    private final ArquivoCSV arquivo;

    public EstoqueService(ArquivoCSV arquivo) throws IOException {
        this.arquivo = arquivo;
        this.medicamentos = new ArrayList<>(arquivo.carregar());
        validarCodigosCarregados();
    }

    private void validarCodigosCarregados() throws IOException {
        long distintos = medicamentos.stream().map(Medicamento::getCodigo).distinct().count();
        if (distintos != medicamentos.size()) {
            throw new IOException("O arquivo CSV contém códigos duplicados.");
        }
    }

    public void cadastrar(Medicamento medicamento) throws IOException {
        if (buscar(medicamento.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Já existe um medicamento com esse código.");
        }
        medicamentos.add(medicamento);
        salvarComSeguranca();
    }

    public void excluir(String codigo) throws IOException {
        Medicamento medicamento = buscar(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado."));
        medicamentos.remove(medicamento);
        salvarComSeguranca();
    }

    public Optional<Medicamento> buscar(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return medicamentos.stream()
                .filter(m -> m.getCodigo().equalsIgnoreCase(codigo.trim()))
                .findFirst();
    }

    public List<Medicamento> listar() {
        return medicamentos.stream()
                .sorted(Comparator.comparing(Medicamento::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public void salvarComSeguranca() throws IOException {
        arquivo.salvar(medicamentos);
    }
}
