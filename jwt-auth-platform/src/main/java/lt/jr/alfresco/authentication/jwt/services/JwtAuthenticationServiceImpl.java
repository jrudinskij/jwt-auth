package lt.jr.alfresco.authentication.jwt.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.rest.framework.core.exceptions.InvalidArgumentException;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {
    private static final int MAX_EXPIRE_IN_DAYS = 730;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationServiceImpl.class);

    private PersonService personService;

    @Value("#{'https://' + '${share.host}'}")
    private String issuer;

    @Value("${jwt.shared.secret}")
    private String sharedSecret;

    @Autowired
    public JwtAuthenticationServiceImpl(
            @Qualifier("PersonService") PersonService personService) {
        this.personService = personService;
    }

    @Override
    public ValidateJwtTokenResult validateJwtToken(String token) {
        try {
            ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWKSource<SimpleSecurityContext> jweKeySource = new ImmutableSecret<>(sharedSecret.getBytes());
            JWEKeySelector<SimpleSecurityContext> jweKeySelector =
                    new JWEDecryptionKeySelector<>(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256, jweKeySource);
            jwtProcessor.setJWEKeySelector(jweKeySelector);
            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
            boolean isValid = StringUtils.equals(issuer, claimsSet.getIssuer())
                    && new Date().before(claimsSet.getExpirationTime());
            return new ValidateJwtTokenResult(isValid, claimsSet.getStringClaim("user"));
        } catch(JOSEException | ParseException | BadJOSEException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error validating jwt token", e);
            }
            return new ValidateJwtTokenResult(false, null);
        }
    }

    @Override
    public JSONObject createJwtAuthenticationToken(String userName, Optional<Integer> expireInDays) throws JOSEException {
        if(StringUtils.isEmpty(sharedSecret)) {
            throw new AlfrescoRuntimeException("The global property jwt.shared.secret is not provided");
        }
        if (!personService.personExists(userName)) {
            throw new AlfrescoRuntimeException("Person does not exist: " + userName);
        }
        int expireValue = expireInDays.orElse(1);
        if(expireValue < 0 || expireValue > MAX_EXPIRE_IN_DAYS) {
            throw new InvalidArgumentException("Invalid expire in days value. Should be positive and less or equal " + MAX_EXPIRE_IN_DAYS);
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, expireValue);
        Date expirationTime = c.getTime();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .expirationTime(expirationTime)
                .claim("user", userName)
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWEHeader jweHeader = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256);
        JWEEncrypter jweEncrypter = new DirectEncrypter(sharedSecret.getBytes());
        JWEObject jweObject = new JWEObject(jweHeader, payload);
        jweObject.encrypt(jweEncrypter);

        JSONObject jwtTokenObj = new JSONObject();
        jwtTokenObj.put("token", jweObject.serialize());
        jwtTokenObj.put("expires", DateFormatUtils.ISO_DATETIME_FORMAT.format(expirationTime));

        return jwtTokenObj;
    }
}
