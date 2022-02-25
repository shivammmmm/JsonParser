./bin/spark-submit \
  --class events_denorm \
  --master local[*] \
  --deploy-mode cluster \
  ./AdvancedJsonParsing/target/AdvancedJsonParsing-1.0-SNAPSHOT.jar