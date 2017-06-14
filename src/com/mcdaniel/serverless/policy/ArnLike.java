
package com.mcdaniel.serverless.policy;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "AWS:SourceArn"
})
public class ArnLike {

    @JsonProperty("AWS:SourceArn")
    private String aWSSourceArn;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("AWS:SourceArn")
    public String getAWSSourceArn() {
        return aWSSourceArn;
    }

    @JsonProperty("AWS:SourceArn")
    public void setAWSSourceArn(String aWSSourceArn) {
        this.aWSSourceArn = aWSSourceArn;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
