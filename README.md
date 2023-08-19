# JWT Authentication Subsystem
This addon implements an authentication subsystem (jwt-auth:external) to authenticate requests
to Alfresco REST API by validating JWT (JSON Web Token) tokens.
## Compatibility
The addon is built to be compatible with Alfresco 5.2 and above.
## Features
This addon consists of the platform (repository) and Share modules.
- The Share module provides Share UI to generate JWT tokens for a specified user.
- The platform module provides a Web script to serve the Share UI in generating JWT tokens for user.
- Likewise, the platform module provides a custom authentication subsystem (jwt-auth:external) to authenticate requests
by JWT token.
- The JWT tokens must be sent with REST calls in Bearer authentication header (Authorization: Bearer <token>).
## Configuration
To enable the jwt-auth:external authentication subsystem you need to provide the following property in the alfresco-global.properties file:
```
external.authentication.enabled=true
```
Apart from that you need to provide the following secret property in alfresco-global.properties for JWT token generation:
```
jwt.shared.secret=
```
The secret is an aes-128-cbc key, which may be generated online as follows: https://asecuritysite.com/encryption/keygen 

