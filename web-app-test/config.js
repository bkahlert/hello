/* jshint esversion: 6 */
/* jshint strict: false */
const config = [
    {
        "key": "sls-hello-dev-DomainNameHttp",
        "value": "api.hello-dev.aws.choam.de"
    },
    {
        "key": "sls-hello-dev-HostedUiUrl",
        "value": "https://hello-dev-bkahlert-com.auth.eu-central-1.amazoncognito.com"
    },
    {
        "key": "sls-hello-dev-WebAppClientID",
        "value": "7lhdbv12q1ud9rgg7g779u8va7"
    }
];
export const apiUrl = `https://${config.filter(c => c.key === "sls-hello-dev-DomainNameHttp")[0].value}`;
export const hostedUiUrl = config.filter(c => c.key === "sls-hello-dev-HostedUiUrl")[0].value;
export const clientId = config.filter(c => c.key === "sls-hello-dev-WebAppClientID")[0].value;
