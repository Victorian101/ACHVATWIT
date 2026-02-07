package com.college;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Test config that turn on H2 in-memory database.
 * This mode is more convenient for fast starting.
 * Change property spring.profiles.active to "test" for run application in this mode.
 */

@Configuration
@Profile("test")
public class TestConfig {



}
