mvn clean install
docker cp .\target\temperature-measurement-tracker.war weather-payara:/opt/payara5/glassfish/domains/domain1/autodeploy