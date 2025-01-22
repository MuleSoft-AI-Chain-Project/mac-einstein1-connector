MuleSoft Anypoint Agentforce Connector Demo
====================================
Anypoint Studio demo for MuleSoft Anypoint Agentforce Connector Bot Operations.


Prerequisites
---------------

* Anypoint Studio 7 with Mule ESB 4.6.9 Runtime.
* Mulesoft Anypoint Agentforce Connector v1.0.0


How to Run Sample
-----------------

1. Import the project folder demo in Studio.
2. Update the Client Id, Client Secret and Salesforce Org URL(token URL) in the mule-artifact.properties
3. Save the configuration & run the application


About the Sample
----------------

You can use postman to trigger curls under the web server http://localhost:8081

## Endpoints

* **POST** - /agentConversation
    * **params**: _{ "prompt":"<PromptValue>" }_
