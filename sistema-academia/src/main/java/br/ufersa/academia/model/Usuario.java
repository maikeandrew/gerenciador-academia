package br.ufersa.academia.model;

import java.util.List;

public abstract class Usuario {
    private int id;
    private String nome;
    private String cpf;
    private String login;
    private String senha;
    private boolean ativo;

    public Usuario() {
    }

    public Usuario(String nome, String cpf, String login, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.login = login;
        this.senha = senha;
        this.ativo = true; 
    }


    public void verUsuario() {
        System.out.println("Nome: " + this.nome);
        System.out.println("CPF: " + this.cpf);
        System.out.println("Login: " + this.login);
        System.out.println("Status: " + (this.ativo ? "Ativo" : "Inativo"));
    }

    public void desativarUsuario(Usuario usuario) {
        if (usuario != null) {
            usuario.setAtivo(false);
        }
    }

    public boolean autenticar(String login, String senha) {
        return this.login.equals(login) && this.senha.equals(senha) && this.ativo;
    }


    public static List<Usuario> listarUsuarios() {
        return null;
    }

    public static Usuario criarUsuario(String nome, String cpf, String login, String senha) {
        return null;
    }

    public static void editarUsuario(Usuario usuario, String nome, String cpf, String login, String senha, boolean ativo) {
        if (usuario != null) {
            usuario.setNome(nome);
            usuario.setCpf(cpf);
            usuario.setLogin(login);
            usuario.setSenha(senha);
            usuario.setAtivo(ativo);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}