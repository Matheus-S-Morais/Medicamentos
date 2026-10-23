package Medicamentos.Service;

import Medicamentos.Model.Fornecedor;
import Medicamentos.Model.Medicamento;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArquivoCSV {
    private final Path caminho;

    public ArquivoCSV(String nomeArquivo) {
        caminho = Path.of(nomeArquivo);
    }

    public List<Medicamento> carregar() throws IOException {
        List<Medicamento> medicamentos = new ArrayList<>();

        if (!Files.exists(caminho)) {
            return medicamentos;
        }

        try (BufferedReader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha;
            int numeroLinha = 0;

            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank() || numeroLinha == 1 && linha.startsWith("codigo;")) {
                    continue;
                }

                try {
                    String[] c = linha.split(";", -1);
                    if (c.length != 15) {
                        throw new IllegalArgumentException("A linha deve conter 15 campos.");
                    }

                    Fornecedor fornecedor = new Fornecedor(
                            c[8], c[9], c[10], c[11], c[12], c[13]
                    );

                    Medicamento medicamento = new Medicamento(
                            c[0], c[1], c[2], c[3],
                            LocalDate.parse(c[4]),
                            Integer.parseInt(c[5]),
                            new BigDecimal(c[6]),
                            Boolean.parseBoolean(c[7]),
                            fornecedor
                    );

                    medicamentos.add(medicamento);
                } catch (RuntimeException e) {
                    throw new IOException("Erro ao ler a linha " + numeroLinha + " do CSV: " + e.getMessage(), e);
                }
            }
        }

        return medicamentos;
    }

    public void salvar(List<Medicamento> medicamentos) throws IOException {
        Path pasta = caminho.toAbsolutePath().getParent();
        if (pasta != null) {
            Files.createDirectories(pasta);
        }

        try (BufferedWriter escritor = Files.newBufferedWriter(caminho, StandardCharsets.UTF_8)) {
            escritor.write("codigo;nome;descricao;principioAtivo;dataValidade;quantidadeEstoque;preco;controlado;cnpj;razaoSocial;telefone;email;cidade;estado");
            escritor.newLine();

            for (Medicamento m : medicamentos) {
                List<String> campos = List.of(
                        m.getCodigo(),
                        m.getNome(),
                        m.getDescricao(),
                        m.getPrincipioAtivo(),
                        m.getDataValidade().toString(),
                        String.valueOf(m.getQuantidadeEstoque()),
                        m.getPreco().toPlainString(),
                        String.valueOf(m.isControlado()),
                        m.getFornecedor().getCnpj(),
                        m.getFornecedor().getRazaoSocial(),
                        m.getFornecedor().getTelefone(),
                        m.getFornecedor().getEmail(),
                        m.getFornecedor().getCidade(),
                        m.getFornecedor().getEstado()
                );

                escritor.write(String.join(";", campos.stream().map(this::limparCampo).toList()));
                escritor.newLine();
            }
        }
    }

    private String limparCampo(String valor) {
        return valor.replace(";", ",").replace("\n", " ").replace("\r", " ");
    }
}
