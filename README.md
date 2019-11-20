# Singtel
Singtel
Approach and solution

I have written one AEM Servlet which work on the selector . Like any of the page if you give the rendition like .header.html it will give the response of all of the child page based on the limit which can be updated in osgi. by default it is being set as 2. Properties returned in the json like page title , path can be configured throguh OSGI.

I am writing the navigation response to dam so that this servlet calls only on author when any changes are being done on the navigation hierarchy and can be published to aem publisher .

/content/dam/AEMMaven13/navigation.json can be cached on webserver as well as cdn so that in case of server down also page will be rendered as html ans json will be fully cached.

performance will be optimal as no call will go to aem servlet , as browser will make a ajax call which will read the data from dam .

i have used ws retail template to created sample aem pages

navigation hierarchy can be check on the url http://localhost:4504/editor.html/content/example/ca/en.html

Full build can be done via mvn clean install -P autoInstallPackage

Please USER AEM 6.4 default instance running on 4504 port.
