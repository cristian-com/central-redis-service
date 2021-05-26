package com.cristian.redis.examples;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExampleMessage(@JsonProperty("value") String value) {
}
