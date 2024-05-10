package main.java;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class Operaio {

    String cf;
    Statement statement;

    public Operaio(String cf, Statement statement){
        this.cf = cf;
        this.statement = statement;
        System.out.println("Accesso come operaio eseguito");
        menu();
    }

    void menu(){
        while(true){
            System.out.println("Cosa si desidera fare?");
            System.out.println("1) Visualizza produzioni da avviare");
            System.out.println("2) Avvia una produzione");
            System.out.println("3) Visualizza produzioni in corso");
            System.out.println("4) Prendi parte a una produzione");
            System.out.println("5) Visualizza produzioni in cui si è preso parte");
            System.out.println("6) Visualizza dati per un progetto da produrre");
            System.out.println("7) Dichiara completata una produzione");
            System.out.println("8) Visualizza ordini da spedire");
            System.out.println("9) prepara una spedizione");
            System.out.println("10) Esci");
            Scanner s = new Scanner(System.in);
            int scelta = Integer.parseInt(s.nextLine());
            switch(scelta){
                case 1:
                    visualizzaProduzioniDaAvviare();
                    break;
                case 2:
                    avviaProduzione();
                    break;
                case 3:
                    visualizzaProduzioniInCorso();
                    break;
                case 4:
                    prendiParte();
                    break;
                case 5:
                    visualizzaPreseParte();
                    break;
                case 6:
                    visualizzaDati();
                    break;
                case 7:
                    dichiaraCompleta();
                    break;
                case 8:
                    visualizzaDettaglio();
                    break;
                case 9:
                    prepara();
                    break;
                case 10:
                    return;
            }
        }
    }

    void visualizzaProduzioniDaAvviare(){

        String sql = "SELECT ordine_artista.id, data, tipo_ordine, priorità, titolo FROM ordine_artista JOIN progetto ON ordine_artista.progetto=progetto.id WHERE stato = 'pagato'";
        try{
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println("ID: "+rs.getInt(1));
                System.out.println("Titolo Progetto: " + rs.getString(5));
                System.out.println("Data ordine: " + rs.getDate(2));
                System.out.println("Tipo ordine: " + rs.getString(3));
                System.out.println("Priorità ordine: " + rs.getString(4));
                System.out.println("");
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    void avviaProduzione(){

        System.out.println("Di quale progetto si vuole avviare la produzione?\nInserire l'id:");
        Scanner s = new Scanner(System.in);
        int id = Integer.parseInt(s.nextLine());
        String sql = "INSERT INTO produzione(id) VALUES ("+id+")";
        try{
            statement.executeUpdate(sql);
            sql = "INSERT INTO affidamento_produzione(id, operaio) VALUES ("+id+", '"+cf+"')";
            statement.executeUpdate(sql);
            System.out.println("Produzione avviata con successo!");
        }catch(SQLException e){
            System.out.println("Qualcosa è andato storto nell'avviare la produzione");
            System.out.println(e.getMessage());
        }
    }

    void visualizzaProduzioniInCorso(){

        String sql = "SELECT produzione.id, data_inizio, priorità," +
                "(SELECT COUNT(*) AS n_operai FROM affidamento_produzione WHERE affidamento_produzione.id = produzione.id)" +
                "FROM produzione JOIN ordine_artista ON produzione.id = ordine_artista.id WHERE data_fine IS NULL";

        try{
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println("ID Produzione: " + rs.getInt(1));
                System.out.println("Data Inizio: " + rs.getDate(2));
                System.out.println("Priorità: " + rs.getString(3));
                System.out.println("");
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    void prendiParte(){

        System.out.println("A quale produzione si vuole prendere parte?\nDigitarne l'id");
        Scanner s = new Scanner(System.in);
        int id = Integer.parseInt(s.nextLine());
        String sql = "INSERT INTO affidamento_produzione (id, operaio) VALUES ("+id+", '"+cf+"')";
        try{
            statement.executeUpdate(sql);
            System.out.println("Operazione effettuata con successo");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void visualizzaPreseParte(){

        String sql = "SELECT p.id, progetto, data_inizio, priorità FROM affidamento_produzione AS a JOIN produzione AS p ON a.id = p.id JOIN ordine_artista AS oa ON p.id = oa.id WHERE a.operaio = '"+cf+"' AND data_fine IS NULL";

        try{
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println("ID Produzione: " + rs.getInt(1));
                System.out.println("ID Progetto: " + rs.getInt(2));
                System.out.println("Data inizio: " + rs.getDate(3));
                System.out.println("");
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    void visualizzaDati(){

        System.out.println("Di quale progetto si desiderano i dati?\nInserire l'id");
        Scanner s = new Scanner(System.in);
        int id = Integer.parseInt(s.nextLine());
        String sql;
        try{

            System.out.println("Che cosa si vuole sapere?\n1) Dettagli del progetto\n2) Dettagli masterizzazione\n3) Dettagli stampa disco\n4) Dettagli inserto");
            int scelta = Integer.parseInt(s.nextLine());
            switch(scelta){
                case 1:
                    sql = "SELECT * FROM dettagli_progetto WHERE progetto = "+id;
                    ResultSet info = statement.executeQuery(sql);
                    while(info.next()){
                        System.out.println("Descrizione: " + info.getString(2));
                        System.out.println("Numero Dischi: " + info.getInt(3));
                        System.out.println("info extra: " + info.getString(4));
                        System.out.println("Packaging: " + info.getString(5));
                        System.out.println("Tipologia: " + info.getString(6));
                    }
                    break;
                case 2:
                    sql = "SELECT * FROM masterizzazione WHERE progetto = "+id;
                    ResultSet master = statement.executeQuery(sql);
                    while(master.next()){
                        System.out.println("Tipo: " + master.getString(2));
                        System.out.println("File Masterizzazione: " + master.getString(3));
                    }
                    break;
                case 3:
                    sql = "SELECT * FROM stampa_disco WHERE progetto = "+id;
                    ResultSet stampa = statement.executeQuery(sql);
                    while(stampa.next()){
                        System.out.println("Tipo di stampa: " + stampa.getString(2));
                        System.out.println("File Stampa: "+ stampa.getString(3));
                    }
                    break;
                case 4:
                    sql = "SELECT * FROM inserto WHERE progetto = "+id;
                    ResultSet inserto = statement.executeQuery(sql);
                    while(inserto.next()){
                        System.out.println("Stampa inserto: " + inserto.getString(2));
                        System.out.println("File stampa: " + inserto.getString(3));
                        System.out.println("Posizione inserto: " + inserto.getString(4));
                    }
                    break;

            }


        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void dichiaraCompleta(){

        System.out.println("Digitare l'id dell'ordine la cui produzione è completata");
        Scanner s = new Scanner(System.in);
        int id = Integer.parseInt(s.nextLine());
        String sql = "UPDATE produzione SET data_fine = CURRENT_TIMESTAMP WHERE id = "+id;
        try{

            statement.executeUpdate(sql);
            System.out.println("Operazione effettuata con successo");

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void visualizzaDettaglio(){

        String sql = "SELECT id, data, merce, indirizzo_spedizione FROM ordine_dettaglio WHERE id_spedizione IS NULL";

        try{

            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println("ID Ordine: " + rs.getInt(1));
                System.out.println("Data dell'ordine: " + rs.getDate(2));
                System.out.println("ID Merce Ordinata: " + rs.getInt(3));
                System.out.println("Indirizzo di spedizione: " + rs.getString(4));
                System.out.println("");
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }


    }

    void prepara(){

        Scanner s = new Scanner(System.in);
        System.out.println("Inserire Lettera Di Vettura");
        String ldv = s.nextLine();
        System.out.println("Inserire Altezza");
        int altezza = Integer.parseInt(s.nextLine());
        System.out.println("Inserire lunghezza");
        int lunghezza = Integer.parseInt(s.nextLine());
        System.out.println("Inserire larghezza");
        int larghezza = Integer.parseInt(s.nextLine());
        System.out.println("Inserire il peso della merce in chili");
        int peso = Integer.parseInt(s.nextLine());
        System.out.println("Inserire il numero di colli");
        int colli = Integer.parseInt(s.nextLine());
        System.out.println("Inserire la data del ritiro nel formato AAAA-MM-GG");
        String dataRitiro = s.nextLine();
        System.out.println("Inserire la data di consegna prevista nel formato AAAA-MM-GG");
        String dataConsegna = s.nextLine();
        String corriere = "";
        String tipologia = "";
        try{
            String query = "SELECT DISTINCT corriere FROM tipologia_spedizione";
            ResultSet sped = statement.executeQuery(query);
            System.out.println("Selezionare il corriere");
            while(sped.next()){
                System.out.println(sped.getString(1));
            }
            corriere = s.nextLine();

            query = "SELECT tipo, costo FROM tipologia_spedizione WHERE corriere = '"+corriere+"' AND peso_max >= "+peso;
            System.out.println("Seleziona il tipo di spedizione tra le seguenti disponibili:");
            ResultSet tipologiaa = statement.executeQuery(query);
            while(tipologiaa.next()){
                System.out.println(tipologiaa.getString(1) + " Costo: " + tipologiaa.getInt(2));
            }
            tipologia = s.nextLine();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        System.out.println("La spedizione è al dettaglio o all'ingrosso?");
        String di = s.nextLine();
        String sql = "INSERT INTO spedizione (ldv, dimensioni, peso, colli, data_ritiro, consegna_prevista, tipologia, corriere)" +
            "VALUES ('"+ldv+"', ("+altezza + ","+lunghezza+","+larghezza+")"+", "+peso+", "+colli+", '"+dataRitiro+"', '"+dataConsegna+"', '"+tipologia+"', '"+corriere+"') RETURNING ID";
        try{

            ResultSet rs = statement.executeQuery(sql);
            rs.next();
            int id = rs.getInt(1);
            if(di.equals("dettaglio")){
                System.out.println("A quale ordine desideri associare la spedizione?");
                int idAss = Integer.parseInt(s.nextLine());
                sql="UPDATE ordine_dettaglio SET id_spedizione = "+id+" WHERE id = "+idAss+""; //che minchia è id_ordine mo
                statement.executeUpdate(sql);
                System.out.println("Spedizione registrata con successo");
            }else{
                System.out.println("A quale ordine desideri associare la spedizione?");
                int idAss = Integer.parseInt(s.nextLine());
                sql = "UPDATE ordine_ingrosso SET id_spedizione = "+id+" WHERE id = "+idAss+"";
                statement.executeUpdate(sql);
                System.out.println("Spedizione registrata con successo");
            }


        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

}
