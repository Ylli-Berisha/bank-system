package com.ylli.users_service.controllers;

import com.ylli.shared.base.BaseFallbackController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UsersFallbackController extends BaseFallbackController {

}
