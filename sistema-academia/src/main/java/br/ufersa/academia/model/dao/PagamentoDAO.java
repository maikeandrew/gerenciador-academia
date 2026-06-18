package br.ufersa.academia.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.ufersa.academia.model.Pagamento; 
import br.ufersa.academia.model.StatusPagamento;

public class PagamentoDAO {

    public boolean cadastrar(Pagamento pagamento, int idAlunoNoBanco) {
        String sql = "INSERT INTO pagamentos (aluno_id, valor, data_vencimento, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idAlunoNoBanco);
            stmt.setDouble(2, pagamento.getValor());
            stmt.setDate(3, Date.valueOf(pagamento.getDataVencimento()));
            stmt.setString(4, pagamento.getStatus().name());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pagamento.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println(" Erro ao registrar pagamento no Supabase: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Pagamento> listar() {
        String sql = "SELECT * FROM pagamentos WHERE UPPER(status::text) IN ('PENDENTE', 'ATRASADO') ORDER BY data_vencimento";
        List<Pagamento> lista = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pagamento pagamento = new Pagamento();
                pagamento.setId(rs.getInt("id")); 
                pagamento.setValor(rs.getDouble("valor"));
                
                if (rs.getDate("data_vencimento") != null) {
                    pagamento.setDataVencimento(rs.getDate("data_vencimento").toLocalDate());
                }
                if (rs.getDate("data_pagamento") != null) {
                    pagamento.setDataPagamento(rs.getDate("data_pagamento").toLocalDate());
                }
                
                pagamento.setStatus(StatusPagamento.valueOf(rs.getString("status")));
                lista.add(pagamento);
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao listar pagamentos: " + e.getMessage());
        }
        return lista;
    }

    public List<Pagamento> listarPorAluno(String cpfAluno) {
        String sql = "SELECT p.* FROM pagamentos p JOIN alunos a ON p.aluno_id = a.id WHERE a.cpf = ?";
        List<Pagamento> lista = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpfAluno);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pagamento pagamento = new Pagamento();
                    pagamento.setId(rs.getInt("id"));
                    pagamento.setValor(rs.getDouble("valor"));
                    
                    if (rs.getDate("data_vencimento") != null) {
                        pagamento.setDataVencimento(rs.getDate("data_vencimento").toLocalDate());
                    }
                    if (rs.getDate("data_pagamento") != null) {
                        pagamento.setDataPagamento(rs.getDate("data_pagamento").toLocalDate());
                    }
                    
                    pagamento.setStatus(StatusPagamento.valueOf(rs.getString("status")));
                    lista.add(pagamento);
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao listar histórico do aluno: " + e.getMessage());
        }
        return lista;
    }

    public boolean confirmarPagamento(int idPagamentoNoBanco) {
        String sql = "UPDATE pagamentos SET status = 'PAGO', data_pagamento = CURRENT_DATE WHERE id = ?";
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPagamentoNoBanco);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao confirmar pagamento: " + e.getMessage());
            return false;
        }
    }

    public boolean editar(Pagamento pagamento) {
        String sql = "UPDATE pagamentos SET valor = ?, data_vencimento = ?, data_pagamento = ?, status = ? WHERE id = ?";

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, pagamento.getValor());

            if (pagamento.getDataVencimento() != null) {
                stmt.setDate(2, Date.valueOf(pagamento.getDataVencimento()));
            } else {
                stmt.setNull(2, java.sql.Types.DATE);
            }

            if (pagamento.getDataPagamento() != null) {
                stmt.setDate(3, Date.valueOf(pagamento.getDataPagamento()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            if (pagamento.getStatus() != null) {
                stmt.setString(4, pagamento.getStatus().name());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }

            stmt.setInt(5, pagamento.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao editar pagamento: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int idPagamentoNoBanco) {
        String sql = "DELETE FROM pagamentos WHERE id = ?";

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPagamentoNoBanco);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir pagamento: " + e.getMessage());
            return false;
        }
    }

    public List<Pagamento> listarAtrasados() {
        String sql = "SELECT * FROM pagamentos WHERE status = 'ATRASADO'";
        List<Pagamento> lista = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pagamento pagamento = new Pagamento();
                pagamento.setId(rs.getInt("id"));
                pagamento.setValor(rs.getDouble("valor"));

                if (rs.getDate("data_vencimento") != null) {
                    pagamento.setDataVencimento(rs.getDate("data_vencimento").toLocalDate());
                }
                if (rs.getDate("data_pagamento") != null) {
                    pagamento.setDataPagamento(rs.getDate("data_pagamento").toLocalDate());
                }

                pagamento.setStatus(StatusPagamento.valueOf(rs.getString("status")));
                lista.add(pagamento);
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao listar pagamentos atrasados: " + e.getMessage());
        }
        return lista;
    }

    public void verificaPagamentosAtrasados() {
        String sql = "UPDATE pagamentos SET status = 'ATRASADO' WHERE status = 'PENDENTE' AND data_vencimento < CURRENT_DATE";
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println(" Aviso do Sistema: " + linhasAfetadas + " mensalidades foram marcadas como ATRASADAS hoje.");
            } else {
                System.out.println(" Nenhuma mensalidade nova entrou em atraso hoje.");
            }
            
        } catch (SQLException e) {
            System.err.println(" Erro ao verificar pagamentos atrasados: " + e.getMessage());
        }
    }
}
