import org.apache.spark.sql.{DataFrame, SparkSession}

object events_denorm extends App with Flatter{


  val spark = SparkSession.builder().master("local[*]").appName("Tamara-Asses").getOrCreate()

  import spark.implicits._
  spark.sparkContext.setLogLevel("ERROR")
  val driver = "com.mysql.jdbc.Driver"
  val EVENTS = spark.read
  .format("jdbc").option("driver", driver)
  .option("url", "jdbc:mysql://localhost:3306/tamara")
  .option("dbtable", "tamara.order_events")
  .option("user", "root")
  .option("password", "root")
  .load().createTempView("events")


  val eventsDF : Array[(String,DataFrame)] = spark.sql("select distinct event_name from events").collect().map(_.getString(0))
  .map(x => {
   val itr = x.split("Event")(1).mkString("")
    println(itr.replace("\\",""))
    val payloads = spark.sql(s"select payload from events where event_name like '%${itr.replace("\\","")}%'")
      .collect().map(_.getString(0)).map(_.replace("\\","").stripPrefix("\"").stripSuffix("\""))
    val payloadsDS =spark.createDataset[String](payloads)
    payloadsDS.printSchema()
    val payloadsDF = spark.read.json(payloadsDS)
    payloadsDF.printSchema()
    val flattenedpayloadDF = flattenDataframe(payloadsDF)
    flattenedpayloadDF.printSchema()
    (itr.replace("\\",""),flattenedpayloadDF)
  })
  eventsDF.foreach(event => {
    val prop = new java.util.Properties
    prop.setProperty("driver", driver)
    prop.setProperty("user", "root")
    prop.setProperty("password", "root")
    event._2.write.mode("append").jdbc("jdbc:mysql://localhost:3306/tamara", s"order_events_${event._1}", prop)
    println(s"Written Event Table for ${event._1} in table named order_events_${event._1}")
  })


}
