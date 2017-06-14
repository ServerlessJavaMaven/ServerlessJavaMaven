
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
    "Sid",
    "Effect",
    "Principal",
    "Action",
    "Resource",
    "Condition"
})
public class Statement {

    @JsonProperty("Sid")
    private String sid;
    @JsonProperty("Effect")
    private String effect;
    @JsonProperty("Principal")
    private Principal principal;
    @JsonProperty("Action")
    private String action;
    @JsonProperty("Resource")
    private String resource;
    @JsonProperty("Condition")
    private Condition condition;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Sid")
    public String getSid() {
        return sid;
    }

    @JsonProperty("Sid")
    public void setSid(String sid) {
        this.sid = sid;
    }

    @JsonProperty("Effect")
    public String getEffect() {
        return effect;
    }

    @JsonProperty("Effect")
    public void setEffect(String effect) {
        this.effect = effect;
    }

    @JsonProperty("Principal")
    public Principal getPrincipal() {
        return principal;
    }

    @JsonProperty("Principal")
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    @JsonProperty("Action")
    public String getAction() {
        return action;
    }

    @JsonProperty("Action")
    public void setAction(String action) {
        this.action = action;
    }

    @JsonProperty("Resource")
    public String getResource() {
        return resource;
    }

    @JsonProperty("Resource")
    public void setResource(String resource) {
        this.resource = resource;
    }

    @JsonProperty("Condition")
    public Condition getCondition() {
        return condition;
    }

    @JsonProperty("Condition")
    public void setCondition(Condition condition) {
        this.condition = condition;
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
