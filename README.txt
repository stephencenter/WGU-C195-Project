Scheduler v1.0 by Stephen Center

Date: 10/19/2021
IDE: IntelliJ IDEA 2021.1.1
JDK: Java 11.0.11
JavaFX: javafx-sdk11.0.2
MYSQL: mysql-connector-java-8.0.25

The purpose of the application is to assist with managing schedules for 
appointments. It allows the user to view, add, delete, and modify both customers
and appointments. It also provides alerts when there are upcoming appointments 
and can generate various reports. Report options include number of 
appointments by both type and month, a schedule for each contact, and the 
number of appointments scheduled in the past, present, and future.


Before attempting to run, ensure that the mysql-connector and JavaFX runtime are
properly attached to the project. They should be included in the lib/ folder in 
the main directory.

The application can be launched by running the Main.java file located in the
src folder. This will bring up the login screen, available in both English and
French. Enter your information and it will bring you to the main menu. From here
you have the option to view Appointments, Customers, Reports, or to log out. 
Choosing Appointments or Customers will bring you to their respective page where 
you can view, add, delete, and modify the items in the tables. The reports page 
has three tabsat the bottom that allow you to view different information about 
the scheduled appointments. Pressing log out will return you to the login 
screen.

Javadocs are located in the Javadocs/ folder in the main directory. Run
Javadoc/index.html to view the documentation.