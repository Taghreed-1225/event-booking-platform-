package com.eventbooking.eventservice.security;

import com.eventbooking.eventservice.domain.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private final JwtService jwtService =
            new JwtService("demo-shared-secret-key-for-event-booking-platform-32bytes-min", 60);

    @Test
    void generateToken_thenParse_roundTrips() {
        String token = jwtService.generateToken("Taghreed", Role.ADMIN);

        AuthenticatedUser user = jwtService.parse(token);

        assertThat(user.username()).isEqualTo("Taghreed");
        assertThat(user.role()).isEqualTo("ADMIN");
    }

    @Test
    void parse_throwsOnGarbageToken() {
        assertThatThrownBy(() -> jwtService.parse("not-a-real-token"))
                .isInstanceOf(InvalidTokenException.class);
    }
}
