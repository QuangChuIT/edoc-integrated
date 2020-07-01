/**
 *
 */
package com.bkav.edoc.service.entity.edxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author QuangCV
 *
 */
@XmlRootElement(name = "Organization")
@XmlType(name = "Organization", propOrder = {"domain", "name", "address", "email", "telephone", "fax", "website", "children"})
@XmlAccessorType(XmlAccessType.FIELD)
public class OrganizationTree {
    /**
     * @param name
     * @param domain
     * @param address
     * @param email
     * @param telephone
     * @param fax
     * @param website
     */
    public OrganizationTree(String name, String domain, String address,
                            String email, String telephone, String fax, String website, ChildOrganizationTree children) {
        this.name = name;
        this.domain = domain;
        this.address = address;
        this.email = email;
        this.telephone = telephone;
        this.fax = fax;
        this.website = website;
        if (children != null) {
            this.children = children;
        } else {
            this.children = new ChildOrganizationTree();
        }
    }

    /**
     *
     */
    public OrganizationTree() {
        super();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the telephone
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * @param telephone the telephone to set
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * @param fax the fax to set
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return the website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @param website the website to set
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * @return the children
     */
    public ChildOrganizationTree getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(ChildOrganizationTree children) {
        this.children = children;
    }

    @XmlElement(name = "OrganName")
    private String name;

    @XmlElement(name = "OrganId")
    private String domain;

    @XmlElement(name = "OrganAdd")
    private String address;

    @XmlElement(name = "Email")
    private String email;

    @XmlElement(name = "Telephone")
    private String telephone;

    @XmlElement(name = "Fax")
    private String fax;

    @XmlElement(name = "Website")
    private String website;

    @XmlElement(name = "Children")
    private ChildOrganizationTree children;
}
