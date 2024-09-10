import com.mongodb.spark.MongoSpark;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import java.util.concurrent.TimeoutException;

public class Streaming {
    public static void main(String[] args) {
        // Set Hadoop Home Directory
        System.setProperty("hadoop.home.dir", "C:\\hadoop-3.0.0");

        // Set TLS protocols
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
        System.setProperty("hadoop.native.lib", "false");


        // Retrieve MongoDB URI and Kafka server from environment variables
        String mongoUri = System.getenv("MONGO_URI");
        String kafkaServers = System.getenv("KAFKA_SERVERS");

        // Initialize SparkSession
        SparkSession sparkSession = SparkSession.builder()
                .master("local[*]") // Use all available cores
                .appName("Spark Search Analysis")
                .config("spark.mongodb.output.uri", mongoUri)
                .config("spark.hadoop.io.native.lib", "false") // Disable native IO
                .getOrCreate();

        // Define the schema of incoming data
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("search", DataTypes.StringType, true),
                DataTypes.createStructField("region", DataTypes.StringType, true),
                DataTypes.createStructField("current_ts", DataTypes.StringType, true),
                DataTypes.createStructField("user_id", DataTypes.IntegerType, true)
        });

        // Read the Kafka stream
        Dataset<Row> kafkaDS = sparkSession
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaServers)
                .option("subscribe", "searchv-analys-streaming")
                .load();

        // Convert Kafka data from binary to string
        Dataset<Row> stringDS = kafkaDS.selectExpr("CAST(value AS STRING)");

        // Parse the JSON data into columns using the defined schema
        Dataset<Row> jsonDS = stringDS
                .select(functions.from_json(functions.col("value"), schema).as("data"))
                .select("data.*");

        // Gerçek Zamanlı Ürün Takibi
        Dataset<Row> telFilter = jsonDS.filter(jsonDS.col("search").equalTo("telefon"));

        // Günün En Yoğun Saatleri
        Dataset<Row> curr_ts_window = jsonDS.groupBy(functions.window(jsonDS.col("current_ts"),"30 minute"),jsonDS.col("search")).count();

        try {
            // Write the streaming data to MongoDB
            telFilter.writeStream()
                    .trigger(Trigger.ProcessingTime("60 seconds")) // Trigger every 60 seconds
                    .foreachBatch((batchDF, batchId) -> {
                        MongoSpark.write(batchDF)
                                .option("collection", "SearchTelefon")
                                .save();
                    }).start().awaitTermination();

            curr_ts_window.writeStream()
                    .trigger(Trigger.ProcessingTime("60 seconds")) // Trigger every 60 seconds
                    .foreachBatch((batchDF, batchId) -> {
                        MongoSpark.write(batchDF)
                                .option("collection", "TimeWindowSearch")
                                .save();
                    }).start().awaitTermination();
        } catch (StreamingQueryException e) {
            // Handle StreamingQueryException
            e.printStackTrace();
        } catch (TimeoutException e) {
            // Handle TimeoutException
            e.printStackTrace();
        } finally {
            // Stop the SparkSession gracefully
            sparkSession.stop();
        }
    }
}
