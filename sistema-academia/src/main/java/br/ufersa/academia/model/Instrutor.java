package br.ufersa.academia.model;

import java.util.List;

import br.ufersa.academia.model.dao.InstrutorDAO;

public class Instrutor extends Usuario {

    private boolean ehGerente;

    public Instrutor() {
        super();
    }

    public Instrutor(String nome, String cpf, String login, String senha, boolean ehGerente) {
        super(nome, cpf, login, senha);
        this.ehGerente = ehGerente;
    }

    public void verInstrutor() {
        super.verUsuario();
        System.out.println("Cargo: " + (this.ehGerente ? "Gerente" : "Instrutor Comum"));
    }

    public static List<Instrutor> listarInstrutores() {
        return new InstrutorDAO().listar();
    }

    public static Instrutor criarInstrutor(String nome, String cpf, String login, String senha, boolean ehGerente) {
        Instrutor instrutor = new Instrutor(nome, cpf, login, senha, ehGerente);
        new InstrutorDAO().cadastrar(instrutor);
        return instrutor;
    }

    public static Instrutor criarInstrutor(String nome, String cpf, String login, String senha, String especialidade, boolean ehGerente) {
        return criarInstrutor(nome, cpf, login, senha, ehGerente);
    }

    public static void editarInstrutor(Instrutor instrutor, String nome, String cpf, String login, String senha, boolean ativo, boolean ehGerente) {
        if (instrutor != null) {
            Usuario.editarUsuario(instrutor, nome, cpf, login, senha, ativo);
            instrutor.setEhGerente(ehGerente);
            new InstrutorDAO().editar(instrutor);
        }
    }

    public static void editarInstrutor(Instrutor instrutor, String nome, String cpf, String login, String senha, boolean ativo, String especialidade, boolean ehGerente) {
        editarInstrutor(instrutor, nome, cpf, login, senha, ativo, ehGerente);
    }

    public static void excluirInstrutor(Instrutor instrutor) {
        if (instrutor != null && instrutor.getCpf() != null && !instrutor.getCpf().isBlank()) {
            new InstrutorDAO().desativar(instrutor.getCpf());
            instrutor.setAtivo(false);
        }
    }

    public boolean isEhGerente() {
        return ehGerente;
    }

    public void setEhGerente(boolean ehGerente) {
        this.ehGerente = ehGerente;
    }
}
