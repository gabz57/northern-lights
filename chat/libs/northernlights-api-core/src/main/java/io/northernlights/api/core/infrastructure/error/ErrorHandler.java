package io.northernlights.api.core.infrastructure.error;

import io.northernlights.api.core.domain.error.ForbiddenStateException;
import io.northernlights.api.core.domain.error.ResourceNotFoundException;
import io.northernlights.api.core.domain.error.UnauthorizedActionException;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Converts business Exception into HTTP Error with attached status code
 */
public class ErrorHandler implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request)
            .onErrorMap(IllegalArgumentException.class, BadRequestException::new)
            .onErrorMap(UnauthorizedActionException.class, ForbiddenException::new)
            .onErrorMap(ResourceNotFoundException.class, NotFoundException::new)
            .onErrorMap(ForbiddenStateException.class, ConflictException::new)
            .onErrorMap(e -> !(
                e instanceof BadRequestException ||
                    e instanceof ForbiddenException ||
                    e instanceof NotFoundException ||
                    e instanceof ConflictException
            ), InternalServerErrorException::new);
    }
}
