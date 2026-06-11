package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

import core.Agenda;
import core.Historico;
import core.Relatorio;
import core.StatusTarefa;
import core.Tarefa;

public class GerenciadorBanco {
  
  private Connection conn;

  public GerenciadorBanco() {
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:db/database.db");
      System.out.println("Conexão efetuada com sucesso");
      criarTabelas();
    } catch (SQLException e) {
      System.out.println("Conexão falha");
    }
  }

  private void criarTabelas() {
    String agendaSql = "CREATE TABLE IF NOT EXISTS agenda(" +
    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
    "data TEXT UNIQUE NOT NULL" +
    ");";

    String tarefaSql = "CREATE TABLE IF NOT EXISTS tarefa(" +
    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
    "nome TEXT NOT NULL," +
    "descricao TEXT," + 
    "horario TEXT NOT NULL," +
    "status TEXT NOT NULL CHECK (status IN(\"Pendente\", \"Concluido\", \"Falhado\", \"Pulado\"))," +
    "ciclico INTEGER DEFAULT 0," +
    "id_agenda INTEGER NOT NULL," +
    "FOREIGN KEY (id_agenda) " +
    " REFERENCES agenda (id)" +
    ");";

    try (Statement stmt = conn.createStatement()){
      stmt.execute(agendaSql);
      stmt.execute(tarefaSql);
    } catch (SQLException e) {
      System.err.println("Erro ao tentar criar tabelas: " + e.getMessage());
    }
  }

  public Historico montarHistorico() {
    Historico historico = new Historico();

    String sql = "SELECT id, data " +
      "FROM agenda " +
      "ORDER BY data;";

    try (Statement stmt = conn.createStatement()) {
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        int id = rs.getInt("id");
        LocalDate data = LocalDate.parse(rs.getString("data"));
        
        if (data.equals(LocalDate.now())) continue;
        
        Agenda agenda = new Agenda(id, data);
        findTarefas(agenda);

        Relatorio relatorio = agenda.gerarRelatorio();
        historico.adicionarRelatorio(relatorio);
      }

    } catch (SQLException e) {
      System.out.println("Erro ao buscar agenda para historico: " + e.getMessage());
    }

    return historico;
  }

  private void findTarefas(Agenda agenda) {
    String sql = "SELECT id, nome, descricao, horario, status, ciclico " +
    "FROM tarefa " +
    "WHERE id_agenda = (?)";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, agenda.getId());
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String descricao = rs.getString("descricao");
        LocalTime horario = LocalTime.parse(rs.getString("horario"));
        StatusTarefa status = StatusTarefa.deString(rs.getString("status"));
        boolean ciclico = rs.getInt("ciclico") == 1;

        Tarefa tarefa = new Tarefa(id, nome, descricao, horario, status, ciclico);

        agenda.adicionarTarefa(tarefa);
      }

    } catch(SQLException e) {
      System.err.println("Erro ao tentar buscar tarefas de uma agenda: " + e.getMessage());
    }
  }

  public void adicionarHistoricoTeste(Agenda agenda) {
    String agendaSql = "INSERT INTO agenda (data) " +
      "VALUES (?);";
    String tarefaSql = "INSERT INTO tarefa (nome, descricao, horario, status, ciclico, id_agenda) " +
      "VALUES (?, ?, ?, ?, ?, ?);" ;

    try (PreparedStatement pstmt = conn.prepareStatement(agendaSql, Statement.RETURN_GENERATED_KEYS)) {
      pstmt.setString(1, agenda.getData().toString());

      pstmt.executeUpdate();

      try (ResultSet rsKey = pstmt.getGeneratedKeys()) {
        if (rsKey.next()) {
          agenda.setId(rsKey.getInt(1));;
        }
      }
    } catch (SQLException e) {
      System.err.println("Erro ao registrar agenda de teste: " + e.getMessage());
    }

    try (PreparedStatement pstmt = conn.prepareStatement(tarefaSql, Statement.RETURN_GENERATED_KEYS)) {
    for (Tarefa tarefa : agenda.getTarefas()) {
      pstmt.setString(1, tarefa.getNome());
      pstmt.setString(2, tarefa.getDescricao());
      pstmt.setString(3, tarefa.getHorario().toString());
      pstmt.setString(4, tarefa.getStatus().toString());
      pstmt.setInt(5, tarefa.isCiclico()? 1: 0);
      pstmt.setInt(6, agenda.getId());

      pstmt.executeUpdate();
      try (ResultSet rsKey = pstmt.getGeneratedKeys()) {
        if (rsKey.next()) {
        int idTarefa = rsKey.getInt(1);
        tarefa.setId(idTarefa);
        }
      } catch (SQLException e) {
        System.err.println("Erro ao tentar buscar id da tarefa registrada: " + e.getMessage());
      }
    }

    } catch (SQLException e) {
      System.err.println("Erro ao inserir tarefas para teste: " + e.getMessage());
    }
  }
}
