# Database-for-a-CD-printing-company

A project developed by me and a collegue at the Sapienza University of Rome for the "Databases - 2nd module" exam, it consists in the creation from scratch of a database for a CD printing company. The database creation starts from the definition of an ER schema which is then translated into a Relational Database coded with PostgreSQL and accessed through a Java application. 

The database handles usersion, musical projects, orders, product and everything related to any of the aforementioned. 

## Operations

The Java application allows the user to login under any of the roles defined in the database (client, company worker, assistant, admin...) aswell as allowing the creation of new users under any role. After the login phase the user is presented with an interface that allows him to execute tasks related to his role. for example: 

- An artist might create projects, order their production and put them on sale on the online storefront;
- A client might order CDs or open support requests;
- A worker might start the prodution of an order;
- A support specialist might reply to a ticket;



