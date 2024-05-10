package main.java;
import java.sql.*;
import java.util.*;

public class Artista {

    String cf;
    Statement statement;

    public Artista(String cf, Statement statement){
        this.cf = cf;
        this.statement = statement;
        System.out.println("Accesso come artista eseguito");
        menu();
    }

    void menu(){
        while(true){
            System.out.println("Cosa si vuole fare?");
            System.out.println("1) Crea progetto");
            System.out.println("2) Visualizza progetti");
            System.out.println("3) Associa IBAN");
            System.out.println("4) Ordina un progetto");
            System.out.println("5) Visualizza stato degli ordini");
            System.out.println("6) Traccia un ordine all'ingrosso");
            System.out.println("7) Inserisci un progetto in vetrina");
            System.out.println("8) Richiedi supporto");
            System.out.println("9) Associa telefono");
            System.out.println("10) Associa email");
            System.out.println("11) Esci");
            Scanner s = new Scanner(System.in);
            int scelta = Integer.parseInt(s.nextLine());
            switch(scelta){
                case 1:
                    creaProgetto();
                    break;
                case 2:
                    visualizzaProgetti();
                    break;
                case 3:
                    associaIban();
                    break;
                case 4:
                    ordinaProgetto();
                    break;
                case 5:
                    visualizzaStato();
                    break;
                case 6:
                    tracciaIngrosso();
                    break;
                case 7:
                    mettiInVetrina();
                    break;
                case 8:
                    supporto();
                    break;
                case 9:
                    associaTelefono();
                    break;
                case 10:
                    associaEmail();
                    break;
                case 11:
                    return;
            }
        }
    }

