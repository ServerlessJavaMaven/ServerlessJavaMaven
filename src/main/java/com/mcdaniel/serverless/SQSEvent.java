package com.mcdaniel.serverless;

public class SQSEvent implements Event
{

    protected String queueArn;
    protected String queueName;
    protected int batchSize;
    protected int messageVisibilityTimeoutSeconds;
    protected String deadLetterQueueName;

    public int getMessageVisibilityTimeoutSeconds()
    {
        return messageVisibilityTimeoutSeconds;
    }

    public void setMessageVisibilityTimeoutSeconds(int messageVisibilityTimeoutSeconds)
    {
        this.messageVisibilityTimeoutSeconds = messageVisibilityTimeoutSeconds;
    }

    public String getDeadLetterQueueName()
    {
        return deadLetterQueueName;
    }

    public void setDeadLetterQueueName(String deadLetterQueueName)
    {
        this.deadLetterQueueName = deadLetterQueueName;
    }

    public String getQueueArn()
    {
        return queueArn;
    }

    public void setQueueArn(String queueArn)
    {
        this.queueArn = queueArn;
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }


}
