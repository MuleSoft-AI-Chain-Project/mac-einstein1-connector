MuleSoft Einstein Connector Demo
====================================
Anypoint Studio demo for MuleSoft Einstein Connector.


Prerequisites
---------------

* Anypoint Studio 7 with Mule ESB 4.6.9 Runtime.
* MuleChain Einstein Connector v1.0.0


How to Run Sample
-----------------

1. Import the project folder demo in Studio.
2. Update the Client Id, Client Secret, Salesforce Org
3. Save the configuration & run the application


About the Sample
----------------

You can use postman to trigger curls under the web server http://localhost:8081

## Endpoints

* POST - /promptTemplate (Prompt template)
* POST - /chat (Chat answer prompt)
* POST - /chatwithmemory (Chat answer prompt with memory)
* POST - /chatgenfrommsg (Chat Generate From Messages )
* POST - /embeddingAdd (Generate Embedding from Adhoc File Query)
* POST - /ragadhoc (Perform RAG over a file/doc provided)
* POST - /toolsai (Tools implementation)
