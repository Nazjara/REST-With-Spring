package com.baeldung.um.service;

import com.baeldung.um.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class AsyncService {

    @Autowired
    private IUserService userService;

    public static final long DELAY = 10000L;

    public void scheduleCreateUser(UserDto dto, DeferredResult<UserDto> deferredResult) {
        CompletableFuture.supplyAsync(() -> userService.createSlow(dto)).whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));
    }

    @Async
    public Future<UserDto> createUserAsync(UserDto dto) throws InterruptedException {
        dto.setStatus("In progress");

        final UserDto result = userService.create(dto);
        Thread.sleep(AsyncService.DELAY);

        result.setStatus("Ready");

        userService.update(result);
        return new AsyncResult<>(result);
    }
}
