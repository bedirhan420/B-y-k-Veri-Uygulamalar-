package com.xbank.bigdata.efthavale.consumer;

import com.mongodb.spark.MongoSpark;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.TimeoutException;

public class Application {
    public static void main(String[] args) throws StreamingQueryException, TimeoutException {

        // .env dosyasından değişkenleri çekme
        Dotenv dotenv = Dotenv.load();

        String kafkaURL = dotenv.get("KAFKA_URL");
        String mongoURI = dotenv.get("MONGO_URI")+ ".products";
        String hadoopPath = dotenv.get("HADOOP_PATH");

        // Hadoop yolunu ayarlama
        System.setProperty("hadoop.home.dir", hadoopPath);

        // Spark session başlatma
        SparkSession sparkSession = SparkSession.builder()
                .master("local").appName("EFT/HAVALE Takip")
                .config("spark.mongodb.output.uri", mongoURI)
                .getOrCreate();

        // Şemaları tanımlama
        StructType accountSchema = new StructType()
                .add("iban", DataTypes.StringType)
                .add("oid", DataTypes.IntegerType)
                .add("title", DataTypes.StringType);

        StructType infoSchema = new StructType()
                .add("bank", DataTypes.StringType)
                .add("iban", DataTypes.StringType)
                .add("title", DataTypes.StringType);

        StructType schema = new StructType()
                .add("current_ts", DataTypes.TimestampType)
                .add("balance", DataTypes.IntegerType)
                .add("btype", DataTypes.StringType)
                .add("pid", DataTypes.IntegerType)
                .add("ptype", DataTypes.StringType)
                .add("account", accountSchema)
                .add("info", infoSchema);

        // Kafka'dan veri çekme
        Dataset<Row> loadDS = sparkSession.readStream().format("kafka")
                .option("kafka.bootstrap.servers", kafkaURL)
                .option("subscribe", "efthavale").load()
                .selectExpr("CAST(value AS STRING)");

        // JSON formatındaki veriyi ayıklama
        Dataset<Row> rawDS = loadDS.select(functions.from_json(loadDS.col("value"), schema).as("data"))
                .selectExpr("data.*");

        // Havaleler için filtreleme (ptype = "H")
        Dataset<Row> havaleTypeDS = rawDS.filter(rawDS.col("ptype").equalTo("H"));

        // 1 dakikalık pencerede işlem türüne göre havale toplamlarını hesaplama
        Dataset<Row> volumeDS = havaleTypeDS.groupBy(functions.window(havaleTypeDS.col("current_ts"), "1 minute"),
                        havaleTypeDS.col("btype"))
                .sum("balance");

        // Veriyi MongoDB'ye yazma
        volumeDS.writeStream().outputMode("complete").foreachBatch(new VoidFunction2<Dataset<Row>, Long>() {
            @Override
            public void call(Dataset<Row> rowDataset, Long aLong) throws Exception {
                try {
                    MongoSpark.write(rowDataset).mode("append").save();
                } catch (Exception e) {
                    System.err.println("MongoDB'ye yazarken hata: " + e.getMessage());
                }
            }
        }).start().awaitTermination();

    }
}
