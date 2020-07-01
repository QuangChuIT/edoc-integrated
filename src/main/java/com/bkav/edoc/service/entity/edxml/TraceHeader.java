package com.bkav.edoc.service.entity.edxml;

import java.util.Date;

public class TraceHeader {

    private String organId;

    private Date timeStamp;


    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public TraceHeader() {

    }

    /**
     * @return the timeStamp
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }


}
