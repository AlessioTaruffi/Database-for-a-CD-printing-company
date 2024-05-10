package main.java;
import java.sql.*;
import java.util.*;

public class Privato {

    String cf;
    Statement statement;

    public Privato(String cf, Statement statement) {
        this.cf = cf;
        this.statement = statement;
        System.out.println("Accesso come privato eseguito");
        menu();
    }

    void menu() {

        while(true){
            Scanner s = new Scanner(System.in);
            System.out.println("Cosa si desidera fare?");
            System.out.println("1) Visualizzare la vetrina");
            System.out.println("2) Ordinare un progetto");
            System.out.println("3) Visualizza stato degli ordini");
            System.out.println("4) Invia una richiesta di supporto");
            System.out.println("5) Associa un nuovo telefono");
            System.out.println("6) Associa email");
            System.out.println("7) Esci");
            int scelta = Integer.parseInt(s.nextLine());
            switch (scelta) {
                case 1:
                    vetrina();
                    break;
                case 2:
                    ordina();
                    break;
                case 3:
                    traccia();
                    break;
                case 4:
                    supporto();
                    break;
                case 5:
                    associaTelefono();
                    break;
                case 6:
                    associaEmail();
                    break;
                case 7:
                    return;
            }
        }
    }

    void vetrina() {

        String sql = "SELECT p.titolo, v.prezzo_unit, p.id FROM vetrina_online as v JOIN progetto as p on v.id = p.id ";
        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                System.out.println("ID Progetto: " + rs.getInt(3));
                System.out.println("Titolo: " + rs.getString(1));
                System.out.println("Costo: " + rs.getInt(2));
                System.out.println("");
            }

            sql = "UPDATE vetrina_online SET visualizzazioni = visualizzazioni + 1";
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void ordina() {

        Scanner s = new Scanner(System.in);
        System.out.println("Digitare l'id del progetto che si desidera ordinare");
        int id = Integer.parseInt(s.nextLine());
        System.out.println("Digitare la quantità da ordinare");
        int quantita = Integer.parseInt(s.nextLine());
        String sql = "SELECT id  FROM stock WHERE progetto = " + id + " AND quantità_rimanente >= " + quantita;
        try {
            ResultSet rs = statement.executeQuery(sql);

            if (!rs.next()) {
                System.out.println("Non vi è stock a sufficienza per ordinare il prodotto selezionato");
                return;
            }
            System.out.println("Digitare l'indirizzo di spedizione");
            String indirizzo = s.nextLine();
            System.out.println("Digitare la città");
            String citta = s.nextLine();
            System.out.println("Digitare la provincia");
            String provincia = s.nextLine();
            System.out.println("Digitare la Regione");
            String regione = s.nextLine();
            System.out.println("Digirate la nazione");
            String stato = s.nextLine();
            int idStockOrdinato = rs.getInt(1);

            System.out.println("Digitare l'id del metodo di pagamento");
            sql = "SELECT id, metodo, commissione FROM metodo_pagamento";
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getInt(1) + ") " + rs.getString(2) + " commissione: " + rs.getInt(3));
            }
            int metodoPagamento = Integer.parseInt(s.nextLine());
            System.out.println("Procedere all'ordine? SI/NO");
            String procedere = s.nextLine();
            if (procedere.equals("NO")) {
                return;
            }


            sql = "INSERT INTO ordine_dettaglio (privato, indirizzo_spedizione, fatturazione, merce, quantità)" +
                    "VALUES ('" + cf + "', ('" + indirizzo + "', '" + citta + "', '" + provincia + "', '" + regione + "', '" + stato + "'), " + metodoPagamento + ", " + idStockOrdinato + ", " + quantita + ")";
            statement.executeUpdate(sql);
            System.out.println("Ordine effettuato con successo");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void traccia() {

        String sql = "SELECT * FROM ordine_dettaglio WHERE privato = '" + cf + "'";
        try {
            ResultSet ordini = statement.executeQuery(sql);
            while (ordini.next()) {

                System.out.println(ordini.getString(3) + " " + ordini.getDate(4));
                System.out.print("Stato: ");
                int stato = ordini.getInt(9);
                if (stato > 0) {
                    sql = "SELECT consegna_prevista FROM spedizione WHERE id = " + stato;
                    Connection connection2 = DriverManager.getConnection("jdbc:postgresql://db.marcorealacci.me:5556/CDClickBD2", "marco", "serafina");
                    Statement statement2 = connection2.createStatement();
                    ResultSet stators = statement2.executeQuery(sql);
                    stators.next();
                    System.out.print("Consegna prevista il ");
                    System.out.println(stators.getDate(1));

                } else {
                    System.out.println("Non Spedito");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    void supporto() {

        Scanner s = new Scanner(System.in);
        System.out.println("Descrivere la richiesta da effettuare");
        String richiesta = s.nextLine();
        String sql = "INSERT INTO richiesta_supporto(mittente, messaggio, stato) VALUES('" + cf + "','" + richiesta + "','ricevuta')";
        try {
            statement.executeUpdate(sql);
            System.out.println("Richiesta effettuata con successo");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    void associaTelefono() {
        System.out.println("Digitare il numero di telefono");
        Scanner s = new Scanner(System.in);
        String tel = s.nextLine();
        String sql = "INSERT INTO telefoni_utenti(cf,telefono) VALUES('" + cf + "','" + tel + "')";
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void associaEmail() {
        System.out.println("Digitare l'email");
        Scanner s = new Scanner(System.in);
        String tel = s.nextLine();
        String sql = "INSERT INTO email_utenti(cf,email) VALUES('" + cf + "','" + tel + "')";
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
}
