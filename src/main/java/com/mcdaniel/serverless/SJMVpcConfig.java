package com.mcdaniel.serverless;

import java.util.List;

public class SJMVpcConfig {
    protected List<String> subnetNames;
    protected List<String> securityGroupNames;

    @Override
    public String toString()
    {
        return "SJMVpcConfig{" +
                "subnetNames=" + subnetNames +
                ", securityGroupNames=" + securityGroupNames +
                '}';
    }
}
