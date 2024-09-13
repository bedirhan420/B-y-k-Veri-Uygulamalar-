package com.xbank.bigdata.efthavale.rdb2mongo;

import com.mongodb.spark.MongoSpark;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;

public class Application {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String postgreURL = dotenv.get("POSTGRE_URL");
        String dbTable = dotenv.get("DB_TABLE");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");
        String mongoURI = dotenv.get("MONGO_URI")+"havaleislem";

        SparkSession sparkSession = SparkSession
                .builder()
                .master("local")
                .appName("PostgreSQL To MongoDB ")
                .config("spark.mongodb.output.uri",mongoURI)
                .getOrCreate();

         Dataset<Row> loadDS = sparkSession
                .read()
                .format("jdbc")
                .option("driver","org.postgresql.Driver")
                .option("url",postgreURL)
                .option("dbtable",dbTable)
                .option("user",dbUser)
                .option("password",dbPassword)
                .load();

        Dataset<Row> newDS = loadDS.withColumn("balance",loadDS.col("balance").cast(DataTypes.IntegerType));

        MongoSpark.write(newDS).mode("append").save();
    }
}
