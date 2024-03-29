/*************************************************************************
 *                                                                       *
 *  EJBCA: The OpenSource Certificate Authority                          *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.ejbca.core.protocol.ws.client.gen;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.ejbca.core.model.SecConst;
import org.ejbca.core.model.ra.UserDataConstants;

/**
 * Class used to represent userdata in the WebService API.
 * Is used instead of UserDataVO because of profilenames is used instead of id's.<br>
 * Example code:<pre>
 *   UserDataVOWS user = new UserDataVOWS ();
 *   user.setUsername ("tester");
 *   user.setPassword ("foo123");
 *   user.setClearPwd (false);
 *   user.setSubjectDN ("CN=Tester,C=SE");
 *   user.setCaName ("AdminCA1");
 *   user.setEmail (null);
 *   user.setSubjectAltName (null);
 *   user.setStatus (UserDataVOWS.STATUS_NEW);
 *   user.setTokenType (UserDataVOWS.TOKEN_TYPE_USERGENERATED);
 *   user.setEndEntityProfileName ("EMPTY");
 *   user.setCertificateProfileName ("ENDUSER");
 *   
 *   List<ExtendedInformationWS> ei = new ArrayList<ExtendedInformationWS> ();
 *   ei.add(new ExtendedInformationWS (ExtendedInformation.CUSTOMDATA+ExtendedInformation.CUSTOM_REVOCATIONREASON,
 *                                     Integer.toString(RevokeStatus.REVOKATION_REASON_CERTIFICATEHOLD)));
 *   ei.add(new ExtendedInformationWS (ExtendedInformation.SUBJECTDIRATTRIBUTES, "DATEOFBIRTH=19761123"));
 *   user.setExtendedInformation(ei);
 *</pre>
 * 
 * @author Philip Vendil
 * @version $Id: UserDataVOWS.java 12704 2011-09-23 12:45:09Z primelars $
 */
public class UserDataVOWS implements Serializable{
	
	public static final java.lang.String TOKEN_TYPE_USERGENERATED = "USERGENERATED"; 
	public static final java.lang.String TOKEN_TYPE_JKS           = "JKS";
	public static final java.lang.String TOKEN_TYPE_PEM           = "PEM";
	public static final java.lang.String TOKEN_TYPE_P12           = "P12";
	
    public static final int STATUS_NEW = UserDataConstants.STATUS_NEW;        // New user
    public static final int STATUS_FAILED = UserDataConstants.STATUS_FAILED;     // Generation of user certificate failed
    public static final int STATUS_INITIALIZED = UserDataConstants.STATUS_INITIALIZED;// User has been initialized
    public static final int STATUS_INPROCESS = UserDataConstants.STATUS_INPROCESS;  // Generation of user certificate in process
    public static final int STATUS_GENERATED = UserDataConstants.STATUS_GENERATED;  // A certificate has been generated for the user
    public static final int STATUS_REVOKED = UserDataConstants.STATUS_REVOKED;  // The user has been revoked and should not have any more certificates issued
    public static final int STATUS_HISTORICAL = UserDataConstants.STATUS_HISTORICAL; // The user is old and archived
    public static final int STATUS_KEYRECOVERY  = UserDataConstants.STATUS_KEYRECOVERY; // The user is should use key recovery functions in next certificate generation.
	
    private java.lang.String username = null;
    private java.lang.String password = null;
    private boolean clearPwd = false;
    private java.lang.String subjectDN = null;
    private java.lang.String caName = null;
    private java.lang.String subjectAltName = null;
    private java.lang.String email = null;
    private int status = 0;
    private java.lang.String tokenType = null;
    private boolean sendNotification = false;
    private boolean keyRecoverable = false;
    private java.lang.String endEntityProfileName = null;
    private java.lang.String certificateProfileName = null;
    private java.lang.String hardTokenIssuerName = null;
    private java.lang.String startTime = null;
    private java.lang.String endTime = null;
    private BigInteger certificateSerialNumber;
    private List<ExtendedInformationWS> extendedInformation = null;

    /**
     * Emtpy constructor used by internally by web services
     */
    public UserDataVOWS(){}
    
	/**
	 * Constructor used when creating a new UserDataVOWS.
	 * 
	 * @param username the unique username if the user, used internally in EJBCA
	 * @param password password u sed to lock the keystore
	 * @param clearPwd true if password should be in clear
	 * @param subjectDN of 
	 * @param caName the name of the CA used in the EJBCA web gui.
	 * @param subjectAltName
	 * @param email 
	 * @param status one of the STATUS_ constants
	 * @param tokenType type of token, one of TOKEN_TYPE constants for soft tokens, for hard ones  use hardtokenprofilename
	 * @param endEntityProfileName
	 * @param certificateProfileName
	 * @param hardTokenIssuerName if no hardTokenIssuer should be used then use null.
	 */
	public UserDataVOWS(String username, String password, boolean clearPwd, String subjectDN, String caName, String subjectAltName, String email, int status, String tokenType, String endEntityProfileName, String certificateProfileName, String hardTokenIssuerName) {
		super();
		this.username = username;
		this.password = password;
		this.clearPwd = clearPwd;
		this.subjectDN = subjectDN;
		this.caName = caName;
		this.subjectAltName = subjectAltName;
		this.email = email;
		this.status = status;
		this.tokenType = tokenType;
		this.endEntityProfileName = endEntityProfileName;
		this.certificateProfileName = certificateProfileName;
		this.hardTokenIssuerName = hardTokenIssuerName;
	}

    
    /**
     * 
     * @return true if the user is keyrecoverable
     */
    public boolean isKeyRecoverable(){
    	return this.keyRecoverable;
    }
    
