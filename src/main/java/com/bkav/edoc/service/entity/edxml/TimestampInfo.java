/**
 *
 */
package com.bkav.edoc.service.entity.edxml;

/**
 * @author FirstName LastName
 *
 */
public class TimestampInfo {

    private String timestamp;
    private String signedValue;

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the signedValue
     */
    public String getSignedValue() {
        return signedValue;
    }

    /**
     * @param signedValue the signedValue to set
     */
    public void setSignedValue(String signedValue) {
        this.signedValue = signedValue;
    }

    public TimestampInfo(String timestamp, String signedValue) {
        this.timestamp = timestamp;
        this.signedValue = signedValue;
    }

    public TimestampInfo() {
    }

}
