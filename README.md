# RFQ Structured Object Bot

## Introduction

This example shows how a Symphony chat bot using the [SymphonyOSS symphony-java-client](https://github.com/symphonyoss/symphony-java-client) and Dropwizard can be used to manage the life cycle of an RFQ. The bot takes the workflow since the initial request, through quoting and negotiation, all the way to order creation. This integration makes use of the Extension API [entity-service](https://extension-api.symphony.com/docs/entity-service) to render bot messages as interactive forms. The extension app, uses iFrames to render an Angular app that connects via REST API to this project to display the current status of the RFQ at different stages and pass the input information for the bot to post it back into the room for retention and time stamp purposes. 

## Pre-requisites

    1. Service account with a valid certificate for the bot
    2. Extension API application with a entity renderer for the structured objects
    3. Web application that is rendered by the Extension App
    4. For users to see the objects rendered properly they need to have the app enabled in the pod and installed for their specific user.
        -Mobile and ECM do not render structured objects.
        -For XPOD use of this integration, the app would need to be deployed at the firms that communicate with the bot.

## Overview

* At startup, the `RFQBot` is initialized as a Chat and Room listener so the implemented methods respond to the events of the datafeed for the bot. Go to this class to find most implementation details. 

* `RFQBotResource` class is where the endpoints that are called by the Angular app are defined to move the RFQ through all the different stages

* `RFQInfoResource` class is where the endpoints that are called by the Angular app are defined to gather data about an RFQ and display it.

* `AppAuthResource` class is where extension app appauth endpoint are to complete the circle of trust for your renderer extension app (https://extension-api.symphony.com/v1.48/docs/application-authentication)


## Running This Sample

Set up your config in `example.yml`. Fill out the following parameters.

        sessionAuthURL: https://your-pod.symphony.com/sessionauth
        keyAuthUrl: https://your-km.symphony.com:8444/keyauth
        localKeystorePath: complete path to your jks keystore
        localKeystorePassword: keystore password
        botCertPath: complete path to your bot's p12 file
        botCertPassword: password
        botEmailAddress: bot.user@example.com
        agentAPIEndpoint: https://your-agent.symphony.com/agent
        podAPIEndpoint: https://your-pod.symphony.com/pod
        mongoURL: URL to your mongo instance
        appAuthBase: https://your-pod.symphony.com:8444
        appAuthPath: /sessionauth/v1/authenticate/extensionApp
        appCertPath: <path to p12 file of cert which has a CN that matches your app's id>
        appCertPassword: <password>
        symphCertBaseURL: https://your-pod.symphony.com:8444
        symphCertPathURL: /sessionauth/v1/app/pod/certificate
        
Set these properties to setup https endpoints

        keyStorePath: 
        keyStorePassword: 

If you are developing a bot that lives within an enterprise pod with on-premise components (KM and Agent) and need a proxy to reach the cloud (your pod) add the following field to your sample.yml file

        proxyURL: url to your internal proxy
        proxyUsername:
        proxyPassword


To test the application run the following commands.

* To package the example run.

        mvn package

* To run the server run.

        java -jar target/RFQ-form-bot-1.1.0-SNAPSHOT-sources.jar server example.yml
