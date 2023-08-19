package lt.jr.alfresco.authentication.external.jwt;

import lt.jr.alfresco.authentication.jwt.services.JwtAuthenticationService;
import lt.jr.alfresco.authentication.jwt.services.ValidateJwtTokenResult;
import org.alfresco.repo.security.authentication.external.DefaultRemoteUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class JwtRemoteUserMapper extends DefaultRemoteUserMapper {

    private JwtAuthenticationService jwtAuthenticationService;

    @Override
    public String getRemoteUser(HttpServletRequest req) {
        return validateBearerToken(req)
                .filter(ValidateJwtTokenResult::isValid)
                .map(ValidateJwtTokenResult::getUsername)
                .orElseGet(() -> super.getRemoteUser(req));
    }

    @Autowired
    public void setJwtAuthenticationService(JwtAuthenticationService jwtAuthenticationService) {
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @Value("${external.authentication.enabled}")
    @Override
    public void setActive(boolean isEnabled) {
        super.setActive(isEnabled);
    }

    private Optional<ValidateJwtTokenResult> validateBearerToken(HttpServletRequest req) {
        String authorization = req.getHeader("Authorization");
        if(StringUtils.isBlank(authorization)) {
            return Optional.empty();
        }
        String[] authorizationParts = authorization.split(" ");
        if(!"Bearer".equalsIgnoreCase(authorizationParts[0]) || authorizationParts.length < 2) {
            return Optional.empty();
        }
        return Optional.of(jwtAuthenticationService.validateJwtToken(authorizationParts[1]));
    }
}
