### TOMCAT DEPLOY CONF ###
1. Използва се tomcat server
2. Да се добави <user username="admin" password="admin" roles="manager-script,admin,manager-gui,admin-gui" /> в tomcat-users.xml

### EAUTH TEST/PRODUCTION ENV###
1. Да се добави bg.bulsi.eauth.config.dir.path=/home/{USER}/ECLIPSE_WORKSPACES/eforms/eforms-parent/conf в catalina.properties
2. Да се коригират параметрите в confEauth.properties файла

### EPDAEU TEST/PRODUCTION ENV###
1. Да се добави bg.bulsi.epdaeu.config.dir.path=/home/{USER}/ECLIPSE_WORKSPACES/eforms/eforms-parent/conf в catalina.properties

### EDELIVERY TEST/PRODUCTION ENV###
1. Да се добави bg.bulsi.edelivery.config.dir.path=/home/{USER}/ECLIPSE_WORKSPACES/eforms/eforms-parent/conf в catalina.properties
2. Да се коригират параметрите в confEdelivery.properties

### EFORMS TEST/PRODUCTION ENV###
1. Да се добави app.config.path=/home/{USER}/Desktop/conf в catalina.properties
2. Да се добави confEforms.properties в тази директория и да се коригират параметрите
3. Да се добави datasource.properties в тази директория и да се коригират параметрите

## Достъп до h2 конзолата ###
localhost:8080/eforms-portal/console
