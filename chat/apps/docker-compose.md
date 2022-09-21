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

| Nom              | V-Path    | Repo (dossier)                                         | Port                           |
|------------------|-----------|--------------------------------------------------------|--------------------------------|
| **Front Chat**   | [:80]() / | [northernlights-chat-ui](northernlights-chat-ui)       | [:8081](http://localhost:8081) |
| **API Chat**     | [:80]() /api      | [northernlights-chat-api](northernlights-chat-api)     | [:3000](http://localhost:8080) |

Target

| Nom              | V-Path                | Repo (dossier)                                         | Port                           |
|------------------|-----------------------|--------------------------------------------------------|--------------------------------|
| **Front Home**   | [:80]() /             | [northernlights-ui](northernlights-ui)                 | [:8080](http://localhost:8080) |
| **Front Chat**   | [:80]() /chat/www     | [northernlights-chat-ui](northernlights-chat-ui)       | [:8081](http://localhost:8081) |
| **API Chat**     | [:80]() /chat/api     | [northernlights-chat-api](northernlights-chat-api)     | [:3000](http://localhost:3001) |
| **API Chat SSE** | [:80]() /chat/api/sse | [northernlights-chat-sse](northernlights-chat-api-sse) | [:3001](http://localhost:3002) |

See [docker-compose.yml](/docker-compose.yml) and [Makefile](/../../Makefile) at the root to ease build and run, basically :
- Have Docker and Docker-compose on the target machine:
- Allow the certificate on the target machine
- Run 'make' to build java images, and starting containers
- Go on https://localhost