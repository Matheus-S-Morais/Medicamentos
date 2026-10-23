package Medicamentos.Model;

public class Fornecedor {
    private String cnpj;
    private String razaoSocial;
    private String telefone;
    private String email;
    private String cidade;
    private String estado;

    public Fornecedor(String cnpj, String razaoSocial, String telefone, String email, String cidade, String estado) {
        setCnpj(cnpj);
        setRazaoSocial(razaoSocial);
        setTelefone(telefone);
        setEmail(email);
        setCidade(cidade);
        setEstado(estado);
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        if (!cnpjValido(cnpj)) {
            throw new IllegalArgumentException("CNPJ inválido.");
        }
        this.cnpj = cnpj.replaceAll("\\D", "");
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        if (razaoSocial == null || razaoSocial.trim().length() < 2) {
            throw new IllegalArgumentException("A razão social deve ter pelo menos 2 caracteres.");
        }
        this.razaoSocial = razaoSocial.trim();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = textoObrigatorio(telefone, "Telefone");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String valor = textoObrigatorio(email, "E-mail");
        if (!valor.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        this.email = valor;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = textoObrigatorio(cidade, "Cidade");
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        String valor = textoObrigatorio(estado, "Estado").toUpperCase();
        if (valor.length() != 2) {
            throw new IllegalArgumentException("O estado deve ser informado com a sigla de 2 letras.");
        }
        this.estado = valor;
    }

    private String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return valor.trim();
    }

    public static boolean cnpjValido(String cnpj) {
        if (cnpj == null) {
            return false;
        }
        String numero = cnpj.replaceAll("\\D", "");
        if (!numero.matches("\\d{14}") || numero.chars().distinct().count() == 1) {
            return false;
        }

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;

        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(numero.charAt(i)) * pesos1[i];
        }

        int resto = soma % 11;
        int digito1 = resto < 2 ? 0 : 11 - resto;

        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(numero.charAt(i)) * pesos2[i];
        }

        resto = soma % 11;
        int digito2 = resto < 2 ? 0 : 11 - resto;

        return Character.getNumericValue(numero.charAt(12)) == digito1
                && Character.getNumericValue(numero.charAt(13)) == digito2;
    }
}
