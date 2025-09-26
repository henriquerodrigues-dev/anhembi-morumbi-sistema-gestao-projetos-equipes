package util;

import java.util.regex.Pattern;

public class ValidadorUtil {

    // Regex para validação de email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Valida se o email está em formato válido
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida CPF usando algoritmo oficial
     */
    public static boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        
        // Remove formatação
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;
        
        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;
        
        try {
            // Calcula primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) primeiroDigito = 0;
            
            // Calcula segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) segundoDigito = 0;
            
            // Verifica se os dígitos calculados conferem
            return Character.getNumericValue(cpf.charAt(9)) == primeiroDigito &&
                   Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
                   
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida se o campo não está vazio
     */
    public static boolean validarCampoObrigatorio(String campo) {
        return campo != null && !campo.trim().isEmpty();
    }

    /**
     * Valida se o campo tem tamanho mínimo
     */
    public static boolean validarTamanhoMinimo(String campo, int tamanhoMinimo) {
        if (campo == null) return false;
        return campo.trim().length() >= tamanhoMinimo;
    }

    /**
     * Valida se o login é válido (apenas letras, números e underscore)
     */
    public static boolean validarLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            return false;
        }
        return login.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    /**
     * Valida se a senha tem requisitos mínimos
     */
    public static boolean validarSenha(String senha) {
        if (senha == null) return false;
        
        // Mínimo 6 caracteres
        if (senha.length() < 6) return false;
        
        // Pelo menos uma letra e um número
        boolean temLetra = senha.matches(".*[a-zA-Z].*");
        boolean temNumero = senha.matches(".*[0-9].*");
        
        return temLetra && temNumero;
    }

    /**
     * Formata CPF para exibição (000.000.000-00)
     */
    public static String formatarCPF(String cpf) {
        if (cpf == null) return "";
        
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) return cpf;
        
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9, 11);
    }

    /**
     * Remove formatação do CPF
     */
    public static String limparCPF(String cpf) {
        if (cpf == null) return "";
        return cpf.replaceAll("[^0-9]", "");
    }

    /**
     * Valida se o nome tem pelo menos nome e sobrenome
     */
    public static boolean validarNomeCompleto(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        
        String[] partes = nome.trim().split("\\s+");
        return partes.length >= 2 && partes[0].length() >= 2 && partes[1].length() >= 2;
    }
}
