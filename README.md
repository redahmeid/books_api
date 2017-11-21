Welcome to Complete the Story Books API
==============================================

This is a project created as a demo for a talk I ran at Tesco.

The premise is of a book writing crowd sourcing service.

What's Here
-----------

This demo includes:

* README.md - this file
* buildspec.yml - this file is used by AWS CodeBuild to build the web
  service
* pom.xml - this file is the Maven Project Object Model for the web service
* src/ - this directory contains your Java service source files
* template.yml - this file contains the Serverless Application Model (SAM) used
  by AWS Cloudformation to deploy your application to AWS Lambda and Amazon API
  Gateway.


The architecture
----------------

Complete the story is built on the AWS platform fully.

APIs are run on API Gateway

Authentication is via AWS Cognito

Business logic is via AWS Lambdas

Database is Neo4j

Code is deployed using AWS SAM template - running Code Build, Code Deploy, Cloud Formation and Code Pipeline


TODOs
-----

Split out Lamdas in their own repos

Start doing proper TDD (would have saved me so much headache)

Add swagger with AWS extensions - this saves time when deploying changes to template.yml, asthis wipes out custom authorizers

Create a developer hub to expose these

Create a mobile app
