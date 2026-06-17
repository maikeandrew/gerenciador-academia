    package br.ufersa.academia.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    
    private static final String URL = "jdbc:postgresql://aws-1-sa-east-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String USUARIO = "postgres.qipcyxlzvguwlmcwuxnt";
    private static final String SENHA = "#Mlps202020060428";

    public static Connection getConnection() {
        try {
            Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            return conexao;
        } catch (SQLException e) {
            System.err.println("🔴 Erro crítico: Não foi possível conectar ao Supabase!");
            e.printStackTrace();
            return null;
        }
    }
}