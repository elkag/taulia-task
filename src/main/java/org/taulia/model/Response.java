package org.taulia.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter @Setter
public class Response {
    @JsonProperty(value = "success")
    boolean success;
    @JsonProperty(value = "files")
    Set<String> createdFiles;
}
