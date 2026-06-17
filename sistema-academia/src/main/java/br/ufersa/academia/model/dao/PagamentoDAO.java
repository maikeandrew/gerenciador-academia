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

    // --- MÉTODO CADASTRAR ---
    public boolean cadastrar(Pagamento pagamento, int idAlunoNoBanco) {
        String sql = "INSERT INTO pagamentos (aluno_id, valor, data_vencimento, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAlunoNoBanco);
            stmt.setDouble(2, pagamento.getValor());
            stmt.setDate(3, Date.valueOf(pagamento.getDataVencimento()));
            stmt.setString(4, pagamento.getStatus().name()); 
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao registrar pagamento no Supabase: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- MÉTODO LISTAR TODOS ---
    public List<Pagamento> listar() {
        String sql = "SELECT * FROM pagamentos";
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
            System.err.println("🔴 Erro ao listar pagamentos: " + e.getMessage());
        }
        return lista;
    }

    // --- MÉTODO LISTAR POR ALUNO (Para o Portal do Aluno) ---
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
            System.err.println("🔴 Erro ao listar histórico do aluno: " + e.getMessage());
        }
        return lista;
    }

    // --- MÉTODO CONFIRMAR PAGAMENTO ---
    public boolean confirmarPagamento(int idPagamentoNoBanco) {
        String sql = "UPDATE pagamentos SET status = 'PAGO', data_pagamento = CURRENT_DATE WHERE id = ?";
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPagamentoNoBanco);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao confirmar pagamento: " + e.getMessage());
            return false;
        }
    }

    // --- MÉTODO VERIFICAR ATRASADOS ---
    public void verificaPagamentosAtrasados() {
        String sql = "UPDATE pagamentos SET status = 'ATRASADO' WHERE status = 'PENDENTE' AND data_vencimento < CURRENT_DATE";
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("⚠️ Aviso do Sistema: " + linhasAfetadas + " mensalidades foram marcadas como ATRASADAS hoje.");
            } else {
                System.out.println("✅ Nenhuma mensalidade nova entrou em atraso hoje.");
            }
            
        } catch (SQLException e) {
            System.err.println("🔴 Erro ao verificar pagamentos atrasados: " + e.getMessage());
        }
    }
}