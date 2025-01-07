import java.sql.*;
import java.util.Scanner;

public class BibliotecaCLI {

    // Conexão com o banco de dados
    private static Connection connect() {
        String url = "jdbc:sqlite:biblioteca.db"; // URL do banco de dados SQLite
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Criar tabelas
    private static void createTables() {
        String sqlCreateLivros = "CREATE TABLE IF NOT EXISTS livros ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "titulo TEXT NOT NULL,"
                + "autor TEXT NOT NULL,"
                + "ano INTEGER NOT NULL"
                + ");";

        String sqlCreateMembros = "CREATE TABLE IF NOT EXISTS membros ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome TEXT NOT NULL,"
                + "email TEXT NOT NULL"
                + ");";

        String sqlCreateEmprestimos = "CREATE TABLE IF NOT EXISTS emprestimos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_livro INTEGER,"
                + "id_membro INTEGER,"
                + "data_emprestimo TEXT,"
                + "data_devolucao TEXT,"
                + "FOREIGN KEY (id_livro) REFERENCES livros (id),"
                + "FOREIGN KEY (id_membro) REFERENCES membros (id)"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCreateLivros);
            stmt.execute(sqlCreateMembros);
            stmt.execute(sqlCreateEmprestimos);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para adicionar um livro
    private static void adicionarLivro() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o título do livro:");
        String titulo = scanner.nextLine();
        System.out.println("Digite o autor do livro:");
        String autor = scanner.nextLine();
        System.out.println("Digite o ano de publicação:");
        int ano = scanner.nextInt();

        String sql = "INSERT INTO livros(titulo, autor, ano) VALUES(?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titulo);
            pstmt.setString(2, autor);
            pstmt.setInt(3, ano);
            pstmt.executeUpdate();
            System.out.println("Livro adicionado com sucesso!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para listar todos os livros
    private static void listarLivros() {
        String sql = "SELECT * FROM livros";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Lista de Livros:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("titulo") + "\t" +
                        rs.getString("autor") + "\t" +
                        rs.getInt("ano"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método principal para interagir com o usuário
    public static void main(String[] args) {
        createTables(); // Cria as tabelas no banco de dados se não existirem
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\nSistema de Gerenciamento de Biblioteca");
            System.out.println("1. Adicionar Livro");
            System.out.println("2. Listar Livros");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    adicionarLivro();
                    break;
                case 2:
                    listarLivros();
                    break;
                case 3:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 3);
    }
}
