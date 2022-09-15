## Setup local environment
For routing `https://localhost/**` to specific containers,
`jwilder/nginx-proxy` is used to route by path to a container

### Install certificate for localhost

To generate a certificate to be accepted (once) in Chrome
(nss is only needed if you are using Firefox)

```
brew install mkcert nss
```

Then to bind mkcert on your machine
```
mkcert -install
```

Generate certificates, then rename them and install .crt in the keychain (macOS)
```
mkcert -key-file key.pem -cert-file cert.pem localhost
```

Then rename :
- `cert.pem` to `localhost.crt`
- `key.pem` to `localhost.key`

Current

| Nom              | Repo (dossier)                                         | V-Path | Port                           |
|------------------|--------------------------------------------------------|--------|--------------------------------|
| **Front Chat**   | [northernlights-chat-ui](northernlights-chat-ui)       | /      | [:8081](http://localhost:8081) |
| **API Chat**     | [northernlights-chat-api](northernlights-chat-api)     | /api   | [:3000](http://localhost:8080) |

Target

| Nom              | Repo (dossier)                                         | V-Path        | Port                           |
|------------------|--------------------------------------------------------|---------------|--------------------------------|
| **Front Home**   | [northernlights-ui](northernlights-ui)                 | /             | [:8080](http://localhost:8080) |
| **Front Chat**   | [northernlights-chat-ui](northernlights-chat-ui)       | /chat/www     | [:8081](http://localhost:8081) |
| **API Chat**     | [northernlights-chat-api](northernlights-chat-api)     | /chat/api     | [:3000](http://localhost:3001) |
| **API Chat SSE** | [northernlights-chat-sse](northernlights-chat-api-sse) | /chat/api/sse | [:3001](http://localhost:3002) |
