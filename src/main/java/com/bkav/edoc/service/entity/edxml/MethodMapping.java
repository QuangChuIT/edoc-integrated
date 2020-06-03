/**
 * FirstName LastName - Feb 5, 2015
 */
package com.bkav.edoc.service.entity.edxml;

import java.io.Serializable;

/**
 * @author FirstName LastName
 *
 */
public class MethodMapping implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @return the soapAction
	 */
	public String getSoapAction() {
		return soapAction;
	}

	/**
	 * @param soapAction
	 *            the soapAction to set
	 */
	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName
	 *            the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	public MethodMapping(String soapAction, String methodName, String className) {
		this.soapAction = soapAction;
		this.methodName = methodName;
		this.className = className;
	}

	public MethodMapping() {
	}

	String soapAction;
	String methodName;
	String className;

}
