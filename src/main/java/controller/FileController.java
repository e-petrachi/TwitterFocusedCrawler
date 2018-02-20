package controller;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import db.MongoCRUD;

import java.io.*;

public class FileController {

    private DB db;

    public FileController(){}

    public void saveCluster(int num) throws IOException {
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("tfc");
        this.db = mongoCRUD.getDb();

        this.saveCSV(num);
        this.saveARFF(num);

    }
    private void saveCSV(int num) throws IOException{
        DBCollection collection0 = null;

        if (num == 0){
            collection0 = db.getCollection("label2");
        } else {
            collection0 = db.getCollection("label" + num);
        }
        DBCursor cursor0 = collection0.find();

        BufferedWriter writer = new BufferedWriter(new FileWriter("cluster" + num + ".csv"));
        String head = "";

        int lunghezza = 0;
        cursor0.next();

        lunghezza = cursor0.curr().keySet().size()-1;
        for (int i=0;i<lunghezza;i++) {
            String s = ((String) cursor0.curr().get("" + i)).toLowerCase().replaceAll(" |,|;|:", "_").replaceAll("\\.|!|\\?|\\'","");
            head = head + s + ";";
        }
        head = head + "\n";

        writer.write(head);


        DBCollection collection = db.getCollection("cluster" + num);

        DBCursor cursor = collection.find();

        while (cursor.hasNext()) {
            cursor.next();
            if (cursor.curr().get("" + 0) != null ) {
                String line = "";
                for (int i = 0; i < lunghezza; i++) {
                    line = line + cursor.curr().get("" + i) + ";";
                }
                line = line + "\n";
                writer.write(line);
            }
        }

        writer.close();
    }
    private void saveARFF(int num) throws IOException{
        DBCollection collection0 = null;

        if (num == 0){
            collection0 = db.getCollection("label2");
        } else {
            collection0 = db.getCollection("label" + num);
        }
        DBCursor cursor0 = collection0.find();

        BufferedWriter writer = new BufferedWriter(new FileWriter("cluster" + num + ".arff"));
        String head = "@RELATION cluster" +  num + "\n\n";

        int lunghezza = 0;


        cursor0.next();
        lunghezza = cursor0.curr().keySet().size()-1;

        System.out.println("\tATTRIBUTI # " + lunghezza);

        for (int i=0;i<lunghezza;i++) {
            String s = ((String) cursor0.curr().get("" + i)).toLowerCase().replaceAll(" |,|;|:", "_").replaceAll("\\.|!|\\?|\\'","");
            head = head + "@ATTRIBUTE " + s + " NUMERIC\n";
        }
        head = head + "\n";

        head = head + "\n\n@DATA\n";
        writer.write(head);

        DBCollection collection = db.getCollection("cluster" + num);
        DBCursor cursor = collection.find();

        int rows = 0;
        while (cursor.hasNext()) {
            cursor.next();
            if (cursor.curr().get("" + 0) != null ) {
                String line = "";
                for (int i = 0; i < lunghezza; i++) {
                    line = line + cursor.curr().get("" + i) + ",";
                }
                line = line + "\n";
                writer.write(line);
                rows++;
            }
        }

        System.out.println("\tDATI # " + rows);

        writer.close();
    }
    public void saveTokens(String token, String secret){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(".env"));
            writer.write(token + "\n");
            writer.write(secret + "\n");
            writer.close();
        } catch (IOException e) { }

    }
}
