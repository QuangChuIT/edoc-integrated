package com.bkav.edoc.service.database.dao;

import com.bkav.edoc.service.database.entity.EdocDynamicContact;

import java.util.List;

public interface EdocDynamicContactDao {
    public EdocDynamicContact findByDomain(String domain);
}
