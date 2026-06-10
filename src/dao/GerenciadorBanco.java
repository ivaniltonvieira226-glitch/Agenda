package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
    "data TEXT NOT NULL" +
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
}
