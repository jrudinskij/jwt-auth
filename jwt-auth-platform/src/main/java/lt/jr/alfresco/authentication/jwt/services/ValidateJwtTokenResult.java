package lt.jr.alfresco.authentication.jwt.services;

public class ValidateJwtTokenResult {
    private boolean valid;
    private String username;

    public ValidateJwtTokenResult(boolean valid, String username) {
        this.valid = valid;
        this.username = username;
    }

    public boolean isValid() {
        return valid;
    }

    public String getUsername() {
        return username;
    }
}
