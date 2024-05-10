package main.java;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Grafico {

    String cf;
    Statement statement;

    public Grafico(String cf, Statement statement){
        this.cf = cf;
        this.statement = statement;
        System.out.println("Accesso come grafico eseguito");
        menu();
    }

    void menu(){

        while(true){
            System.out.println("Cosa si vuole fare?");
            System.out.println("1) Visualizza progetti da valutare");
            System.out.println("2) Valuta un progetto");
            System.out.println("3) Esci");
            Scanner s = new Scanner(System.in);
            int scelta = Integer.parseInt(s.nextLine());
            switch(scelta){
                case 1:
                    visualizza();
                    break;
                case 2:
                    approva();
                    break;
                case 3:
                    return;
            }
        }




    }

    void visualizza(){

        String sql = "SELECT id, titolo, data_creazione, artista.nome_arte FROM progetto LEFT JOIN valutazione ON id = progetto JOIN artista ON progetto.artista=artista.cf WHERE grafico IS NULL";
        try{
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + "\ncreato in data " + rs.getDate(3) + "\nda " + rs.getString(4));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    void approva(){

        System.out.println("Digitare l'id del progetto che si desidera valutare");
        Scanner s = new Scanner(System.in);
        int id = Integer.parseInt(s.nextLine());

        try{

            String sql = "SELECT * FROM progetto WHERE id = "+id;
            ResultSet progetto = statement.executeQuery(sql);
            progetto.next();
            String titolo = progetto.getString(2);
            Date dataCreazione = progetto.getDate(3);
            String cfArtista = progetto.getString(4);

            sql = "SELECT nome_arte FROM artista WHERE cf = '"+cfArtista+"'";
            progetto = statement.executeQuery(sql);
            progetto.next();
            String nomeArtista = progetto.getString(1);

            sql = "SELECT * FROM dettagli_progetto WHERE progetto = "+id;
            ResultSet dettagli = statement.executeQuery(sql);
            dettagli.next();
            String descrizione = dettagli.getString(2);
            int nDischi = dettagli.getInt(3);
            String infoExtra = dettagli.getString(4);
            String packaging = dettagli.getString(5);
            String tipologiaCD = dettagli.getString(6);

            sql = "SELECT * FROM masterizzazione WHERE progetto = "+id;
            ResultSet masterizzazione = statement.executeQuery(sql);
            masterizzazione.next();
            String tipoMasterizzazione = masterizzazione.getString(2);
            String percorsoFileCD = masterizzazione.getString(3);

            sql = "SELECT * FROM stampa_disco WHERE progetto = "+id;
            ResultSet stampaDisco = statement.executeQuery(sql);
            stampaDisco.next();
            String tipoStampa = stampaDisco.getString(2);
            String percorsoFileStampa = stampaDisco.getString(3);

            sql = "SELECT * FROM inserto WHERE progetto = "+id;
            ResultSet inserto = statement.executeQuery(sql);
            int c = 0;
            ArrayList<String> inserti = new ArrayList<String>();
            while(inserto.next()){
                c += 1;

                inserti.add(inserto.getString(3)); //percorso file

            }

            System.out.println("Titolo: " + titolo);
            System.out.println("Data Creazione: " + dataCreazione);
            System.out.println("Artista: " + nomeArtista);
            System.out.println("Descrizione del progetto: " + descrizione);
            System.out.println("Numero di dischi: " + nDischi);
            System.out.println("Informazioni Extra: " + infoExtra);
            System.out.println("Packaging: " + packaging);
            System.out.println("Tipo Masterizzazione: "+ tipoMasterizzazione);
            System.out.println("Tipologia CD: " + tipologiaCD);
            System.out.println("Immagine per il CD: " + percorsoFileCD);
            System.out.println("Tipologia di stampa: " + tipoStampa);
            System.out.println("Immagine per la stampa: "+ percorsoFileStampa);
            System.out.println("Inserto frontale: "+ inserti.get(0));
            if(c == 2){
                System.out.println("Inserto posteriore: " + inserti.get(1));
            }
            System.out.println("Si desidera approvare il progetto? SI/NO");
            String scelta = s.nextLine();
            boolean approvazione = false;
            if(scelta.equals("SI")) {
                approvazione = true;
            }
            sql = "INSERT INTO valutazione (progetto, grafico, esito) VALUES ("+id+", '"+cf+"',"+approvazione+")";
            statement.executeUpdate(sql);
            System.out.println("Valutazione eseguita con successo");


        }catch(SQLException e){
            System.out.println("Qualcosa è andato male durante la valutazione");
            System.out.println(e.getMessage());
        }

    }

}