    /**
     * indicates if the users keys should be keyrecoverable
     * @param keyrecoverable
     */
    public void setKeyRecoverable(boolean keyrecoverable){
      this.keyRecoverable = keyrecoverable;
    }
    
    /**
     * If true notifications will be sent to the user
     */
	public boolean isSendNotification(){
    	return sendNotification;
    }
    
	/**
	 * set to true if notifications should be sent to the user.
	 */
    public void setSendNotification(boolean sendnotification){
    	this.sendNotification = sendnotification;
    }
    
    /**
	 * @return Returns the cAName.
	 */
	public String getCaName() {
		return caName;
	}


	/**
	 * @return Returns the certificateProfileName.
	 */
	public String getCertificateProfileName() {
		return certificateProfileName;
	}


	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * @return Returns the endEntityProfileName.
	 */
	public String getEndEntityProfileName() {
		return endEntityProfileName;
	}


	/**
	 * @return Returns the hardTokenIssuerName.
	 */
	public String getHardTokenIssuerName() {
		return hardTokenIssuerName;
	}


	/**
	 * Observe when sending userdata to clients outside EJBCA will the password
	 * always be null.
	 * 
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Observe sending usedata to clients outside EJBCA will always return false
	 * @return Returns the clearpwd.
	 */
	public boolean isClearPwd() {
		return clearPwd;
	}

	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}


	/**
	 * @return Returns the subjecDN.
	 */
	public String getSubjectDN() {
		return subjectDN;
	}


	/**
	 * @return Returns the subjectAltName.
	 */
	public String getSubjectAltName() {
		return subjectAltName;
	}


	/**
	 * @return Returns the tokenType. One of TOKEN_TYPE constants for soft tokens, for hard ones  use hardtokenprofilename
	 */
	public String getTokenType() {
		return tokenType;
	}


	/**
	 * @return Returns the type.
	 */
	public int getType() {
		int type = 1;
		
    	if(sendNotification) {
    		type = type | SecConst.USER_SENDNOTIFICATION;
    	} else {
    		type = type & (~SecConst.USER_SENDNOTIFICATION);
    	}
    	if(keyRecoverable) {
    		type = type | SecConst.USER_KEYRECOVERABLE;
    	} else {
    		type = type & (~SecConst.USER_KEYRECOVERABLE);
    	}
		return type;
	}


	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param name The caName to set.
	 */
	public void setCaName(String name) {
		caName = name;
	}

	/**
	 * @param certificateProfileName The certificateProfileName to set.
	 */
	public void setCertificateProfileName(String certificateProfileName) {
		this.certificateProfileName = certificateProfileName;
	}

	/**
	 * @param clearPwd The clearpwd to set.
	 */
	public void setClearPwd(boolean clearPwd) {
		this.clearPwd = clearPwd;
	}

	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @param endEntityProfileName The endEntityProfileName to set.
	 */
	public void setEndEntityProfileName(String endEntityProfileName) {
		this.endEntityProfileName = endEntityProfileName;
	}

	/**
	 * @param hardTokenIssuerName The hardTokenIssuerName to set.
	 */
	public void setHardTokenIssuerName(String hardTokenIssuerName) {
		this.hardTokenIssuerName = hardTokenIssuerName;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @param subjectAltName The subjectAltName to set.
	 */
	public void setSubjectAltName(String subjectAltName) {
		this.subjectAltName = subjectAltName;
	}

	/**
	 * @param subjectDN The subjectDN to set.
	 */
	public void setSubjectDN(String subjectDN) {
		this.subjectDN = subjectDN;
	}

	/**
	 * @param tokenType The tokenType to set.
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}



	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

    /**
     * @return the startTime
     */
    public String getStartTime() {
        return this.startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public String getEndTime() {
        return this.endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

	/**
	 * @return certificate serial number.
	 */
	public BigInteger getCertificateSerialNumber() {
		return this.certificateSerialNumber;
	}

	/**
	 * @param sn Serial number of the certificate to be generated. Only used if 'Allow certificate serial number override' in used certificate profile is enabled.
	 */
	public void setCertificateSerialNumber(BigInteger sn) {
		this.certificateSerialNumber = sn;
	}

	/**
	 * @return optional extended information list
	 */
    public List<ExtendedInformationWS> getExtendedInformation() {
		return extendedInformation;
	}

    /**
     * Generic setter for extendedInformation. Set with values from ExtendedInformation such as:
     * ExtendedInformation.CUSTOM_REVOCATIONREASON, Integer.toString(RevokeStatus.REVOKATION_REASON_CERTIFICATEHOLD)
     * 
     * Add certificate extension properties like this by adding a ExtendedInformationWS:
     * Set the name to the string representation of the OID optionally prepended with '.' and type of property (<oid>[.<type>]). <oid> is same as <oid>.value. Example 1.2.3.4 1.2.3.5.value1
     * Set the value to what should be used in the implementation class.
     * @param extendedInformation
     */
	public void setExtendedInformation(List<ExtendedInformationWS> extendedInformation) {
		this.extendedInformation = extendedInformation;
	}

}
