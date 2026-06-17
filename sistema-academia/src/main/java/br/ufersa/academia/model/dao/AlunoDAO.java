package br.ufersa.academia.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.ufersa.academia.model.Aluno;

public class AlunoDAO {

    
    public boolean cadastrar(Aluno aluno) {
        String sql = "INSERT INTO alunos (nome, cpf, login, senha, ativo, matricula, valor_mensal, data_fim_matricula, instrutor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getCpf());
            stmt.setString(3, aluno.getLogin());
            stmt.setString(4, aluno.getSenha());
            stmt.setBoolean(5, aluno.isAtivo());
            stmt.setString(6, aluno.getMatricula());
            stmt.setDouble(7, aluno.getValorMensal());
            
            if (aluno.getDataFimMatricula() != null) {
                stmt.setDate(8, java.sql.Date.valueOf(aluno.getDataFimMatricula()));
            } else {
                stmt.setNull(8, java.sql.Types.DATE);
            }
            
            stmt.setInt(9, aluno.getIdInstrutorResponsavel());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            
            if (linhasAfetadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        
                        aluno.setId(generatedKeys.getInt(1)); 
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao cadastrar aluno: " + e.getMessage());
            return false;
        }
    }

    
    public List<Aluno> listar() {
        String sql = "SELECT * FROM alunos";
        List<Aluno> listaAlunos = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id"));
                aluno.setNome(rs.getString("nome"));
                aluno.setCpf(rs.getString("cpf"));
                aluno.setLogin(rs.getString("login"));
                aluno.setSenha(rs.getString("senha"));
                aluno.setAtivo(rs.getBoolean("ativo"));
                aluno.setMatricula(rs.getString("matricula"));
                aluno.setValorMensal(rs.getDouble("valor_mensal"));
                
                if (rs.getDate("data_fim_matricula") != null) {
                    aluno.setDataFimMatricula(rs.getDate("data_fim_matricula").toLocalDate());
                }
                
                aluno.setIdInstrutorResponsavel(rs.getInt("instrutor_id"));

                listaAlunos.add(aluno);
            }
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao listar alunos: " + e.getMessage());
        }
        return listaAlunos;
    }

    
    public boolean editar(Aluno aluno) {
        String sql = "UPDATE alunos SET nome = ?, login = ?, senha = ?, ativo = ?, matricula = ?, valor_mensal = ?, data_fim_matricula = ?, instrutor_id = ? WHERE cpf = ?";
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getLogin());
            stmt.setString(3, aluno.getSenha());
            stmt.setBoolean(4, aluno.isAtivo());
            stmt.setString(5, aluno.getMatricula());
            stmt.setDouble(6, aluno.getValorMensal());
            
            if (aluno.getDataFimMatricula() != null) {
                stmt.setDate(7, java.sql.Date.valueOf(aluno.getDataFimMatricula()));
            } else {
                stmt.setNull(7, java.sql.Types.DATE);
            }
            
            stmt.setInt(8, aluno.getIdInstrutorResponsavel());
            stmt.setString(9, aluno.getCpf()); 
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao editar aluno: " + e.getMessage());
            return false;
        }
    }
    
    
    public boolean possuiPagamentoAtrasado(String cpf) {
        String sql = "SELECT COUNT(*) FROM pagamentos p " +
                     "JOIN alunos a ON p.aluno_id = a.id " +
                     "WHERE a.cpf = ? AND p.status = 'ATRASADO'";
                     
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao verificar pendências do aluno: " + e.getMessage());
        }
        return false;
    }

   
    public boolean desativar(String cpf) {
        String sql = "UPDATE alunos SET ativo = false WHERE cpf = ?";
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao desativar aluno: " + e.getMessage());
            return false;
        }
    }

    
    public Aluno autenticar(String login, String senha) {
        String sql = "SELECT * FROM alunos WHERE login = ? AND senha = ? AND ativo = true";
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Aluno aluno = new Aluno();
                    aluno.setId(rs.getInt("id"));
                    aluno.setNome(rs.getString("nome"));
                    aluno.setCpf(rs.getString("cpf"));
                    aluno.setLogin(rs.getString("login"));
                    aluno.setMatricula(rs.getString("matricula"));
                    aluno.setValorMensal(rs.getDouble("valor_mensal")); 
                    

                    aluno.setAtivo(rs.getBoolean("ativo")); 
                    
                    
                    if (rs.getDate("data_fim_matricula") != null) {
                        aluno.setDataFimMatricula(rs.getDate("data_fim_matricula").toLocalDate());
                    }
                    
                    return aluno; 
                }
            }
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao autenticar aluno: " + e.getMessage());
        }
        return null; 
    }
}