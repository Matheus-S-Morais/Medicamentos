package Medicamentos.View;

import Medicamentos.Model.Fornecedor;
import Medicamentos.Model.Medicamento;
import Medicamentos.Service.ArquivoCSV;
import Medicamentos.Service.EstoqueService;
import Medicamentos.Service.RelatorioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TelaPrincipal {
    private final EstoqueService estoqueService;
    private final RelatorioService relatorioService;
    private final ObservableList<Medicamento> dados = FXCollections.observableArrayList();
    private TableView<Medicamento> tabela;
    private TextField codigo;
    private TextField nome;
    private TextField descricao;
    private TextField principioAtivo;
    private DatePicker validade;
    private TextField quantidade;
    private TextField preco;
    private CheckBox controlado;
    private TextField cnpj;
    private TextField razaoSocial;
    private TextField telefone;
    private TextField email;
    private TextField cidade;
    private TextField estado;

    public TelaPrincipal() {
        try {
            estoqueService = new EstoqueService(new ArquivoCSV("medicamentos.csv"));
            relatorioService = new RelatorioService(estoqueService);
            dados.setAll(estoqueService.listar());
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível carregar medicamentos.csv: " + e.getMessage(), e);
        }
    }

    public Parent criarTela() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(15));

        Label titulo = new Label("Estoque de Medicamentos");
        titulo.setFont(Font.font(22));
        raiz.setTop(titulo);

        VBox formulario = criarFormulario();
        formulario.setPrefWidth(330);
        raiz.setLeft(formulario);

        VBox centro = new VBox(10, criarTabela(), criarBotoesRelatorio());
        centro.setPadding(new Insets(0, 0, 0, 15));
        VBox.setVgrow(tabela, Priority.ALWAYS);
        raiz.setCenter(centro);

        atualizarTabela();
        return raiz;
    }

    private VBox criarFormulario() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(15, 15, 15, 0));

        codigo = campo("Código (7 caracteres)");
        nome = campo("Nome");
        descricao = campo("Descrição");
        principioAtivo = campo("Princípio ativo");
        validade = new DatePicker(LocalDate.now().plusDays(1));
        quantidade = campo("Quantidade em estoque");
        preco = campo("Preço (ex.: 12.50)");
        controlado = new CheckBox("Medicamento controlado");
        cnpj = campo("CNPJ do fornecedor");
        razaoSocial = campo("Razão social");
        telefone = campo("Telefone");
        email = campo("E-mail");
        cidade = campo("Cidade");
        estado = campo("Estado (UF)");

        box.getChildren().addAll(
                new Label("Dados do medicamento"), codigo, nome, descricao, principioAtivo,
                new Label("Data de validade"), validade, quantidade, preco, controlado,
                new Separator(), new Label("Dados do fornecedor"), cnpj, razaoSocial,
                telefone, email, cidade, estado
        );

        Button cadastrar = new Button("Cadastrar medicamento");
        cadastrar.setMaxWidth(Double.MAX_VALUE);
        cadastrar.setOnAction(e -> cadastrar());
        Button excluir = new Button("Excluir selecionado");
        excluir.setMaxWidth(Double.MAX_VALUE);
        excluir.setOnAction(e -> excluirSelecionado());
        Button consultar = new Button("Consultar por código");
        consultar.setMaxWidth(Double.MAX_VALUE);
        consultar.setOnAction(e -> consultar());

        box.getChildren().addAll(cadastrar, excluir, consultar);
        return box;
    }

    private TextField campo(String dica) {
        TextField campo = new TextField();
        campo.setPromptText(dica);
        return campo;
    }

    private TableView<Medicamento> criarTabela() {
        tabela = new TableView<>();
        tabela.setItems(dados);

        TableColumn<Medicamento, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCodigo()));

        TableColumn<Medicamento, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNome()));

        TableColumn<Medicamento, String> colAtivo = new TableColumn<>("Princípio ativo");
        colAtivo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPrincipioAtivo()));

        TableColumn<Medicamento, String> colValidade = new TableColumn<>("Validade");
        colValidade.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDataValidade().toString()));

        TableColumn<Medicamento, String> colQuantidade = new TableColumn<>("Estoque");
        colQuantidade.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getQuantidadeEstoque())));

        TableColumn<Medicamento, String> colPreco = new TableColumn<>("Preço");
        colPreco.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("R$ " + c.getValue().getPreco().toPlainString()));

        TableColumn<Medicamento, String> colFornecedor = new TableColumn<>("Fornecedor");
        colFornecedor.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFornecedor().getRazaoSocial()));

        tabela.getColumns().addAll(colCodigo, colNome, colAtivo, colValidade, colQuantidade, colPreco, colFornecedor);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tabela;
    }

    private HBox criarBotoesRelatorio() {
        Button vencimento = new Button("Próximos do vencimento");
        vencimento.setOnAction(e -> mostrarLista("Medicamentos com validade nos próximos 30 dias", relatorioService.proximosDoVencimento()));

        Button baixo = new Button("Estoque baixo");
        baixo.setOnAction(e -> mostrarLista("Medicamentos com menos de 5 unidades", relatorioService.estoqueBaixo()));

        Button fornecedor = new Button("Valor por fornecedor");
        fornecedor.setOnAction(e -> mostrarValorFornecedor());

        Button controladosBtn = new Button("Controlados");
        controladosBtn.setOnAction(e -> mostrarControlados());

        Button total = new Button("Valor total do estoque");
        total.setOnAction(e -> alerta("Valor total do estoque", "Total: R$ " + relatorioService.valorTotalEstoque().toPlainString()));

        return new HBox(8, vencimento, baixo, fornecedor, controladosBtn, total);
    }

    private void cadastrar() {
        try {
            Fornecedor fornecedor = new Fornecedor(
                    cnpj.getText(), razaoSocial.getText(), telefone.getText(),
                    email.getText(), cidade.getText(), estado.getText()
            );

            BigDecimal valor = new BigDecimal(preco.getText().trim().replace(",", "."));
            Medicamento medicamento = new Medicamento(
                    codigo.getText(), nome.getText(), descricao.getText(), principioAtivo.getText(),
                    validade.getValue(), Integer.parseInt(quantidade.getText().trim()),
                    valor, controlado.isSelected(), fornecedor
            );

            estoqueService.cadastrar(medicamento);
            atualizarTabela();
            limparCampos();
            alerta("Cadastro realizado", "Medicamento cadastrado com sucesso.");
        } catch (NumberFormatException e) {
            alerta("Dados inválidos", "Informe quantidade inteira e preço numérico válido.");
        } catch (IllegalArgumentException | IOException e) {
            alerta("Não foi possível cadastrar", e.getMessage());
        }
    }

    private void excluirSelecionado() {
        Medicamento selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alerta("Seleção necessária", "Selecione um medicamento na tabela.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir " + selecionado.getNome() + "?");
        confirmacao.setContentText("Esta operação será salva no arquivo CSV.");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                estoqueService.excluir(selecionado.getCodigo());
                atualizarTabela();
                alerta("Exclusão realizada", "Medicamento excluído.");
            } catch (IOException | IllegalArgumentException e) {
                alerta("Erro", e.getMessage());
            }
        }
    }

    private void consultar() {
        String busca = codigo.getText().trim();
        if (busca.isEmpty()) {
            alerta("Código obrigatório", "Digite o código do medicamento no campo Código.");
            return;
        }

        estoqueService.buscar(busca).ifPresentOrElse(
                m -> {
                    tabela.getSelectionModel().select(m);
                    tabela.scrollTo(m);
                    alerta("Medicamento encontrado",
                            "Nome: " + m.getNome()
                                    + "\nCódigo: " + m.getCodigo()
                                    + "\nPrincípio ativo: " + m.getPrincipioAtivo()
                                    + "\nValidade: " + m.getDataValidade()
                                    + "\nEstoque: " + m.getQuantidadeEstoque()
                                    + "\nPreço: R$ " + m.getPreco().toPlainString()
                                    + "\nControlado: " + (m.isControlado() ? "Sim" : "Não")
                                    + "\nFornecedor: " + m.getFornecedor().getRazaoSocial());
                },
                () -> alerta("Não encontrado", "Não existe medicamento com esse código.")
        );
    }

    private void mostrarLista(String titulo, List<Medicamento> lista) {
        if (lista.isEmpty()) {
            alerta(titulo, "Nenhum medicamento encontrado.");
            return;
        }
        String texto = lista.stream()
                .map(m -> m.getCodigo() + " - " + m.getNome()
                        + " | Estoque: " + m.getQuantidadeEstoque()
                        + " | Validade: " + m.getDataValidade())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
        alerta(titulo, texto);
    }

    private void mostrarValorFornecedor() {
        Map<String, BigDecimal> valores = relatorioService.valorPorFornecedor();
        String texto = valores.entrySet().stream()
                .map(e -> e.getKey() + ": R$ " + e.getValue().toPlainString())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("Nenhum dado disponível.");
        alerta("Valor do estoque por fornecedor", texto);
    }

    private void mostrarControlados() {
        Map<Boolean, List<Medicamento>> grupos = relatorioService.controlados();
        String texto = "Controlados:\n" + formatarNomes(grupos.get(true))
                + "\n\nNão controlados:\n" + formatarNomes(grupos.get(false));
        alerta("Medicamentos controlados e não controlados", texto);
    }

    private String formatarNomes(List<Medicamento> lista) {
        return lista.stream()
                .map(m -> m.getCodigo() + " - " + m.getNome())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("Nenhum");
    }

    private void atualizarTabela() {
        dados.setAll(estoqueService.listar());
    }

    private void limparCampos() {
        codigo.clear();
        nome.clear();
        descricao.clear();
        principioAtivo.clear();
        validade.setValue(LocalDate.now().plusDays(1));
        quantidade.clear();
        preco.clear();
        controlado.setSelected(false);
        cnpj.clear();
        razaoSocial.clear();
        telefone.clear();
        email.clear();
        cidade.clear();
        estado.clear();
    }

    private void alerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.getDialogPane().setMinWidth(400);
        alert.showAndWait();
    }
}
