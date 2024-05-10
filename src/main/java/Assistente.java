package main.java;
import java.sql.*;
import java.util.*;

public class Assistente {

    String cf;
    Statement statement;

    public Assistente(String cf, Statement statement){
        this.cf = cf;
        this.statement = statement;
        System.out.println("Accesso come assistente eseguito");
        menu();
    }

    void menu(){

        while(true){
            Scanner sc = new Scanner(System.in);
            System.out.println("Cosa si desidera fare?");
            System.out.println("1) visualizza richieste in pending");
            System.out.println("2) Prendi in carico una richiesta");
            System.out.println("3) Visualizza richieste prese in carico");
            System.out.println("4) Esci") ;
            int scelta = Integer.parseInt(sc.nextLine());
            switch(scelta){
                case 1:
                    pending();
                    break;
                case 2:
                    prendiInCarico();
                    break;
                case 3:
                    prese();
                    break;
                case 4:
                    return;
            }
        }
    }

    void pending(){
        String sql = "SELECT u.nome, u.cognome, messaggio, richiesta_supporto.id FROM richiesta_supporto JOIN utente AS u ON richiesta_supporto.mittente = u.cf WHERE stato='ricevuta'";
        try{
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println("Richiesta numero " + rs.getInt(4) + " di " + rs.getString(1) +" "+ rs.getString(2) + ": "+ rs.getString(3));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void prendiInCarico(){

        System.out.println("Inserire l'id della richiesta da prendere in carico");
        Scanner s = new Scanner(System.in);
        int id = Integer.parseInt(s.nextLine());
        String sql = "UPDATE richiesta_supporto SET assistente = '"+cf+"', stato='accolta' WHERE richiesta_supporto.id = "+id;
        try{
            statement.executeUpdate(sql);
            System.out.println("Presa in carico effettuata");

            sql = "SELECT email FROM richiesta_supporto, email_utenti AS eu JOIN utente AS u ON u.cf=eu.cf WHERE richiesta_supporto.id = "+id+" AND richiesta_supporto.mittente = u.cf";
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println("Email utente: "+rs.getString(1));
            }
            sql = "SELECT telefono FROM richiesta_supporto, telefoni_utenti AS tu JOIN utente AS u ON u.cf=tu.cf WHERE richiesta_supporto.id = "+id+" AND richiesta_supporto.mittente = u.cf";
            rs = statement.executeQuery(sql);
            while(rs.next()){

                System.out.println("Telefono utente: " + rs.getString(1));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void prese(){

        String slq = "SELECT id, u.nome, u.cognome, messaggio, u.username FROM richiesta_supporto JOIN utente AS u ON u.cf=richiesta_supporto.mittente WHERE assistente='"+cf+"'";
        try{
            ResultSet rs = statement.executeQuery(slq);
            int c = 0;
            Connection connection2 = DriverManager.getConnection("jdbc:postgresql://db.marcorealacci.me:5556/CDClickBD2", "marco", "serafina");
            Statement statement2 = connection2.createStatement();
            while(rs.next()){
                String nome = rs.getString(2);
                String cognome = rs.getString(3);
                String username = rs.getString(5);
                System.out.println("Richiesta numero "+rs.getInt(1) + " inviata da " + nome+" "+cognome+" con messaggio:\n"+rs.getString(4));
                String sql = "SELECT cf FROM utente WHERE nome='"+nome+"' AND cognome='"+cognome+"' AND username = '"+username+"'";
                ResultSet rs2 = statement2.executeQuery(sql);
                rs2.next();
                String cfu = rs2.getString(1);
                sql = "SELECT email FROM email_utenti WHERE cf='"+cfu+"'";
                rs2 = statement2.executeQuery(sql);
                System.out.println("Email registrate dall'utente:");
                while(rs2.next()){
                    System.out.println("\t"+rs2.getString(1));
                    c += 1;
                }
                if(c == 0){
                    System.out.println("\tNessuna email registrata");
                }
                c = 0;
                sql = "SELECT telefono FROM telefoni_utenti WHERE cf='"+cfu+"'";
                rs2 = statement2.executeQuery(sql);
                System.out.println("Numeri di telefono registrati dall'utente:");
                while(rs2.next()){
                    System.out.println("\t"+rs2.getString(1));
                    c += 1;
                }
                if(c == 0){
                    System.out.println("\t Nessun numero di telefono registrato\n");
                }
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
