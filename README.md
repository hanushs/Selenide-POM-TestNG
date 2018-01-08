# Selenide-POM-TestNG

TO Configure startup of the tests you can change setting in local.properties files and set
desired options. Description can be found at http://selenide.org/javadoc in com.codeborne.selenide.Configuration package

Main of them are:
- selenide.browser - type of browser you want to start
- selenide.baseUrl - url of application tested
- remote - url of selenium hub

In order to run test open src/test/XMLSuites folder in IDE and select Run through right click on the test you want to run.

To run from comand line need to execute <<mvn -Dsurefire.suiteXmlFiles="fullPathToXmlSuite" test >> command


NOTE: not test configured to run on locally installed Pentaho