    void creaProgetto(){
        Scanner s = new Scanner(System.in);
        System.out.println("Che nome vuoi dargli?");
        String titolo = s.nextLine();
        System.out.println("Descrivi il tuo progetto");
        String descrizione = s.nextLine();
        System.out.println("quanti dischi avrà il tuo progetto?");
        int nDischi = Integer.parseInt(s.nextLine());
        System.out.println("Vuoi aggiungere delle informazioni aggiuntive per la produzione? scrivi NO se non desideri aggiungere altro");
        String infoAggiuntive = s.nextLine();
        String tipoPackaging = "SELECT \"tipo\" FROM \"packaging\" WHERE n_dischi >= " + nDischi;
        System.out.println("Seleziona il tipo di packaging digitandone il nome");
        try{
            int c=1;
            ResultSet rs = statement.executeQuery(tipoPackaging);
            while(rs.next()){
                System.out.print(c + ")");
                c += 1;
                System.out.println(rs.getString("tipo"));
            }
            tipoPackaging = s.nextLine();
        } catch(SQLException e){
            System.out.println("qualcosa è andato storto");
            return;
        }

        System.out.println("Seleziona il tipo di disco digitandone il nome");
        String tipoDisco = "SELECT \"tipo\" FROM \"tipo_dischi\"";
        try{
            int c=1;
            ResultSet rs = statement.executeQuery(tipoDisco);
            while(rs.next()){
                System.out.println(c + ")" + rs.getString("tipo"));
                c += 1;
            }
            tipoDisco = s.nextLine();

        }catch(SQLException e){
            System.out.println("Qualcosa è andato malissimo");
            return;
        }

        System.out.println("Seleziona il tipo di masterizzazione digitandone il nome");
        String tipoMasterizzazione = "SELECT \"tipo\" FROM \"tipo_masterizzazione\" WHERE tipologia_disco = '" + tipoDisco+"'";
        try{
            ResultSet rs = statement.executeQuery(tipoMasterizzazione);
            int c=1;
            while(rs.next()){
                System.out.println(c+") "+rs.getString("tipo"));
            }
            tipoMasterizzazione = s.nextLine();
        }catch(SQLException e){
            System.out.println("Qualcosa è andato male nella selezione della masterizzazione");
            return;
        }


        System.out.println("Linka il file da usare come copertina");
        String copertina = s.nextLine();

        System.out.println("linka il file da usare come inserto frontale");
        String insertoFrontale = s.nextLine();

        System.out.println("Linka il file da usare come inserto posteriore");
        String insertoPosteriore = s.nextLine();

        System.out.println("Scegli il tipo di stampa da usare per la carta");
        String stampa = "SELECT tipo FROM tipo_stampe_dischi";
        try{
            ResultSet rs = statement.executeQuery(stampa);
            int c=1;
            while(rs.next()){
                System.out.println(c+") "+rs.getString("tipo"));
                c += 1;
            }
            stampa = s.nextLine();
        }catch(SQLException e){
            System.out.println("qualcosa è andato male nella selezione della stampa");
        }

        System.out.println("Scegli il tipo di stampa da usare per l'inserto");
        String stampaDisco = "SELECT tipo FROM tipo_stampe_inserti";
        try{
            ResultSet rs = statement.executeQuery(stampaDisco);
            int c=1;
            while(rs.next()){
                System.out.println(c+") "+rs.getString("tipo"));
                c += 1;
            }
            stampaDisco = s.nextLine();

        }catch(SQLException e){
            System.out.println("Qualcosa è andato storto nella scelta della stampa per l'inserto");
        }

        System.out.println("Confermi la creazione? SI/NO");
        String conferma = s.nextLine();
        if(conferma == "NO") return;



        try{
            String sql = "INSERT INTO progetto(titolo, artista) " +
                    "VALUES('" + titolo + "', '" + cf + "') RETURNING ID";
            ResultSet idProgetto = statement.executeQuery(sql);
            idProgetto.next();
            int id = idProgetto.getInt("id");
            sql = "INSERT INTO dettagli_progetto(progetto, descrizione, n_dischi, info_extra, packaging, tipologia) " +
                    "VALUES("+id+",'"+descrizione+"',"+nDischi+",'"+infoAggiuntive+"','"+tipoPackaging+"','"+tipoDisco+"')";
            statement.executeUpdate(sql);
            sql = "INSERT INTO masterizzazione(progetto, tipo, percorso_file) " +
                    "VALUES("+id+",'"+tipoMasterizzazione+"','"+copertina+"')";  //opzionale
            statement.executeUpdate(sql);
            sql = "INSERT INTO stampa_disco(progetto, tipo, percorso_file) " +
                    "VALUES("+id+",'"+stampa+"','"+copertina+"')"; //opzionale
            statement.executeUpdate(sql);
            sql = "INSERT INTO inserto(progetto, stampa, percorso_file, posizione) " +
                    "VALUES("+id+",'"+stampaDisco+ "','"+insertoFrontale+"','frontale')"; //opzionale
            statement.executeUpdate(sql);
            sql = "INSERT INTO inserto(progetto, stampa, percorso_file, posizione) " +
                    "VALUES("+id+",'"+stampaDisco+ "','"+insertoPosteriore+"','posteriore')"; //opzionale
            statement.executeUpdate(sql);
            System.out.println("Progetto creato con successo!");
            System.out.println("");

        }catch(SQLException e){
            System.out.println("qualcosa è andato male nell'aggiunta del progetto al database");
            System.out.println(e.getMessage());
        }

    }

