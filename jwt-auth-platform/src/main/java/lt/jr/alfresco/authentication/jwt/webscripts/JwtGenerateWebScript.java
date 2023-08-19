package lt.jr.alfresco.authentication.jwt.webscripts;

import com.nimbusds.jose.JOSEException;
import lt.jr.alfresco.authentication.jwt.services.JwtAuthenticationService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Generates JSON Web Token for provided user
 */
public class JwtGenerateWebScript extends AbstractWebScript {

    private JwtAuthenticationService jwtAuthenticationService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String user = templateVars.get("user");
        Optional<Integer> expireInDays = Optional.ofNullable(templateVars.get("expireInDays"))
                .map(Integer::parseInt);
        try {
            JSONObject json = jwtAuthenticationService.createJwtAuthenticationToken(user, expireInDays);
            res.setContentType("application/json");
            json.writeJSONString(res.getWriter());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setJwtAuthenticationService(JwtAuthenticationService jwtAuthenticationService) {
        this.jwtAuthenticationService = jwtAuthenticationService;
    }
}
