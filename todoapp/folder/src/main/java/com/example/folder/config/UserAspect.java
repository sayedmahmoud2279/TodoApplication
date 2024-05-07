package com.example.folder.config;

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
        String token = (String) args[lastIndex];
        
        // Replace the original token with the extracted user ID
        Map<String, String> user = (Map<String, String>) jwt.getClaims(token).get("user");
        args[lastIndex] = user.get("id");
        
        // Proceed with the original method execution with the modified parameters
        Object returnValue = joinPoint.proceed(args);
        
        // Return the original return value
        return returnValue;
    }

}
