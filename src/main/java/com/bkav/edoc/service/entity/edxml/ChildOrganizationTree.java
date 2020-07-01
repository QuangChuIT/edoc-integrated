/**
 *
 */
package com.bkav.edoc.service.entity.edxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author QuangCV
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChildOrganizationTree {
    @XmlElement(name = "Organization")
    private List<OrganizationTree> children;

    /**
     *
     */
    public ChildOrganizationTree() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param children
     */
    public ChildOrganizationTree(List<OrganizationTree> children) {
        super();
        if (children == null) {
            this.children = new ArrayList<OrganizationTree>();
        } else {
            this.children = children;
        }
    }

    /**
     * @return the children
     */
    public List<OrganizationTree> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<OrganizationTree> children) {
        this.children = children;
    }
}