    void visualizzaProgetti(){
        String sql = "SELECT id, titolo, data_creazione, esito FROM progetto LEFT JOIN valutazione ON id = progetto WHERE artista = '" +cf+"'";
        try{
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.print(rs.getInt("id")+") ");
                System.out.print(rs.getString("titolo") + " creato il ");
                System.out.print(rs.getDate("data_creazione")+", ");
                boolean app = rs.getBoolean("esito");
                if(app){
                    System.out.println("Approvato");
                }
                else{
                    if(app == false){
                        System.out.println("Non Approvato");
                    }else{
                        System.out.println("In fase di approvazione");
                    }
                }
                System.out.println("");
            }
        }catch(SQLException e){
            System.out.println("qualcosa è andato storto nella visualizzazione dei progetti");
            return;
        }

    }

    void associaIban(){

        System.out.println("Scrivere l'IBAN da associare");
        Scanner s = new Scanner(System.in);
        String iban = s.nextLine();

        String sql = "INSERT INTO conti_artisti(artista, iban) VALUES ('" + cf +"', '"+iban+"')" +
                "ON CONFLICT (artista)" +
                "DO UPDATE SET iban = '"+iban+"'";

        try{
            statement.executeUpdate(sql);
            System.out.println("IBAN inserito con successo");
        }catch(SQLException e){
            System.out.println("Qualcosa è andato storto nell'aggiungere l'IBAN");
            return;
        }
    }

    void ordinaProgetto(){

        Scanner s = new Scanner(System.in);
        System.out.println("Digitare l'id del progetto che si desidera ordinare");
        int ordina = Integer.parseInt(s.nextLine());
        System.out.println("Digitare la quantità da ordinare");
        int quantità = Integer.parseInt(s.nextLine());
        System.out.println("Si preferisce ordinare all'ingrosso o via dropshipping? Digitare ingrosso/dropshipping");
        String tipoOrdine = s.nextLine();
        String indirizzo = "null";
        if(tipoOrdine.equals("ingrosso")){
            System.out.println("Inserire l'indirizzo di spedizione");
            String ind = s.nextLine();
            System.out.println("inserire città");
            String citta = s.nextLine();
            System.out.println("inserire provincia");
            String provincia = s.nextLine();
            System.out.println("inserire regione");
            String regione = s.nextLine();
            System.out.println("inserire nazione");
            String nazione = s.nextLine();
            indirizzo = "('"+ind+"','"+citta+"','"+provincia+"','"+regione+"','"+nazione+"')";
        }
        System.out.println("Selezionare la priorità per l'ordine tra le seguenti:");
        String sql = "SELECT priorità FROM priorità_ordine";
        String priorità;
        int metodoPagamento;
        try{
            ResultSet rs = statement.executeQuery(sql);
            int c = 1;
            while(rs.next()){
                System.out.println(c + ") "+ rs.getString("priorità"));
                c += 1;
            }
            priorità = s.nextLine();

            System.out.println("Selezionare il metodo di fatturazione tra i seguenti \n Digitare il numero corrispondente al metodo desiderato");
            sql = "SELECT id,metodo FROM metodo_pagamento";
            rs = statement.executeQuery(sql);
            c = 1;
            while(rs.next()){
                System.out.println(rs.getInt("id") + ": " + rs.getString("metodo"));
            }
            metodoPagamento = Integer.parseInt(s.nextLine());
        }catch(SQLException e){
            System.out.println("Qualcosa è andato storto");
            return;
        }
        String preventivo = "SELECT costo_copia * "+quantità+" *" +
                "    (((SELECT maggiorazione_costo_pct FROM \"priorità_ordine\" WHERE priorità = '"+priorità+"') / 100) + 1) *" +
                "    (((SELECT commissione FROM \"metodo_pagamento\" WHERE id = "+metodoPagamento+") / 100) + 1)" +
                " " +
                "        FROM \"costi_produzione_progetti\" WHERE progetto = "+ordina;
        try{
            ResultSet prev = statement.executeQuery(preventivo);
            System.out.print("Il preventivo per il progetto è ");
            prev.next();
            System.out.println(prev.getInt(1) + "€");

        }catch(SQLException e){
            System.out.println("Qualcosa è andato storto nel calcolo del preventivo");
            System.out.println(e.getMessage());
        }

        if(tipoOrdine.equals("ingrosso")){

            sql = "INSERT INTO ordine_artista(progetto, artista, quantità, tipo_ordine, stato, priorità, fatturazione) " +
                    "VALUES("+ordina+", '" +cf+ "',"+quantità+", '"+tipoOrdine+"', 'pagato', '"+priorità+"',"+metodoPagamento+" ) RETURNING id";
            System.out.println("Procedere all'ordine? SI/NO");
            String conferma = s.nextLine();
            if(conferma.equals("NO")){
                return;
            }
            try{
                ResultSet rs = statement.executeQuery(sql);
                rs.next();
                int ordineArtista = rs.getInt(1);
                sql = "INSERT INTO ordine_ingrosso(id, indirizzo_sped) VALUES("+ordineArtista+","+indirizzo+")";
                statement.executeUpdate(sql);
                System.out.println("Ordine eseguito con successo!");
            }catch(SQLException e){
                System.out.println("Il progetto deve essere ancora approvato!");
            }

        }else{
            sql = "INSERT INTO ordine_artista(progetto, artista, quantità, tipo_ordine, stato, priorità, fatturazione) " +
                    "VALUES("+ordina+", '" +cf+ "',"+quantità+", '"+tipoOrdine+"', 'pagato', '"+priorità+"',"+metodoPagamento+" )";
            System.out.println("Procedere all'ordine? SI/NO");
            String conferma = s.nextLine();
            if(conferma.equals("NO")) return;
            try{
                statement.executeUpdate(sql);
                System.out.println("Progetto ordinato con successo!");
            }catch(SQLException e){
                System.out.println("Il progetto deve essere ancora approvato!");
            }

        }


    }

    void visualizzaStato(){
        String sql = "SELECT ordine_artista.id, progetto, titolo, quantità, data, tipo_ordine, stato " +
                "FROM ordine_artista JOIN progetto ON progetto = progetto.id " +
                "WHERE ordine_artista.artista = '"+cf+"'";

        try{
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getInt(1) + " " + rs.getInt(2) + " " +
                        rs.getString(3) + " " + rs.getInt(4) + " " +
                        rs.getDate(5) + " " + rs.getString(6) + " " +
                        rs.getString(7));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void tracciaIngrosso(){

        String sql = "SELECT oi.id, oi.id_spedizione FROM ordine_ingrosso AS oi JOIN ordine_artista as oa ON oi.id = oa.id WHERE oa.artista = '"+cf+"'";
        try{
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println("Spedizione numero: " + rs.getInt(1));
                System.out.print("Stato: ");
                int stato = rs.getInt(2);
                if(stato > 0){
                    sql = "SELECT consegna_prevista FROM spedizione WHERE id = "+stato;
                    Connection connection2 = DriverManager.getConnection("jdbc:postgresql://db.marcorealacci.me:5556/CDClickBD2", "marco", "serafina");
                    Statement statement2 = connection2.createStatement();
                    ResultSet stators = statement2.executeQuery(sql);
                    stators.next();
                    System.out.print("Consegna prevista il ");
                    System.out.println(stators.getDate(1));
                }else{
                    System.out.println("Spedizione ancora da effettuare");
                }
            }


        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    void mettiInVetrina(){

        Scanner s = new Scanner(System.in);
        System.out.println("Digitare l'id del progetto da mettere in vetrina");
        int id = Integer.parseInt(s.nextLine());
        String sql = "SELECT esito FROM valutazione WHERE progetto = " + id;
        try{
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                if(rs.getBoolean("esito") != true){
                    System.out.println("Il progetto scelto non è stato approvato");
                    return;
                }
            }

            sql = "SELECT quantità_rimanente FROM stock WHERE progetto = "+id;
            rs = statement.executeQuery(sql);
            if(rs.next()){
                int stock = rs.getInt(1);
                if(stock == 0){
                    System.out.println("Non vi sono stock del progetto scelto, necessaria ordinazione");
                    return;
                }
            }

            System.out.println("Scegliere il prezzo al dettaglio per il progetto");
            int prezzo = Integer.parseInt(s.nextLine());
            sql = "INSERT INTO vetrina_online (id, prezzo_unit) VALUES ("+id+", "+prezzo+")";
            statement.executeUpdate(sql);
            System.out.println("Progetto messo in vetrina!");

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void supporto(){

        Scanner s = new Scanner(System.in);
        System.out.println("Descrivere la richiesta da effettuare");
        String richiesta = s.nextLine();
        String sql = "INSERT INTO richiesta_supporto(mittente, messaggio, stato) VALUES('"+cf+"','"+richiesta+"','ricevuta')";
        try{
            statement.executeUpdate(sql);
            System.out.println("Richiesta effettuata con successo");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    void associaTelefono(){
        System.out.println("Digitare il numero di telefono");
        Scanner s = new Scanner(System.in);
        String tel = s.nextLine();
        String sql = "INSERT INTO telefoni_utenti(cf,telefono) VALUES('"+cf+"','"+tel+"')";
        try{
            statement.executeUpdate(sql);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    void associaEmail(){
        System.out.println("Digitare l'email");
        Scanner s = new Scanner(System.in);
        String tel = s.nextLine();
        String sql = "INSERT INTO email_utenti(cf,email) VALUES('"+cf+"','"+tel+"')";
        try{
            statement.executeUpdate(sql);
            System.out.println("Email associata con successo!");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
