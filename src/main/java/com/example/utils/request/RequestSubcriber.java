package com.example.utils.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "requestor", "target" })
public class RequestSubcriber {
    @JsonProperty("requestor")
//    @Email
    @NotEmpty
    private String requestor;

    @JsonProperty("target")
//    @Email
    @NotEmpty
    private String target;
}
