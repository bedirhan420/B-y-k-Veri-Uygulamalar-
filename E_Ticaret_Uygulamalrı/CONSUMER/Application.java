import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.StructField;
import com.mongodb.spark.MongoSpark;

public class Application {
    public static void main(String[] args) {
        try {
            // Hadoop home directory ayarlama
            System.setProperty("hadoop.home.dir", "C:\\hadoop-3.0.0");
            System.setProperty("jdk.tls.client.protocols", "TLSv1.2");

            String mongoUri = System.getenv("MONGO_URI");
            String kafkaServers = System.getenv("KAFKA_SERVERS");

            // Spark oturumu oluşturma
            SparkSession sparkSession = SparkSession.builder()
                    .master("local")
                    .appName("Spark Search Analysis")
                    .config("spark.mongodb.output.uri", mongoUri)
                    .getOrCreate();

            // Kafka'dan veri okuma
            Dataset<Row> kafkaDS = sparkSession
                    .read()
                    .format("kafka")
                    .option("kafka.bootstrap.servers", kafkaServers)
                    .option("subscribe", "searchv-userid-v2")
                    .load();

            // Kafka verilerini String formatına dönüştürme
            Dataset<Row> stringDS = kafkaDS.selectExpr("CAST(value AS STRING)");
            stringDS.printSchema();

            // JSON verilerini ayrıştırma
            StructType schema = DataTypes.createStructType(new StructField[]{
                    DataTypes.createStructField("search", DataTypes.StringType, true),
                    DataTypes.createStructField("region", DataTypes.StringType, true),
                    DataTypes.createStructField("timestamp", DataTypes.StringType, true),
                    DataTypes.createStructField("user_id", DataTypes.IntegerType, true)

            });

            Dataset<Row> jsonDS = stringDS
                    .select(functions.from_json(functions.col("value"), schema).as("data"))
                    .select("data.*");

            jsonDS.printSchema();
            //jsonDS.show();

            // En çok aranan 10 ürünü listeleme
            Dataset<Row> searchGroup = jsonDS.groupBy("search").count();
            Dataset<Row> searchResult = searchGroup.orderBy(functions.desc("count")).limit(10);
            //searchResult.show();

            //Kullanıcıların Ürün Arama Sayıları
            Dataset<Row> count = jsonDS.groupBy("user_id", "search").count();
            Dataset<Row> pivot = count.groupBy("user_id").pivot("search").sum("count").na().fill(0);
            //pivot.show();
            // Sonuçları MongoDB'ye yazma
            MongoSpark.write(searchResult)
                    .option("collection", "top_searched_products")
                    .mode("overwrite")
                    .save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
