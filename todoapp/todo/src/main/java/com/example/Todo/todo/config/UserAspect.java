package com.example.Todo.todo.config;

import com.example.aspect.config.JwtUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class UserAspect {

    private JwtUtil jwt = new JwtUtil();

    @Around("execution(* *..controller.*.*(..))")
    public Object aroundGetFolderDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get parameters from the original method
        Object[] args = joinPoint.getArgs();
        Integer lastIndex = args.length - 1;
        Map<String, String> headers = (Map<String, String>)args[lastIndex];
        String token = headers.get("authorization");
        
        // Replace the original token with the extracted user ID
        Map<String, String> user = (Map<String, String>) jwt.getClaims(token).get("user");
        user.put("token", token);
        args[lastIndex] = user;
        
        // Proceed with the original method execution with the modified parameters
        Object returnValue = joinPoint.proceed(args);
        
        // Return the original return value
        return returnValue;
    }

}
