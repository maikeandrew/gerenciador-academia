package br.ufersa.academia.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.ufersa.academia.model.dao.PagamentoDAO;

public class Pagamento {

    private int id;
    private Aluno aluno;
    private double valor;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private StatusPagamento status;

    public Pagamento() {
    }

    public Pagamento(Aluno aluno, double valor, LocalDate dataVencimento) {
        this.aluno = aluno;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = StatusPagamento.PENDENTE;
    }

    public void verPagamento() {
        System.out.println("--- Detalhes do Pagamento ---");
        System.out.println("ID Fatura: " + this.id);
        System.out.println("Aluno: " + (this.aluno != null ? this.aluno.getNome() : "Nao informado"));
        System.out.println("Valor: R$ " + this.valor);
        System.out.println("Data de Vencimento: " + this.dataVencimento);
        System.out.println("Status: " + this.status);
        if (this.dataPagamento != null) {
            System.out.println("Pago em: " + this.dataPagamento);
        }
    }

    public void registrarPagamento() {
        if (this.status != StatusPagamento.PAGO) {
            this.dataPagamento = LocalDate.now();
            this.status = StatusPagamento.PAGO;

            if (this.id > 0) {
                new PagamentoDAO().confirmarPagamento(this.id);
            }

            System.out.println("Pagamento confirmado para o aluno: " + (this.aluno != null ? this.aluno.getNome() : "Nao informado"));

            if (this.aluno != null) {
                this.aluno.renovarMatricula();
            }
        } else {
            System.out.println("Este pagamento ja foi realizado anteriormente.");
        }
    }

    public static void verificaPagamentosAtrasados() {
        new PagamentoDAO().verificaPagamentosAtrasados();
    }

    public static Pagamento criarPagamento(Aluno aluno, double valor, LocalDate dataVencimento) {
        Pagamento pagamento = new Pagamento(aluno, valor, dataVencimento);

        if (aluno != null && aluno.getId() > 0) {
            new PagamentoDAO().cadastrar(pagamento, aluno.getId());
        }

        return pagamento;
    }

    public static void editarPagamento(Pagamento pagamento, double valor, LocalDate dataVencimento, LocalDate dataPagamento, StatusPagamento status) {
        if (pagamento != null) {
            pagamento.setValor(valor);
            pagamento.setDataVencimento(dataVencimento);
            pagamento.setDataPagamento(dataPagamento);
            pagamento.setStatus(status);

            if (pagamento.getId() > 0) {
                new PagamentoDAO().editar(pagamento);
            }
        }
    }

    public static void excluirPagamento(Pagamento pagamento) {
        if (pagamento != null && pagamento.getId() > 0) {
            new PagamentoDAO().excluir(pagamento.getId());
        }
    }

    public static List<Pagamento> listarPagamentos() {
        return new PagamentoDAO().listar();
    }

    public static List<Pagamento> listarPagamentosAtrasados() {
        return new PagamentoDAO().listarAtrasados();
    }

    public static List<Pagamento> listarPagamentosPorAluno(Aluno aluno) {
        if (aluno == null || aluno.getCpf() == null || aluno.getCpf().isBlank()) {
            return new ArrayList<>();
        }

        return new PagamentoDAO().listarPorAluno(aluno.getCpf());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
}
