package lt.jr.alfresco.authentication.jwt.services;

import com.nimbusds.jose.JOSEException;
import org.json.simple.JSONObject;

import java.util.Optional;

public interface JwtAuthenticationService {
    JSONObject createJwtAuthenticationToken(String userName, Optional<Integer> expireInDays) throws JOSEException;
    ValidateJwtTokenResult validateJwtToken(String token);
}
