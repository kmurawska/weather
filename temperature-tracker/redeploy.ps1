mvn clean install
docker cp .\target\temperature-tracker.war weather-payara:/opt/payara5/glassfish/domains/domain1/autodeploy