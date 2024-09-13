package com.xbank.bigdata.efthavale.producer;

import com.xbank.bigdata.efthavale.producer.interfaces.DataGenerator;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.*;

public class ConcreteDataGenerator implements DataGenerator {
    public static List<String> names = new ArrayList<>();
    public static List<String> surnames = new ArrayList<>();
    public static Random rand = new Random();
    public static List<String> btype = Arrays.asList("TL", "USD", "EUR");

    // Statik olarak artan pid için başlangıç noktası
    private static long pidCounter = 1000000000L;  // Başlangıç değeri

    static {
        try {
            File fileName = new File("C:\\Users\\bedir\\IdeaProjects\\efthavaletakip\\src\\main\\java\\com\\xbank\\bigdata\\efthavale\\producer\\isimler.txt");
            File fileSurname = new File("C:\\Users\\bedir\\IdeaProjects\\efthavaletakip\\src\\main\\java\\com\\xbank\\bigdata\\efthavale\\producer\\soyisimler.txt");

            try (Scanner fileNameScanner = new Scanner(fileName); Scanner fileSurnameScanner = new Scanner(fileSurname)) {
                while (fileNameScanner.hasNext()) {
                    names.add(fileNameScanner.nextLine());
                }
                while (fileSurnameScanner.hasNext()) {
                    surnames.add(fileSurnameScanner.nextLine());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("One or more files not found: " + e.getMessage());
        }
    }

    @Override
    public String generate() {
        if (names.isEmpty() || surnames.isEmpty()) {
            return "Error: Name or surname lists are empty.";
        }

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        JSONObject data = new JSONObject();
        data.put("pid", generateID());
        data.put("ptype", "H");

        JSONObject account = new JSONObject();
        account.put("oid", generateID());
        account.put("title", generateNameSurname());
        account.put("iban", "TR" + generateID());

        data.put("account", account);

        JSONObject info = new JSONObject();
        info.put("title", generateNameSurname());
        info.put("iban", "TR" + generateID());
        info.put("bank", "X-Bank");

        data.put("info", info);
        data.put("balance", rand.nextInt(1000000));
        data.put("btype", btype.get(rand.nextInt(btype.size())));
        data.put("current_ts", ts.toString());

        return data.toJSONString();
    }

    // Artan pid değeri üreten metot
    private static synchronized long generateID() {
        return pidCounter++;
    }

    private static String generateNameSurname() {
        String name = names.get(rand.nextInt(names.size()));
        String surname = surnames.get(rand.nextInt(surnames.size()));
        return name + " " + surname;
    }
}
