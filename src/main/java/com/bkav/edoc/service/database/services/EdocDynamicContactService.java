package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.EdocDynamicContactDaoImpl;
import com.bkav.edoc.service.database.entity.EdocDynamicContact;

public class EdocDynamicContactService {
    private static final EdocDynamicContactDaoImpl dynamicContactDaoImpl = new EdocDynamicContactDaoImpl();

    public EdocDynamicContact getDynamicContactByDomain(String domain) {
        dynamicContactDaoImpl.openCurrentSession();

        EdocDynamicContact dynamicContact = dynamicContactDaoImpl.findByDomain(domain);

        dynamicContactDaoImpl.closeCurrentSession();
        return dynamicContact;
    }
}
