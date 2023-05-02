package at.adesso.leagueapi.apigateway.gateway;

import at.adesso.leagueapi.commons.errorhandling.error.CommonError;
import at.adesso.leagueapi.commons.errorhandling.error.Error;
import at.adesso.leagueapi.commons.errorhandling.exception.ApiException;

public class UnauthorizedAccessException extends ApiException {
    public UnauthorizedAccessException() {
        super(CommonError.UNAUTHORIZED);
    }
}
