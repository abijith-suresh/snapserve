package com.snapserve.common.config;

import com.snapserve.common.handler.GlobalExceptionHandler;
import com.snapserve.common.jwt.JwtUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({GlobalExceptionHandler.class, JwtUtils.class})
public class CommonAutoConfiguration {}
