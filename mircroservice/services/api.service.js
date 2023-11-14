const { ServiceBroker } = require("moleculer");
const ApiGateway = require("moleculer-web");
const _ = require("lodash");
const { MoleculerClientError } = require("moleculer").Errors;

module.exports = {
    name: "api",
    mixins: [ApiGateway],

    settings: {
        port: 8060,
        // Global CORS settings for all routes
        cors: {
            // Configures the Access-Control-Allow-Origin CORS header.
            origin: "*",
            // Configures the Access-Control-Allow-Methods CORS header.
            methods: ["GET", "OPTIONS", "POST", "PUT", "DELETE"],
            // Configures the Access-Control-Allow-Headers CORS header.
            allowedHeaders: [],
            // Configures the Access-Control-Expose-Headers CORS header.
            exposedHeaders: [],
            // Configures the Access-Control-Allow-Credentials CORS header.
            credentials: false,
            // Configures the Access-Control-Max-Age CORS header.
            maxAge: 3600
        },
        routes: [{
            path: "/api",
            bodyParsers: {
                json: { limit: "5MB" },
                urlencoded: { extended: true, limit: "5MB" }
            },
            autoAliases:true,
            authorization: true,
        }],
    }
};
