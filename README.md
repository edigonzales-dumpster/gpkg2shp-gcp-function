# gpkg2shp-gcp-function

## Todo
- `sogis-gdi-prod` Projekt
- Tests (unit=done)
- Pipeline (inkl. Deployment)
- DXF
- CSV
- Beispiel HTML/JS-Client (nginx?)
- Firebase (https://cloud.google.com/functions/docs/securing/authenticating / )

## Integration-Tests
Achtung: https://github.com/unbroken-dome/gradle-testsets-plugin#eclipse

```
java -jar libs/java-function-invoker-1.0.0-beta1.jar --classpath 'build/libs/gpkg2shp-gcp-function-1.0.LOCALBUILD-all.jar' --target ch.so.agi.functions.Gpkg2Shp

curl -F "file=@./src/test/data/ch.so.agi.av-gb-administrative-einteilung.gpkg" localhost:8080 > fubar.zip
```
```
./gradlew clean build shadowJar integrationTest
```

## Developing
```
mvn archetype:generate -DgroupId=ch.so.agi -DartifactId=gpkg2shp-gcp-function -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
```

```
mvn function:run
```

```
curl -F "file=@./src/test/data/ch.so.agi.av-gb-administrative-einteilung.gpkg" localhost:8080 > fubar.zip
```

```
mvn function:run -Drun.functionTarget=your.package.yourFunction

gcloud auth login
gcloud config set project gpkg2shpfn


mvn function:deploy -Dfunction.deploy.name=gpkg2shp -Dfunction.deploy.region=europe-west1 -Dfunction.deploy.triggerhttp=true -Dfunction.deploy.runtime=java11 -Dfunction.deploy.allowunauthenticated=true -Dfunction.deploy.memory=1024 -Dfunction.deploy.functiontarget=ch.so.agi.functions.Gpkg2Shp -Dfunction.deploy.projectId=gpkg2shpfn

mvn function:deploy -Dfunction.deploy.name=gpkg2shp -Dfunction.deploy.region=europe-west1 -Dfunction.deploy.triggerhttp=true -Dfunction.deploy.runtime=java11 -Dfunction.deploy.allowunauthenticated=false -Dfunction.deploy.memory=1024 -Dfunction.deploy.functiontarget=ch.so.agi.functions.Gpkg2Shp -Dfunction.deploy.projectId=gpkg2shpfn
```


```
curl -F "file=@./src/test/data/ch.so.agi.av-gb-administrative-einteilung.gpkg" https://europe-west1-gpkg2shpfn.cloudfunctions.net/gpkg2shp
```
-> Probleme mit http2 und nicht mehr erlaubten Header?

```
curl --http1.1 -v -F "file=@./src/test/data/ch.so.agi.av-gb-administrative-einteilung.gpkg" https://europe-west1-gpkg2shpfn.cloudfunctions.net/gpkg2shp > fubar.zip
```


## IAM
- Service Account erstellen
- Service Account die Role "Cloud Functions Invoker" zuweisen.
- Unter Service Accounts bei Dotted Hamburger (Actions) "Create Keys"
- Service Account aktivieren: gcloud auth activate-service-account --key-file=/Users/stefan/Downloads/gpkg2shpfn-808f61d8b4a5.json
- Token für Funktion-Aufruf auslesen: gcloud auth print-identity-token gpkg2shp-user@gpkg2shpfn.iam.gserviceaccount.com
- curl --http1.1 -v -F "file=@./src/test/data/ch.so.agi.av-gb-administrative-einteilung.gpkg" https://europe-west1-gpkg2shpfn.cloudfunctions.net/gpkg2shp -H "Authorization: bearer MY_TOKEN"

## Links
- https://cloud.google.com/functions/docs/securing/authenticating#end-users
- https://github.com/GoogleCloudPlatform/functions-framework-java/blob/master/invoker/function-maven-plugin/src/main/java/com/google/cloud/functions/plugin/DeployFunction.java
- https://stackoverflow.com/questions/57122047/google-cloud-function-not-created-with-private-access
- https://cloud.google.com/functions/docs/securing/managing-access-iam


## Gradle
./gradlew runFunction -PrunFunction.target=ch.so.agi.functions.Gpkg2Shp -PrunFunction.port=8080