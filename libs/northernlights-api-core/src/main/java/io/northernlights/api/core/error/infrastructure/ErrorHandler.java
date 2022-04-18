package io.northernlights.api.core.error.infrastructure;

import io.northernlights.api.core.error.domain.ForbiddenActionException;
import io.northernlights.api.core.error.domain.ForbiddenStateException;
import io.northernlights.api.core.error.domain.ResourceNotFoundException;
import io.northernlights.api.core.error.domain.UnauthorizedActionException;
import io.northernlights.security.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Converts business Exception into HTTP Error with attached status code
 */
@Slf4j
public class ErrorHandler implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request)
            .doOnError(e -> log.error("An error occurred during API call", e))
            .onErrorMap(IllegalArgumentException.class, BadRequestException::new)
            .onErrorMap(AuthenticationException.class, UnauthorizedException::new)
            .onErrorMap(UnauthorizedActionException.class, UnauthorizedException::new)
            .onErrorMap(ForbiddenActionException.class, ForbiddenException::new)
            .onErrorMap(ResourceNotFoundException.class, NotFoundException::new)
            .onErrorMap(ForbiddenStateException.class, ConflictException::new)
            .onErrorMap(e -> !(
                e instanceof BadRequestException ||
                    e instanceof UnauthorizedException ||
                    e instanceof ForbiddenException ||
                    e instanceof NotFoundException ||
                    e instanceof ConflictException
            ), InternalServerErrorException::new);
    }
}
