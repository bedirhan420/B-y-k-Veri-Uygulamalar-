package com.xbank.bigdata.kredikartitakip;

import com.mongodb.spark.MongoSpark;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.hamcrest.core.DescribedAs;

public class Application {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String mongoURI = dotenv.get("MONGO_URI")+"havaleislem";

        SparkSession sparkSession = SparkSession
                .builder()
                .master("local")
                .appName("Kredi Kartı Takip")
                .config("spark.mongodb.uri",mongoURI)
                .getOrCreate();

        Dataset<Row> rawDS = sparkSession
                .read()
                .option("header",true)
                .option("inferSchema",true)
                .csv("kredikartislem.csv");

        Dataset<Row> processDS = rawDS.groupBy("oid","ptype").count();
        Dataset<Row> resultDS = processDS.groupBy("oid").pivot("ptype").sum("count").na().fill(0);
        Dataset<Row> qrCodeDS = resultDS.filter(resultDS.col("1000").$greater(2).and(resultDS.col("1001").equalTo(0)));
        Dataset<Row> resultQrCodeDS = qrCodeDS.sort(functions.desc("1000"));
        Dataset<Row> cardDS = resultDS.filter(resultDS.col("2000").$greater(3).and(resultDS.col("2001").equalTo(0))).sort(functions.desc("2000"));

        Dataset<Row> windowDayDS = rawDS.groupBy(functions.window(rawDS.col("current_ts"),"4 hour"),rawDS.col("ptype"))
                .count().groupBy("window").pivot("ptype").sum("count").na().fill(0);


        MongoSpark.write(resultQrCodeDS).option("collection","qrcode").mode("append").save();
        MongoSpark.write(cardDS).option("collection","creditcard").mode("append").save();
        MongoSpark.write(windowDayDS).option("collection","windowDay").mode("append").save();

    }
}
