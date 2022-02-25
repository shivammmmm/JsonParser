./bin/spark-submit \
  --class events_denorm \
  --master local[*] \
  --deploy-mode cluster \
  ./Tamara-Asses/target/Tamara-Asses-1.0-SNAPSHOT.jar