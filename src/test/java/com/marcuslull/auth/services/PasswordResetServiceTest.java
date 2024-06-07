package com.marcuslull.auth.services;

import com.marcuslull.auth.models.Verification;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {
    @Mock
    private VerificationService verificationService;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private ValidationService validationService;
    @Mock
    private LogoutService logoutService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private HttpServletRequest request;

    @Test
    void displayProcessorReVerifyTest() {
        Map<String, Object> expected = Map.of("isVerify", true, "isGet", true, "message", "");

        Map<String, Object> result = passwordResetService.displayProcessor(request, null, true);

        assertEquals(expected.size(), result.size());
        assertEquals(expected.get("isVerify"), result.get("isVerify"));
        assertEquals(expected.get("isGet"), result.get("isGet"));
        assertEquals(expected.get("message"), result.get("message"));
    }

    @Test
    void displayProcessorFirstTimeThroughTest() {
        Map<String, Object> expected = Map.of("isVerify", false, "isGet", true, "message", "");

        Map<String, Object> result = passwordResetService.displayProcessor(request, null, false);

        assertEquals(expected.size(), result.size());
        assertEquals(expected.get("isVerify"), result.get("isVerify"));
        assertEquals(expected.get("isGet"), result.get("isGet"));
        assertEquals(expected.get("message"), result.get("message"));
    }

    @Test
    void displayProcessorSecondTimeThroughTest() {
        Map<String, Object> expected = Map.of("isVerify", false, "isGet", false, "code", "randomUUID");
        when(verificationService.verificationEntryGetter("randomUUID")).thenReturn(new Verification());

        Map<String, Object> result = passwordResetService.displayProcessor(request, "randomUUID", false);

        assertEquals(expected.size(), result.size());
        assertEquals(expected.get("isVerify"), result.get("isVerify"));
        assertEquals(expected.get("isGet"), result.get("isGet"));
        assertEquals(expected.get("code"), result.get("code"));
    }

    @Test
    void displayProcessorSecondTimeThroughInvalidCodeTest() {
        Map<String, Object> expected = Map.of("isVerify", false, "isGet", false, "code", "randomUUID","invalidCode", true);
        when(verificationService.verificationEntryGetter("randomUUID")).thenReturn(null);

        Map<String, Object> result = passwordResetService.displayProcessor(request, "randomUUID", false);

        assertEquals(expected.size(), result.size());
        assertEquals(expected.get("isVerify"), result.get("isVerify"));
        assertEquals(expected.get("isGet"), result.get("isGet"));
        assertEquals(expected.get("code"), result.get("code"));
        assertEquals(expected.get("invalidCode"), result.get("invalidCode"));
    }

}