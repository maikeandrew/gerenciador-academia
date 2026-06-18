package br.ufersa.academia.model;

import java.util.List;

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
        
        return null;
    }

    public static void editarInstrutor(Instrutor instrutor, String nome, String cpf, String login, String senha, boolean ativo, boolean ehGerente) {
        if (instrutor != null) {
           
            Usuario.editarUsuario(instrutor, nome, cpf, login, senha, ativo);
           
            instrutor.setEhGerente(ehGerente);
        }
    }

  

    public boolean isEhGerente() {
        return ehGerente;
    }

    public void setEhGerente(boolean ehGerente) {
        this.ehGerente = ehGerente;
    }
}
