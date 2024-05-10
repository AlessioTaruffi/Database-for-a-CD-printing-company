import main.java.*;

import java.sql.*;
import java.util.*;

public class Main {

    public static final String driver = "org.postgresql.Driver";
    public static final String url = "jdbc:postgresql://db.marcorealacci.me:5556/CDClickBD2";

    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        System.out.println("Benvenuti in CDClick!");
        int scelta = 0;
        while (scelta != 3) {

            System.out.println("Si desidera registrarsi o fare il login?");
            System.out.println("1 = Registra utente\n2 = Associa un account utente a un account artista\n3 = login");
            scelta = Integer.parseInt(sc.nextLine());
            if(scelta == 1){
                registrazione(sc);
            }
            if(scelta == 2){
                associa(sc);
            }
        }

        try{

            System.out.println("Inserire username");
            String user = sc.nextLine();
            System.out.println("Inserire password");
            String password = sc.nextLine();


            Class.forName(driver);

            Connection connection = DriverManager.getConnection(url, "xxxxx", "xxxxx");
            Statement statement = connection.createStatement();
            String sql;
            sql = "SELECT cf, nome FROM utente WHERE username = '" + user + "' AND password = '" + password + "'";
            ResultSet rs =  statement.executeQuery(sql);

            if (!rs.next()) {
                System.out.println("username o password non esistente");
            }

            String cf = rs.getString("cf");
            String nome = rs.getString("nome");


            List possibili = new ArrayList();

            sql = "SELECT COUNT(*) AS operai FROM \"operaio\" WHERE \"cf\" = '" + cf +  "'";
            rs =  statement.executeQuery(sql);
            if (!rs.next()) erroreGenerico();
            if (rs.getInt("operai") > 0) possibili.add("operaio");

            sql = "SELECT COUNT(*) AS operai FROM \"grafico\" WHERE \"cf\" = '" + cf +  "'";
            rs =  statement.executeQuery(sql);
            if (!rs.next()) erroreGenerico();
            if (rs.getInt("operai") > 0) possibili.add("grafico");

            sql = "SELECT COUNT(*) AS operai FROM \"artista\" WHERE \"cf\" = '" + cf +  "'";
            rs =  statement.executeQuery(sql);
            if (!rs.next()) erroreGenerico();
            if (rs.getInt("operai") > 0) possibili.add("artista");

            sql = "SELECT COUNT(*) AS operai FROM \"addetto_assistenza\" WHERE \"cf\" = '" + cf +  "'";
            rs =  statement.executeQuery(sql);
            if (!rs.next()) erroreGenerico();
            if (rs.getInt("operai") > 0) possibili.add("assistente");

            sql = "SELECT COUNT(*) AS operai FROM \"privato\" WHERE \"cf\" = '" + cf +  "'";
            rs =  statement.executeQuery(sql);
            if (!rs.next()) erroreGenerico();
            if (rs.getInt("operai") > 0) possibili.add("privato");

            System.out.println("Benvenuto " + nome + "!\nPuoi accedere come:");


            for (int counter = 0; counter < possibili.size(); counter++) {
                System.out.println(possibili.get(counter));
            }

            System.out.println("Come vuoi accedere?");
            String ruolo = accesso(possibili);

            switch(ruolo){
                case "grafico":
                    Grafico newGrafico = new Grafico(cf, statement);
                    break;
                case "operaio":
                    Operaio newOperaio = new Operaio(cf, statement);
                    break;
                case "artista":
                    Artista newArtista = new Artista(cf, statement);
                    break;
                case "privato":
                    Privato newPrivato = new Privato(cf, statement);
                    break;
                case "assistente":
                    Assistente newAssistente = new Assistente(cf, statement);
                    break;
                default:
                    System.out.println("errore nell scelta del login");
                    return;
            }

        } catch (ClassNotFoundException e){
            System.out.println("errore, non chiedermi quale");
            e.printStackTrace();
        } catch(SQLException f){
            System.out.println("altro errore" + f.getMessage());
            f.printStackTrace();
        }

    }

    static void erroreGenerico() {
        System.out.println("errore imprevisto! Contatta l'amministratore");
        return;
    }

    static void associa(Scanner sc){
        System.out.println("Inserire Codice Fiscale");
        String cf = sc.nextLine();
        System.out.println("Inserire il nome d'arte");
        String na = sc.nextLine();
        System.out.println("Si desidera inserire una Partita IVA?\n1=s1 2=no");
        int iva = Integer.parseInt(sc.nextLine());
        String piva = "";
        boolean p = false;
        String sql = "";
        if(iva == 1){
            System.out.println("Inserire Partita IVA");
            piva = sc.nextLine();
            p = true;
        }
        try{
            Connection connection = DriverManager.getConnection(url, "xxxx", "xxxx");
            Statement statement = connection.createStatement();
            if(!p){
                sql = "INSERT INTO artista(cf, nome_arte) VALUES('"+cf+"','"+na+"')";
            }
            else{
                sql = "INSERT INTO artista(cf, nome_arte, p_iva) VALUES('"+cf+"','"+na+"','"+piva+"')";
            }
            statement.executeUpdate(sql);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    static void registrazione(Scanner sc){
        System.out.println("Inserire il proprio Codice Fiscale");
        String cf = sc.nextLine();
        System.out.println("Inserire il proprio nome utente");
        String utente = sc.nextLine();
        System.out.println("Inserire la propria password");
        String password = sc.nextLine();
        System.out.println("Inserire il proprio nome");
        String nome = sc.nextLine();
        System.out.println("Inserire il proprio cognome");
        String cognome = sc.nextLine();
        System.out.println("Inserire numero di telefono");
        String telefono = sc.nextLine();
        System.out.println("Inserire e-mail");
        String email = sc.nextLine();
        try{
            Connection connection = DriverManager.getConnection(url, "xxxx", "xxxx");
            Statement statement = connection.createStatement();
            String sql = "INSERT INTO utente(cf, username, password, nome, cognome) VALUES('"+cf+"','"+
                    utente + "','"+password+"','"+nome+"','"+cognome+"')";
            statement.executeUpdate(sql);
            sql = "INSERT INTO privato(cf) VALUES('"+cf+"')";
            statement.executeUpdate(sql);
            sql = "INSERT INTO email_utenti(cf, email) VALUES('"+cf+"','"+email+"')";
            statement.executeUpdate(sql);
            sql = "INSERT INTO telefoni_utenti(cf, telefono) VALUES('"+cf+"','"+telefono+"')";
            statement.executeUpdate(sql);
            System.out.println("Registrazione avvenuta con successo!");

        }catch(SQLException e){
            System.out.println("Errore nella registrazione, controllare i dati e riprovare");
        }
    }

    static String accesso(List possibili){

        Scanner s = new Scanner(System.in);
        String accesso;

        do {
            accesso = s.nextLine();
            if(possibili.indexOf(accesso) == -1) System.out.println("inserire un accesso valido");
        } while(possibili.indexOf(accesso) == -1);

        return accesso;
    }
}
