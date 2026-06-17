package br.ufersa.academia.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.ufersa.academia.model.Instrutor;

public class InstrutorDAO {

    public boolean cadastrar(Instrutor instrutor) {
        String sql = "INSERT INTO instrutores (nome, cpf, login, senha, ativo, eh_gerente) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBanco.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, instrutor.getNome());
            stmt.setString(2, instrutor.getCpf());
            stmt.setString(3, instrutor.getLogin());
            stmt.setString(4, instrutor.getSenha());
            stmt.setBoolean(5, instrutor.isAtivo());
            stmt.setBoolean(6, instrutor.isEhGerente());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("🔴 Erro ao cadastrar instrutor no Supabase: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Instrutor> listar() {
        String sql = "SELECT * FROM instrutores";
        List<Instrutor> lista = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Instrutor instrutor = new Instrutor();
                instrutor.setNome(rs.getString("nome"));
                instrutor.setCpf(rs.getString("cpf"));
                instrutor.setLogin(rs.getString("login"));
                instrutor.setSenha(rs.getString("senha"));
                instrutor.setAtivo(rs.getBoolean("ativo"));
                instrutor.setEhGerente(rs.getBoolean("eh_gerente"));

                lista.add(instrutor);
            }
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao listar instrutores: " + e.getMessage());
        }
        return lista;
    }

    public boolean editar(Instrutor instrutor) {
        String sql = "UPDATE instrutores SET nome = ?, login = ?, senha = ?, ativo = ?, eh_gerente = ? WHERE cpf = ?";

        try (Connection conn = ConexaoBanco.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, instrutor.getNome());
            stmt.setString(2, instrutor.getLogin());
            stmt.setString(3, instrutor.getSenha());
            stmt.setBoolean(4, instrutor.isAtivo());
            stmt.setBoolean(5, instrutor.isEhGerente());
            stmt.setString(6, instrutor.getCpf());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("🔴 Erro ao editar instrutor: " + e.getMessage());
            return false;
        }
    }

    public boolean desativar(String cpf) {
        String sql = "UPDATE instrutores SET ativo = false WHERE cpf = ?";

        try (Connection conn = ConexaoBanco.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("🔴 Erro ao desativar instrutor: " + e.getMessage());
            return false;
        }
    }

    public Instrutor autenticar(String login, String senha) {
        String sql = "SELECT * FROM instrutores WHERE login = ? AND senha = ? AND ativo = true";

        try (Connection conn = ConexaoBanco.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Instrutor instrutor = new Instrutor();
                    instrutor.setId(rs.getInt("id"));
                    instrutor.setNome(rs.getString("nome"));
                    instrutor.setCpf(rs.getString("cpf"));
                    instrutor.setLogin(rs.getString("login"));
                    instrutor.setEhGerente(rs.getBoolean("eh_gerente"));
                    return instrutor;
                }
            }
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao autenticar: " + e.getMessage());
        }
        return null;
    }
}
