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
 
package org.ejbca.core.model.ra.raadmin;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.ejbca.core.model.InternalResources;
import org.ejbca.core.model.SecConst;
import org.ejbca.core.model.UpgradeableDataHashMap;
import org.ejbca.core.model.ca.crl.RevokedCertInfo;
import org.ejbca.core.model.ra.ExtendedInformation;
import org.ejbca.core.model.ra.UserDataVO;
import org.ejbca.util.Base64;
import org.ejbca.util.StringTools;
import org.ejbca.util.ValidityDate;
import org.ejbca.util.dn.DNFieldExtractor;
import org.ejbca.util.dn.DnComponents;
import org.ejbca.util.passgen.PasswordGeneratorFactory;

/**
 * The model representation of an end entity profile.
 * 
 * The algorithm for constants in the EndEntityProfile is:
 * Values are stored as 100*parameternumber+parameter, so the first COMMONNAME value is 105, the second 205 etc.
 * Use flags are stored as 10000+100*parameternumber+parameter, so the first USE_COMMONNAME value is 10105, the second 10205 etc.
 * Required flags are stored as 20000+100*parameternumber+parameter, so the first REQUIRED_COMMONNAME value is 20105, the second 20205 etc.
 * Modifyable flags are stored as 30000+100*parameternumber+parameter, so the first MODIFYABLE_COMMONNAME value is 30105, the second 30205 etc.
 *
 * @author  Philip Vendil
 * @version $Id: EndEntityProfile.java 15968 2012-12-04 08:27:56Z anatom $
 */
public class EndEntityProfile extends UpgradeableDataHashMap implements Serializable, Cloneable {

    private static final Logger log = Logger.getLogger(EndEntityProfile.class);
    /** Internal localization of logs and errors */
    private static final InternalResources intres = InternalResources.getInstance();

    public static final float LATEST_VERSION = 14;

    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version
     * of this class is not compatible with old versions. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     */
    private static final long serialVersionUID = -8356152324295231463L;
    
    /** Constant values for end entity profile. */
    private static final HashMap<String, Integer> dataConstants = new HashMap<String, Integer>();

    // Field constants, used in the map below
    public static final String USERNAME           = "USERNAME";
    public static final String PASSWORD           = "PASSWORD";
    public static final String CLEARTEXTPASSWORD  = "CLEARTEXTPASSWORD";
    public static final String AUTOGENPASSWORDTYPE   = "AUTOGENPASSWORDTYPE";
    public static final String AUTOGENPASSWORDLENGTH = "AUTOGENPASSWORDLENGTH";
    
    public static final String EMAIL              = "EMAIL";
    public static final String KEYRECOVERABLE     = "KEYRECOVERABLE";
    public static final String DEFAULTCERTPROFILE = "DEFAULTCERTPROFILE";
    /** A list of available certificate profile names can be retrieved with getAvailableCertificateProfileNames() */
    public static final String AVAILCERTPROFILES  = "AVAILCERTPROFILES";
    public static final String DEFKEYSTORE        = "DEFKEYSTORE";
    public static final String AVAILKEYSTORE      = "AVAILKEYSTORE";
    public static final String DEFAULTTOKENISSUER = "DEFAULTTOKENISSUER";
    public static final String AVAILTOKENISSUER   = "AVAILTOKENISSUER";
    public static final String SENDNOTIFICATION   = "SENDNOTIFICATION";
    public static final String CARDNUMBER         = "CARDNUMBER";
    public static final String DEFAULTCA          = "DEFAULTCA";
    public static final String AVAILCAS           = "AVAILCAS";
    public static final String STARTTIME          = ExtendedInformation.CUSTOM_STARTTIME;	//"STARTTIME"
    public static final String ENDTIME            = ExtendedInformation.CUSTOM_ENDTIME;	//"ENDTIME"
    public static final String CERTSERIALNR       = "CERTSERIALNR";
    /** A maximum value of the (optional) counter specifying how many certificate requests can be processed
     * before user is finalized (status set to GENERATED). Counter is only used when finishUser is
     * enabled in the CA (by default it is)
     */
    public static final String ALLOWEDREQUESTS    = "ALLOWEDREQUESTS";
    /** A revocation reason that will be applied immediately to certificates issued to a user. With this we can issue
     * a certificate that is "on hold" directly when the user gets the certificate.
     */
    public static final String ISSUANCEREVOCATIONREASON = "ISSUANCEREVOCATIONREASON";
    
    public static final String MAXFAILEDLOGINS	 = "MAXFAILEDLOGINS";

    /** Minimum password strength in bits */
    public static final String MINPWDSTRENGTH    = "MINPWDSTRENGTH";

    // Default values
    // These must be in a strict order that can never change 
    // Custom values configurable in a properties file (profilemappings.properties)
    static {
    	dataConstants.put(USERNAME, Integer.valueOf(0));
    	dataConstants.put(PASSWORD, Integer.valueOf(1));
    	dataConstants.put(CLEARTEXTPASSWORD, Integer.valueOf(2));
    	dataConstants.put(AUTOGENPASSWORDTYPE, Integer.valueOf(95));
    	dataConstants.put(AUTOGENPASSWORDLENGTH, Integer.valueOf(96));
        // DN components
    
    	dataConstants.put(EMAIL, Integer.valueOf(26));
    	dataConstants.put(KEYRECOVERABLE, Integer.valueOf(28));
    	dataConstants.put(DEFAULTCERTPROFILE, Integer.valueOf(29));
    	dataConstants.put(AVAILCERTPROFILES, Integer.valueOf(30));
    	dataConstants.put(DEFKEYSTORE, Integer.valueOf(31));
    	dataConstants.put(AVAILKEYSTORE, Integer.valueOf(32));
    	dataConstants.put(DEFAULTTOKENISSUER, Integer.valueOf(33));
    	dataConstants.put(AVAILTOKENISSUER, Integer.valueOf(34));
    	dataConstants.put(SENDNOTIFICATION, Integer.valueOf(35));

    	dataConstants.put(DEFAULTCA, Integer.valueOf(37));
    	dataConstants.put(AVAILCAS, Integer.valueOf(38));
    	
    	// Load all DN, altName and directoryAttributes from DnComponents.
    	dataConstants.putAll(DnComponents.getProfilenameIdMap());
    	
    	dataConstants.put(ISSUANCEREVOCATIONREASON, Integer.valueOf(94));
    	dataConstants.put(ALLOWEDREQUESTS, Integer.valueOf(97));
    	dataConstants.put(STARTTIME, Integer.valueOf(98));
    	dataConstants.put(ENDTIME, Integer.valueOf(99));
    	dataConstants.put(CARDNUMBER, Integer.valueOf(91));
    	dataConstants.put(MAXFAILEDLOGINS, Integer.valueOf(93));
    	dataConstants.put(CERTSERIALNR, Integer.valueOf(92));
    	dataConstants.put(MINPWDSTRENGTH, Integer.valueOf(90));
    }
	// The max value in dataConstants (we only want to do this once)
    private static final int dataConstantsMaxValue = Collections.max(dataConstants.values()).intValue();
    // The keys used when we create an empty profile (we only want to do this once)
    private static final List<String> dataConstantsUsedInEmpty = new LinkedList<String>(dataConstants.keySet());
    static {
    	dataConstantsUsedInEmpty.remove(SENDNOTIFICATION);
    	dataConstantsUsedInEmpty.remove(DnComponents.OTHERNAME);
    	dataConstantsUsedInEmpty.remove(DnComponents.X400ADDRESS);
    	dataConstantsUsedInEmpty.remove(DnComponents.EDIPARTNAME);
    	dataConstantsUsedInEmpty.remove(DnComponents.REGISTEREDID);
    }

    // Type of data constants.
    private static final int VALUE      = 0;
    private static final int USE        = 1;
    private static final int ISREQUIRED = 2;
    private static final int MODIFYABLE = 3;

    // Private Constants.
    private static final int FIELDBOUNDRARY  = 10000;
    private static final int NUMBERBOUNDRARY = 100;
    
    // Pre-calculated constants
    private static final int FIELDBOUNDRARY_VALUE  = FIELDBOUNDRARY * VALUE;
    private static final int FIELDBOUNDRARY_USE  = FIELDBOUNDRARY * USE;
    private static final int FIELDBOUNDRARY_ISREQUIRED  = FIELDBOUNDRARY * ISREQUIRED;
    private static final int FIELDBOUNDRARY_MODIFYABLE  = FIELDBOUNDRARY * MODIFYABLE;

    public static final String SPLITCHAR       = ";";

    public static final String TRUE  = "true";
    public static final String FALSE = "false";

    // Constants used with field ordering
    public static final int FIELDTYPE = 0;
    public static final int NUMBER    = 1;


    /** Number array keeps track of how many fields there are of a specific type, for example 2 OranizationUnits, 0 TelephoneNumber */
    private static final String NUMBERARRAY               = "NUMBERARRAY";
    private static final String SUBJECTDNFIELDORDER       = "SUBJECTDNFIELDORDER";
    private static final String SUBJECTALTNAMEFIELDORDER  = "SUBJECTALTNAMEFIELDORDER";
    private static final String SUBJECTDIRATTRFIELDORDER  = "SUBJECTDIRATTRFIELDORDER";
    
    private static final String USERNOTIFICATIONS         = "USERNOTIFICATIONS";

    private static final String REUSECERTIFICATE = "REUSECERTIFICATE";
    private static final String REVERSEFFIELDCHECKS = "REVERSEFFIELDCHECKS"; 
    private static final String ALLOW_MERGEDN_WEBSERVICES = "ALLOW_MERGEDN_WEBSERVICES";
    
    private static final String PRINTINGUSE            = "PRINTINGUSE";
    private static final String PRINTINGDEFAULT        = "PRINTINGDEFAULT";
    private static final String PRINTINGREQUIRED       = "PRINTINGREQUIRED";
    private static final String PRINTINGCOPIES         = "PRINTINGCOPIES";
    private static final String PRINTINGPRINTERNAME    = "PRINTINGPRINTERNAME";
    private static final String PRINTINGSVGFILENAME    = "PRINTINGSVGFILENAME";
    private static final String PRINTINGSVGDATA        = "PRINTINGSVGDATA";

    /** 
     * If it should be possible to add/edit certificate extension data
     * when adding/editing an end entity using the admin web or not.
     */
    private static final String USEEXTENSIONDATA       = "USEEXTENSIONDATA";

    // String constants that never change, so we can do the String concat/conversion once
    private static final String CONST_DEFAULTCERTPROFILE = Integer.toString(SecConst.CERTPROFILE_FIXED_ENDUSER);
    private static final String CONST_AVAILCERTPROFILES1 = SecConst.CERTPROFILE_FIXED_ENDUSER + ";" + SecConst.CERTPROFILE_FIXED_OCSPSIGNER + ";" + SecConst.CERTPROFILE_FIXED_SERVER;
    private static final String CONST_DEFKEYSTORE = Integer.toString(SecConst.TOKEN_SOFT_BROWSERGEN);
    private static final String CONST_AVAILKEYSTORE = SecConst.TOKEN_SOFT_BROWSERGEN + ";" + SecConst.TOKEN_SOFT_P12 +  ";" + SecConst.TOKEN_SOFT_JKS + ";" + SecConst.TOKEN_SOFT_PEM;
    private static final String CONST_AVAILCAS = Integer.toString(SecConst.ALLCAS);
    private static final String CONST_ISSUANCEREVOCATIONREASON = Integer.toString(RevokedCertInfo.NOT_REVOKED);
    private static final String CONST_AVAILCERTPROFILES2 = SecConst.CERTPROFILE_FIXED_ENDUSER + ";" + SecConst.CERTPROFILE_FIXED_SUBCA + ";" + SecConst.CERTPROFILE_FIXED_ROOTCA;
    
    /** Creates a new instance of EndEntity Profile with the default fields set. */
    public EndEntityProfile() {
    	super();
    	init(false);
    }

    /** Creates a default empty end entity profile with all standard fields added to it. */
    public EndEntityProfile(final boolean emptyprofile){
    	super();
    	init(emptyprofile);
    }

    /** Creates a new instance of EndEntity Profile used during cloning or when we load all the data from the database. */
    public EndEntityProfile(final int unused) {
    }

    private void init(final boolean emptyprofile){
        if (log.isDebugEnabled()) {
        	log.debug("The highest number in dataConstants is: " + dataConstantsMaxValue);
        }
        // Common initialization of profile
        final List<Integer> numberoffields = new ArrayList<Integer>(dataConstantsMaxValue);
        Collections.fill(numberoffields, Integer.valueOf(0));
        data.put(NUMBERARRAY, numberoffields);
        data.put(SUBJECTDNFIELDORDER, new ArrayList<Integer>());
        data.put(SUBJECTALTNAMEFIELDORDER, new ArrayList<Integer>());
        data.put(SUBJECTDIRATTRFIELDORDER, new ArrayList<Integer>());
        
        if (emptyprofile) {
        	for (final String key : dataConstantsUsedInEmpty) {
        		addFieldWithDefaults(key, "", Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
        	}
        	// Add another DC-field since (if used) more than one is always used
    		addFieldWithDefaults(DnComponents.DOMAINCOMPONENT, "", Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
        	// Set required fields
        	setRequired(USERNAME,0,true);
        	setRequired(PASSWORD,0,true);
        	setRequired(DnComponents.COMMONNAME,0,true);
        	setRequired(DEFAULTCERTPROFILE,0,true);
        	setRequired(AVAILCERTPROFILES,0,true);
        	setRequired(DEFKEYSTORE,0,true);
        	setRequired(AVAILKEYSTORE,0,true);
        	setRequired(DEFAULTCA,0,true);
        	setRequired(AVAILCAS,0,true);
        	setRequired(ISSUANCEREVOCATIONREASON,0,false);
        	setRequired(STARTTIME,0,false);
        	setRequired(ENDTIME,0,false);
        	setRequired(ALLOWEDREQUESTS,0,false);
        	setRequired(CARDNUMBER,0,false);
        	setRequired(MAXFAILEDLOGINS,0,false);
        	setValue(DEFAULTCERTPROFILE,0, CONST_DEFAULTCERTPROFILE);
        	setValue(AVAILCERTPROFILES,0, CONST_AVAILCERTPROFILES1);
        	setValue(DEFKEYSTORE,0, CONST_DEFKEYSTORE);
        	setValue(AVAILKEYSTORE,0, CONST_AVAILKEYSTORE);
        	setValue(AVAILCAS,0, CONST_AVAILCAS);
        	setValue(ISSUANCEREVOCATIONREASON, 0, CONST_ISSUANCEREVOCATIONREASON);
        	// Do not use hard token issuers by default.
        	setUse(AVAILTOKENISSUER, 0, false);
        	setUse(STARTTIME,0,false);
        	setUse(ENDTIME,0,false);
        	setUse(ALLOWEDREQUESTS,0,false);
        	setUse(CARDNUMBER,0,false);
        	setUse(ISSUANCEREVOCATIONREASON,0,false);
        	setUse(MAXFAILEDLOGINS,0,false);
        	setUse(MINPWDSTRENGTH,0,false);
        } else {
        	// initialize profile data
        	addFieldWithDefaults(USERNAME, "", Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        	addFieldWithDefaults(PASSWORD, "", Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        	addField(AUTOGENPASSWORDTYPE);
        	addFieldWithDefaults(AUTOGENPASSWORDLENGTH, "8", Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
        	addFieldWithDefaults(DnComponents.COMMONNAME, "", Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        	addField(EMAIL);
        	addFieldWithDefaults(DEFAULTCERTPROFILE, CONST_DEFAULTCERTPROFILE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        	addFieldWithDefaults(AVAILCERTPROFILES, CONST_AVAILCERTPROFILES2, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        	addFieldWithDefaults(DEFKEYSTORE, CONST_DEFKEYSTORE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        	addFieldWithDefaults(AVAILKEYSTORE, CONST_AVAILKEYSTORE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        	addField(DEFAULTTOKENISSUER);
        	// Do not use hard token issuers by default.
        	addFieldWithDefaults(AVAILTOKENISSUER, "", Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
        	addFieldWithDefaults(AVAILCAS, "", Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        	addFieldWithDefaults(DEFAULTCA, "", Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        	addFieldWithDefaults(STARTTIME, "", Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        	addFieldWithDefaults(ENDTIME, "", Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        	addFieldWithDefaults(ALLOWEDREQUESTS, "", Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        	addFieldWithDefaults(CARDNUMBER, "", Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        	addFieldWithDefaults(ISSUANCEREVOCATIONREASON, CONST_ISSUANCEREVOCATIONREASON, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        	addFieldWithDefaults(MAXFAILEDLOGINS, "", Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        }
    }

    /** Add a field with value="", required=false, use=true, modifyable=true */
    public void addField(final String parameter){
    	addField(getParameterNumber(parameter), parameter);
    }
    
    /**
     * Function that adds a field to the profile.
     *
     * @param paramter is the field and one of the field constants.
     */
    public void addField(final int parameter){
    	addField(parameter, getParameter(parameter));
    }
    
    /** Add a field with value="", required=false, use=true, modifyable=true */
    private void addField(final int parameter, final String parameterName) {
    	addFieldWithDefaults(parameter, parameterName, "", Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
    }

    private void addFieldWithDefaults(final String parameterName, final String value, final Boolean required, final Boolean use, final Boolean modifyable) {
    	addFieldWithDefaults(getParameterNumber(parameterName), parameterName, value, required, use, modifyable);
    }

    private void addFieldWithDefaults(final int parameter, final String parameterName, final String value, final Boolean required, final Boolean use, final Boolean modifyable) {
    	final int size = getNumberOfField(parameter);
    	// Perform operations directly on "data" to save some cycles..
    	final int offset = (NUMBERBOUNDRARY*size) + parameter;
   		data.put(Integer.valueOf(FIELDBOUNDRARY_VALUE + offset), value);
    	data.put(Integer.valueOf(FIELDBOUNDRARY_ISREQUIRED + offset), required);
    	data.put(Integer.valueOf(FIELDBOUNDRARY_USE + offset), use);
    	data.put(Integer.valueOf(FIELDBOUNDRARY_MODIFYABLE + offset), modifyable);
    	if (DnComponents.isDnProfileField(parameterName)) {
    		final ArrayList<Integer> fieldorder = (ArrayList<Integer>) data.get(SUBJECTDNFIELDORDER);
    		final Integer val = Integer.valueOf((NUMBERBOUNDRARY*parameter) + size);
    		fieldorder.add(val);
    	} else if (DnComponents.isAltNameField(parameterName)) {
    		final ArrayList<Integer> fieldorder = (ArrayList<Integer>) data.get(SUBJECTALTNAMEFIELDORDER);
    		final Integer val = Integer.valueOf((NUMBERBOUNDRARY*parameter) + size);
    		fieldorder.add(val);
    	} else if (DnComponents.isDirAttrField(parameterName)) {
    		final ArrayList<Integer> fieldorder = (ArrayList<Integer>) data.get(SUBJECTDIRATTRFIELDORDER);
    		final Integer val = Integer.valueOf((NUMBERBOUNDRARY*parameter) + size);
    		fieldorder.add(val);
    	}
    	incrementFieldnumber(parameter);
    }

    public void removeField(final String parameter, final int number){
    	removeField(getParameterNumber(parameter), number);
    }

    /**
     * Function that removes a field from the end entity profile.
     *
     * @param parameter is the field to remove.
     * @param number is the number of field.
     */
    public void removeField(final int parameter, final int number){
    	// Remove field and move all file ids above.
    	final int size = getNumberOfField(parameter);
    	if (size>0) {
    		for (int n = number; n < size-1; n++) {
    			setValue(parameter,n,getValue(parameter,n+1));
    			setRequired(parameter,n,isRequired(parameter,n+1));
    			setUse(parameter,n,getUse(parameter,n+1));
    			setModifyable(parameter,n,isModifyable(parameter,n+1));
    		}
    		final String param = getParameter(parameter);
    		// Remove last element from Subject DN order list.
    		if (DnComponents.isDnProfileField(param)) {
    			final ArrayList<Integer> fieldorder = (ArrayList<Integer>) data.get(SUBJECTDNFIELDORDER);
    			final int value = (NUMBERBOUNDRARY*parameter) + size -1;
    			fieldorder.remove(Integer.valueOf(value));
    		}
    		// Remove last element from Subject AltName order list.
    		if (DnComponents.isAltNameField(param)) {
    			final ArrayList<Integer> fieldorder = (ArrayList<Integer>) data.get(SUBJECTALTNAMEFIELDORDER);
    			final int value = (NUMBERBOUNDRARY*parameter) + size -1;	//number;
    			fieldorder.remove(Integer.valueOf(value));
    		}
    		// Remove last element from Subject DirAttr order list.
    		if (DnComponents.isDirAttrField(param)) {
    			final ArrayList<Integer> fieldorder = (ArrayList<Integer>) data.get(SUBJECTDIRATTRFIELDORDER);
    			final int value = (NUMBERBOUNDRARY*parameter) + size -1;	//number;
    			fieldorder.remove(Integer.valueOf(value));
    		}
    		// Remove last element of the type from hashmap
    		data.remove(Integer.valueOf(FIELDBOUNDRARY_VALUE + (NUMBERBOUNDRARY*(size-1)) + parameter));
    		data.remove(Integer.valueOf(FIELDBOUNDRARY_USE + (NUMBERBOUNDRARY*(size-1)) + parameter));
    		data.remove(Integer.valueOf(FIELDBOUNDRARY_ISREQUIRED + (NUMBERBOUNDRARY*(size-1)) + parameter));
    		data.remove(Integer.valueOf(FIELDBOUNDRARY_MODIFYABLE + (NUMBERBOUNDRARY*(size-1)) + parameter));
    		decrementFieldnumber(parameter);
    	}
    }

    /**
     * Function that returns the number of one kind of field in the profile.
     *
     */
    protected int getNumberOfField(final String parameter){
    	return getNumberOfField(getParameterNumber(parameter));
    }
    private int getNumberOfField(final int parameter){
    	final ArrayList<Integer> arr = checkAndUpgradeWithNewFields(parameter);
    	return arr.get(parameter).intValue();
    }

	private ArrayList<Integer> checkAndUpgradeWithNewFields(final int parameter) {
		final ArrayList<Integer> arr = (ArrayList<Integer>)data.get(NUMBERARRAY);
    	// This is an automatic upgrade function, if we have dynamically added new fields
    	if (parameter >= arr.size()) {
    		if (log.isDebugEnabled()) {
        		log.debug(intres.getLocalizedMessage("ra.eeprofileaddfield", Integer.valueOf(parameter)));
    		}
    		for (int i=arr.size(); i<=parameter; i++) {
                arr.add(Integer.valueOf(0));
    		}
            data.put(NUMBERARRAY,arr);
    	}
		return arr;
	}

    public void setValue(final int parameter, final int number, final String value) {
    	if (value !=null) {
    		data.put(Integer.valueOf(FIELDBOUNDRARY_VALUE + (NUMBERBOUNDRARY*number) + parameter), value.trim());
    	} else {
    		data.put(Integer.valueOf(FIELDBOUNDRARY_VALUE + (NUMBERBOUNDRARY*number) + parameter), "");
    	}
    }

    public void setValue(final String parameter, final int number, final String value) {
    	setValue(getParameterNumber(parameter), number, value);
    }

    public void setUse(final int parameter, final int number, final boolean use){
    	data.put(Integer.valueOf(FIELDBOUNDRARY_USE + (NUMBERBOUNDRARY*number) + parameter), Boolean.valueOf(use));
    }

    public void setUse(final String parameter, final int number, final boolean use){
    	setUse(getParameterNumber(parameter), number, use);
    }

    public void setRequired(final int parameter, final int number, final boolean isrequired) {
    	data.put(Integer.valueOf(FIELDBOUNDRARY_ISREQUIRED + (NUMBERBOUNDRARY*number) + parameter), Boolean.valueOf(isrequired));
    }

    public void setRequired(final String parameter, final int number, final boolean isrequired) {
    	setRequired(getParameterNumber(parameter), number, isrequired);
    }

    public void setModifyable(final int parameter, final int number, final boolean changeable) {
    	data.put(Integer.valueOf(FIELDBOUNDRARY_MODIFYABLE + (NUMBERBOUNDRARY*number) + parameter), Boolean.valueOf(changeable));
    }

    public void setModifyable(final String parameter, final int number, final boolean changeable) {
    	setModifyable(getParameterNumber(parameter), number, changeable);
    }

    public String getValue(final int parameter, final int number) {
    	return getValueDefaultEmpty(Integer.valueOf(FIELDBOUNDRARY_VALUE + (NUMBERBOUNDRARY*number) + parameter));
    }

    public String getValue(final String parameter, final int number) {
    	return getValue(getParameterNumber(parameter), number);
    }

    public boolean getUse(final int parameter, final int number){
    	return getValueDefaultFalse(Integer.valueOf(FIELDBOUNDRARY_USE + (NUMBERBOUNDRARY*number) + parameter));
    }

    public boolean getUse(final String parameter, final int number){
    	return getUse(getParameterNumber(parameter), number);
    }

    public boolean isRequired(final int parameter, final int number) {
    	return getValueDefaultFalse(Integer.valueOf(FIELDBOUNDRARY_ISREQUIRED + (NUMBERBOUNDRARY*number) + parameter));
    }

    public boolean isRequired(final String parameter, final int number) {
    	return isRequired(getParameterNumber(parameter), number);
    }

    public boolean isModifyable(final int parameter, final int number) {
    	return getValueDefaultFalse(Integer.valueOf(FIELDBOUNDRARY_MODIFYABLE + (NUMBERBOUNDRARY*number) + parameter));
    }

    public boolean isModifyable(final String parameter, final int number) {
    	return isModifyable(getParameterNumber(parameter), number);
    }

    public int getSubjectDNFieldOrderLength(){
    	return ((ArrayList<Integer>) data.get(SUBJECTDNFIELDORDER)).size();
    }

    public int getSubjectAltNameFieldOrderLength(){
    	return ((ArrayList<Integer>) data.get(SUBJECTALTNAMEFIELDORDER)).size();
    }

    public int getSubjectDirAttrFieldOrderLength(){
        return ((ArrayList<Integer>) data.get(SUBJECTDIRATTRFIELDORDER)).size();
    }

    /** returns two int : the first is the DN field which is a constant in DN field extractor,
     * the second is in which order the attribute is, 0 is first OU and 1 can mean second OU (if OU is specified in the first value).
     * 
     */
    public int[] getSubjectDNFieldsInOrder(final int index) {
    	final int[] returnval = new int[2];
    	final ArrayList<Integer> fieldorder = (ArrayList<Integer>) data.get(SUBJECTDNFIELDORDER);
    	final int i = fieldorder.get(index).intValue();
    	returnval[NUMBER] = i % NUMBERBOUNDRARY;
    	returnval[FIELDTYPE] = i / NUMBERBOUNDRARY;
    	return returnval;
    }

    public int[] getSubjectAltNameFieldsInOrder(final int index) {
    	int[] returnval = new int[2];
    	final ArrayList<Integer> fieldorder = (ArrayList<Integer>) data.get(SUBJECTALTNAMEFIELDORDER);
    	final int i = fieldorder.get(index).intValue();
    	returnval[NUMBER] = i % NUMBERBOUNDRARY;
    	returnval[FIELDTYPE] = i / NUMBERBOUNDRARY;
    	return returnval;
    }

    public int[] getSubjectDirAttrFieldsInOrder(final int index) {
    	final int[] returnval = new int[2];
    	final ArrayList<Integer> fieldorder = (ArrayList<Integer>) data.get(SUBJECTDIRATTRFIELDORDER);
    	final int i = fieldorder.get(index).intValue();
    	returnval[NUMBER] = i % NUMBERBOUNDRARY;
    	returnval[FIELDTYPE] = i / NUMBERBOUNDRARY;
    	return returnval;
    }

    /** Gets a Collection of available CA Ids (as Strings). 
     * Use String.valueOf(caidstring) to get the int value of the CA id.
     * 
     * @return a Collection of String, where the string is an integer (never null).
     */
    public Collection<String> getAvailableCAs(){
        final ArrayList<String> availablecaids = new ArrayList<String>();
        availablecaids.addAll(Arrays.asList(getValue(AVAILCAS,0).split(SPLITCHAR)));
        return availablecaids;
    }
    
    /** Gets a Collection of available certificate profile ids
     * Use String.valueOf(caidstring) to get the int value
     * 
     * @return a Collection of String, where the string is an integer.
     */
    public Collection<String> getAvailableCertificateProfileIds() {
        final ArrayList<String> profiles = new ArrayList<String>();
        final String list = getValue(AVAILCERTPROFILES,0);
        if (list != null) {
            profiles.addAll(Arrays.asList(list.split(SPLITCHAR)));        	
        }
        return profiles;    	
    }

    public int getDefaultCA() {
    	int ret = -1;
    	final String str = getValue(DEFAULTCA,0);
    	if (str != null && !StringUtils.isEmpty(str)) {
    		ret = Integer.valueOf(str);
    	}
        return ret;
    }
    
    public boolean useAutoGeneratedPasswd(){    	
    	return !this.getUse(EndEntityProfile.PASSWORD,0);
    }
    
    private String getAutoGeneratedPasswdType() {
    	String type = getValue(AUTOGENPASSWORDTYPE, 0);
    	if (type == null || "".equals(type)) {
    		type = PasswordGeneratorFactory.PASSWORDTYPE_LETTERSANDDIGITS;
    	}
    	return type;
    }

    private int getAutoGeneratedPasswdLength() {
    	final String length = getValue(AUTOGENPASSWORDLENGTH, 0);
    	int pwdlen = 8;
    	if (length != null) {
    		try {
    			pwdlen = Integer.parseInt(length);    		
    		} catch (NumberFormatException e) {
    			log.info("NumberFormatException parsing AUTOGENPASSWORDLENGTH, using default value of 8: ", e);
    		}
    	}
    	return pwdlen;
    }

    public String getAutoGeneratedPasswd(){
    	final int pwdlen = getAutoGeneratedPasswdLength();
    	return PasswordGeneratorFactory.getInstance(getAutoGeneratedPasswdType()).getNewPassword(pwdlen, pwdlen);    	
    }
    
    /** @return strength in bits = log2(possible chars) * number of chars rounded down */
    public int getAutoGenPwdStrength() {
    	final int numerOfDifferentChars = PasswordGeneratorFactory.getInstance(getAutoGeneratedPasswdType()).getNumerOfDifferentChars();
    	return getPasswordStrength(numerOfDifferentChars, getAutoGeneratedPasswdLength());
    }
    
    /** @return strength in bits = log2(possible chars) * number of chars rounded down */
    private int getPasswordStrength(int numerOfDifferentChars, int passwordLength) {
    	return Double.valueOf(Math.floor(Math.log((double)numerOfDifferentChars)/Math.log(2)) * (double)passwordLength).intValue();
    }

    /** @return the minimum strength that a password is allowed to have in bits */
    public int getMinPwdStrength() {
    	if (!getUse(MINPWDSTRENGTH, 0)) {
    		return 0;
    	}
    	return Integer.parseInt(getValue(MINPWDSTRENGTH, 0));
    }
    
    /** Set the minimum strength that a password is allowed to have in bits */
    public void setMinPwdStrength(int minPwdStrength) {
    	this.setUse(MINPWDSTRENGTH, 0, true);
    	this.setValue(MINPWDSTRENGTH, 0, Integer.valueOf(minPwdStrength).toString());
    }
    
    /**
     * @return String value with types from org.ejbca.util.passgen, example org.ejbca.util.passgen.DigitPasswordGenerator.NAME (PWGEN_DIGIT)
     */
    public static Collection<String> getAvailablePasswordTypes() {
        return PasswordGeneratorFactory.getAvailablePasswordTypes();
    }

    public List<UserNotification> getUserNotifications() {
    	List<UserNotification> l = (List<UserNotification>)data.get(USERNOTIFICATIONS);
    	if (l == null) {
    		l = new ArrayList<UserNotification>();
    	}
    	return l;
    }

    public void addUserNotification(final UserNotification notification) {
    	if (data.get(USERNOTIFICATIONS) == null) {
    		setUserNotifications(new ArrayList<UserNotification>(0));
    	}
    	((List<UserNotification>) data.get(USERNOTIFICATIONS)).add(notification);
    }

    public void setUserNotifications(final List<UserNotification> notifications) {
    	if (notifications == null) {
    		data.put(USERNOTIFICATIONS, new ArrayList<UserNotification>(0));
    	} else {
    		data.put(USERNOTIFICATIONS, notifications);
    	}
    }

    public void removeUserNotification(final UserNotification notification) {
    	if (data.get(USERNOTIFICATIONS) != null) {
    		((List<UserNotification>) data.get(USERNOTIFICATIONS)).remove(notification);
    	}
    }

    /** @return true if the key-recovered certificate should be reused. */
    public boolean getReUseKeyRecoveredCertificate(){
    	return getValueDefaultFalse(REUSECERTIFICATE);
    }
    
    public void setReUseKeyRecoveredCertificate(final boolean reuse){
    	data.put(REUSECERTIFICATE, Boolean.valueOf(reuse));
    }

    /** @return true if the profile checks should be reversed or not. Default is false. */
    public boolean getReverseFieldChecks(){
    	return getValueDefaultFalse(REVERSEFFIELDCHECKS);
    }
    
    public void setReverseFieldChecks(final boolean reverse){
    	data.put(REVERSEFFIELDCHECKS, Boolean.valueOf(reverse));
    }
    
    /** @return true if profile DN should be merged to webservices. Default is false. */
    public boolean getAllowMergeDnWebServices(){
    	return getValueDefaultFalse(ALLOW_MERGEDN_WEBSERVICES);
    }
    
    public void setAllowMergeDnWebServices(final boolean merge){
    	data.put(ALLOW_MERGEDN_WEBSERVICES, Boolean.valueOf(merge));
    }

    /** @return true if printing of userdata should be done. default is false. */
    public boolean getUsePrinting(){
    	return getValueDefaultFalse(PRINTINGUSE);
    }

    public void setUsePrinting(final boolean use){
    	data.put(PRINTINGUSE, Boolean.valueOf(use));
    }
    
    /** @return true if printing of userdata should be done. default is false. */
    public boolean getPrintingDefault(){
    	return getValueDefaultFalse(PRINTINGDEFAULT);
    }
    
    public void setPrintingDefault(final boolean printDefault){
    	data.put(PRINTINGDEFAULT, Boolean.valueOf(printDefault));
    }
    
    /** @return true if printing of userdata should be done. default is false. */
    public boolean getPrintingRequired(){
    	return getValueDefaultFalse(PRINTINGREQUIRED);
    }
    
    public void setPrintingRequired(final boolean printRequired){
    	data.put(PRINTINGREQUIRED, Boolean.valueOf(printRequired));
    }
    
    /** @return the number of copies that should be printed. Default is 1. */
    public int getPrintedCopies(){
    	if (data.get(PRINTINGCOPIES) == null) {
    		return 1;
    	}
    	return ((Integer) data.get(PRINTINGCOPIES)).intValue();
    }
    
    public void setPrintedCopies(int copies){
    	data.put(PRINTINGCOPIES, Integer.valueOf(copies));
    }
    
    /** @return the name of the printer that should be used */
    public String getPrinterName(){
    	return getValueDefaultEmpty(PRINTINGPRINTERNAME);
    }
    
    public void setPrinterName(final String printerName){
    	data.put(PRINTINGPRINTERNAME, printerName);
    }
    
    /** @return filename of the uploaded */
    public String getPrinterSVGFileName(){
    	return getValueDefaultEmpty(PRINTINGSVGFILENAME);
    }
    
    public void setPrinterSVGFileName(final String printerSVGFileName){
    	data.put(PRINTINGSVGFILENAME, printerSVGFileName);
    }
    
    /**
     * @return the data of the SVG file, if no content have
     * been uploaded null is returned
     */
    public String getPrinterSVGData(){
    	if (data.get(PRINTINGSVGDATA) == null) {
    		return null;
    	}
    	return new String(Base64.decode(((String) data.get(PRINTINGSVGDATA)).getBytes()));
    }
    
    public void setPrinterSVGData(final String sVGData){
    	data.put(PRINTINGSVGDATA, new String(Base64.encode(sVGData.getBytes())));
    }
    
    /** @return the boolean value or false if null */
    private boolean getValueDefaultFalse(final Object key) {
    	if (data.get(key) == null) {
    		return false;
    	}
    	return ((Boolean) data.get(key)).booleanValue();
    }

    /** @return the boolean value or false if null */
    private String getValueDefaultEmpty(final Object key) {
    	if (data.get(key) == null) {
    		return "";
    	}
    	return (String) data.get(key);
    }

    public void doesUserFullfillEndEntityProfile(final UserDataVO userdata, final boolean clearpwd) throws UserDoesntFullfillEndEntityProfile {
    	doesUserFullfillEndEntityProfile(userdata.getUsername(), userdata.getPassword(), userdata.getDN(), userdata.getSubjectAltName(), "", userdata.getEmail(), 
    											userdata.getCertificateProfileId(), clearpwd, userdata.getKeyRecoverable(), userdata.getSendNotification(), 
    											userdata.getTokenType(), userdata.getHardTokenIssuerId(), userdata.getCAId(), userdata.getExtendedinformation());
        //Checking if the cardnumber is required and set
        if (isRequired(CARDNUMBER,0)) {
            if (userdata.getCardNumber()==null || userdata.getCardNumber().isEmpty()) {
               throw new UserDoesntFullfillEndEntityProfile("Cardnumber is not set");
            }
         }
    }
    
    public void doesUserFullfillEndEntityProfile(final String username, final String password, final String dn, final String subjectaltname, final String subjectdirattr,
    		final String email, final int certificateprofileid, final boolean clearpwd, final boolean keyrecoverable, final boolean sendnotification, final int tokentype,
    		final int hardwaretokenissuerid, final int caid, final ExtendedInformation ei) throws UserDoesntFullfillEndEntityProfile {
    	if (useAutoGeneratedPasswd()) {
        	// Checks related to the use of auto generated passwords
    		if (password != null) {
    			throw new UserDoesntFullfillEndEntityProfile("Autogenerated password must have password. (Password was null.)");
    		}
    		if (log.isDebugEnabled()) {
    			log.debug("getAutoGenPwdStrength=" + getAutoGenPwdStrength() + " getMinPwdStrength=" + getMinPwdStrength());
    		}
    		if (getUse(MINPWDSTRENGTH, 0) && (getAutoGenPwdStrength()< getMinPwdStrength())) {
    			throw new UserDoesntFullfillEndEntityProfile("Generated password is not strong enough (" + getAutoGenPwdStrength() + " bits in generated password < " + getMinPwdStrength() + " bits required by end entity profile).");
    		}
    	} else {
        	// Checks related to the use of normal hashed passwords
    		if (!isModifyable(PASSWORD,0)) {
    			if (!password.equals(getValue(PASSWORD,0))) {
    				throw new UserDoesntFullfillEndEntityProfile("Password didn't match requirement of it's profile.");
    			}
    		} else if (isRequired(PASSWORD,0)) {
    			if (password == null || password.trim().equals("")) {
    				throw new UserDoesntFullfillEndEntityProfile("Password cannot be empty or null.");
    			}
    		}
    		// Assume a-zA-Z0-9 + 22 other printable chars = 72 different chars
    		final int passwordStrengthEstimate = getPasswordStrength(72, password.trim().length()); 
    		if (log.isDebugEnabled()) {
    			log.debug("passwordStrengthEstimate=" + passwordStrengthEstimate + " getMinPwdStrength=" + getMinPwdStrength());
    		}
    		if (getUse(EndEntityProfile.MINPWDSTRENGTH, 0) && (passwordStrengthEstimate< getMinPwdStrength())) {
    			throw new UserDoesntFullfillEndEntityProfile("Generated password is not strong enough (~" + passwordStrengthEstimate + " bits in specific password < " + getMinPwdStrength() + " bits required by end entity profile).");
    		}
    	}
    	// Checks related to the use of clear text passwords
    	if (!getUse(CLEARTEXTPASSWORD,0) && clearpwd) {
    		throw new UserDoesntFullfillEndEntityProfile("Clearpassword (used in batch processing) cannot be used.");
    	}
    	if (isRequired(CLEARTEXTPASSWORD,0)) {
    		if (getValue(CLEARTEXTPASSWORD,0).equals(TRUE) && !clearpwd) {
    			throw new UserDoesntFullfillEndEntityProfile("Clearpassword (used in batch processing) cannot be false.");
    		}
    		if (getValue(CLEARTEXTPASSWORD,0).equals(FALSE) && clearpwd) {
    			throw new UserDoesntFullfillEndEntityProfile("Clearpassword (used in batch processing) cannot be true.");
    		}
    	}
    	doesUserFullfillEndEntityProfileWithoutPassword(username, dn, subjectaltname, subjectdirattr, email,
    			certificateprofileid, keyrecoverable, sendnotification, tokentype, hardwaretokenissuerid, caid, ei);
    }

    public void doesUserFullfillEndEntityProfileWithoutPassword(final String username, final String dn, final String subjectaltname, final String subjectdirattr,
    		String email, final int certificateprofileid, final boolean keyrecoverable, final boolean sendnotification, final int tokentype,
    		final int hardwaretokenissuerid, final int caid, final ExtendedInformation ei) throws UserDoesntFullfillEndEntityProfile {
    	if (log.isTraceEnabled()) {
    		log.trace(">doesUserFullfillEndEntityProfileWithoutPassword()");
    	}
    	final DNFieldExtractor subjectdnfields = new DNFieldExtractor(dn, DNFieldExtractor.TYPE_SUBJECTDN);
    	if (subjectdnfields.isIllegal()) {
    		throw new UserDoesntFullfillEndEntityProfile("Subject DN is illegal.");
    	}
    	final DNFieldExtractor subjectaltnames = new DNFieldExtractor(subjectaltname, DNFieldExtractor.TYPE_SUBJECTALTNAME);
    	if (subjectaltnames.isIllegal()) {
    		throw new UserDoesntFullfillEndEntityProfile("Subject alt names are illegal.");
    	}
    	final DNFieldExtractor subjectdirattrs = new DNFieldExtractor(subjectdirattr, DNFieldExtractor.TYPE_SUBJECTDIRATTR);
    	if (subjectdirattrs.isIllegal()) {
    		throw new UserDoesntFullfillEndEntityProfile("Subject directory attributes are illegal.");
    	}
    	// Check that no other than supported dn fields exists in the subject dn.
    	if (subjectdnfields.existsOther()) {
    		throw new UserDoesntFullfillEndEntityProfile("Unsupported Subject DN Field found in:" + dn);
    	}
    	if (subjectaltnames.existsOther()) {
    		throw new UserDoesntFullfillEndEntityProfile("Unsupported Subject Alternate Name Field found in:" + subjectaltname );
    	}
    	if (subjectdirattrs.existsOther()) {
    		throw new UserDoesntFullfillEndEntityProfile("Unsupported Subject Directory Attribute Field found in:" + subjectdirattr );
    	}
    	checkIfAllRequiredFieldsExists(subjectdnfields, subjectaltnames, subjectdirattrs, username, email);
    	// Make sure that there are enough fields to cover all required in profile
    	checkIfForIllegalNumberOfFields(subjectdnfields, subjectaltnames, subjectdirattrs);
    	// Check contents of username.
    	checkIfDataFullfillProfile(USERNAME,0,username, "Username",null);
    	// Check Email address.
    	if (email == null) {
    		email = "";
    	}
    	checkIfDomainFullfillProfile(EMAIL,0,email,"Email");
    	// Make sure that every value has a corresponding field in the entity profile
    	checkIfFieldsMatch(subjectdnfields, DNFieldExtractor.TYPE_SUBJECTDN, email); 
    	checkIfFieldsMatch(subjectaltnames, DNFieldExtractor.TYPE_SUBJECTALTNAME, email);
    	// Check contents of Subject Directory Attributes fields.
    	final HashMap<Integer,Integer> subjectdirattrnumbers = subjectdirattrs.getNumberOfFields();
    	final Integer[] dirattrids = DNFieldExtractor.getUseFields(DNFieldExtractor.TYPE_SUBJECTDIRATTR);
    	for (final Integer dirattrid : dirattrids) {
    		final int nof = subjectdirattrnumbers.get(dirattrid).intValue();
    		for (int j=0; j<nof; j++) {
    			checkForIllegalChars(subjectdirattrs.getField(dirattrid.intValue(),j));
    			switch (dirattrid.intValue()) {
    			case DNFieldExtractor.COUNTRYOFCITIZENSHIP:
    				checkIfISO3166FullfillProfile(DnComponents.COUNTRYOFCITIZENSHIP,j,subjectdirattrs.getField(dirattrid.intValue(),j),"COUNTRYOFCITIZENSHIP");
    				break;
    			case DNFieldExtractor.COUNTRYOFRESIDENCE:
    				checkIfISO3166FullfillProfile(DnComponents.COUNTRYOFRESIDENCE,j,subjectdirattrs.getField(dirattrid.intValue(),j),"COUNTRYOFRESIDENCE");
    				break;
    			case DNFieldExtractor.DATEOFBIRTH:
    				checkIfDateFullfillProfile(DnComponents.DATEOFBIRTH,j,subjectdirattrs.getField(dirattrid.intValue(),j),"DATEOFBIRTH");
    				break;
    			case DNFieldExtractor.GENDER:
    				checkIfGenderFullfillProfile(DnComponents.GENDER,j,subjectdirattrs.getField(dirattrid.intValue(),j),"GENDER");
    				break;
    			default:
    				checkIfDataFullfillProfile(DnComponents.dnIdToProfileName(dirattrid.intValue()),j,subjectdirattrs.getField(dirattrid.intValue(),j), DnComponents.getErrTextFromDnId(dirattrid.intValue()), email);
    			}
    		}
    	}
    	// Check for keyrecoverable flag.
    	if (!getUse(KEYRECOVERABLE,0) && keyrecoverable) {
    		throw new UserDoesntFullfillEndEntityProfile("Key Recoverable cannot be used.");
    	}
    	if (isRequired(KEYRECOVERABLE,0)) {
    		if (getValue(KEYRECOVERABLE,0).equals(TRUE) && !keyrecoverable) {
    			throw new UserDoesntFullfillEndEntityProfile("Key Recoverable is required.");
    		}
    		if (getValue(KEYRECOVERABLE,0).equals(FALSE) && keyrecoverable) {
    			throw new UserDoesntFullfillEndEntityProfile("Key Recoverable cannot be set in current end entity profile.");
    		}
    	}
    	// Check for send notification flag.
    	if (!getUse(SENDNOTIFICATION,0) && sendnotification) {
    		throw new UserDoesntFullfillEndEntityProfile("Email notification cannot be used.");
    	}
    	if (isRequired(SENDNOTIFICATION,0)) {
    		if (getValue(SENDNOTIFICATION,0).equals(TRUE) && !sendnotification) {
    			throw new UserDoesntFullfillEndEntityProfile("Email notification is required.");
    		}
    		if (getValue(SENDNOTIFICATION,0).equals(FALSE) && sendnotification) {
    			throw new UserDoesntFullfillEndEntityProfile("Email notification cannot be set in current end entity profile.");
    		}
    	}
    	// Check if certificate profile is among available certificate profiles.
    	String[] availablecertprofiles;
    	try {
    		availablecertprofiles = getValue(AVAILCERTPROFILES,0).split(SPLITCHAR);
    	} catch (Exception e) {
    		throw new UserDoesntFullfillEndEntityProfile("Error parsing end entity profile.");
    	}
    	if (availablecertprofiles == null) {
    		throw new UserDoesntFullfillEndEntityProfile("Error Available certificate profiles is null.");
    	}
    	final String certificateprofileidString = Integer.valueOf(certificateprofileid).toString();
    	boolean certprofilefound = false;
    	for (final String currentAvailableCertProfile : availablecertprofiles) {
    		if (certificateprofileidString.equals(currentAvailableCertProfile)) {
    			certprofilefound = true;
    			break;
    		}
    	}
    	if (!certprofilefound) {
    		throw new UserDoesntFullfillEndEntityProfile("Couldn't find certificate profile ("+certificateprofileid+") among available certificate profiles.");
    	}
    	// Check if tokentype is among available token types.
    	String[] availablesofttokentypes;
    	try {
    		availablesofttokentypes = getValue(AVAILKEYSTORE,0).split(SPLITCHAR);
    	} catch (Exception e) {
    		throw new UserDoesntFullfillEndEntityProfile("Error parsing end entity profile.");
    	}
    	if (availablesofttokentypes == null) {
    		throw new UserDoesntFullfillEndEntityProfile("Error available  token types is null.");
    	}
    	final String tokenTypeString = Integer.valueOf(tokentype).toString();
    	boolean softtokentypefound = false;
    	for (final String currentAvailableSoftTokenType : availablesofttokentypes) {
    		if (tokenTypeString.equals(currentAvailableSoftTokenType)) {
    			softtokentypefound = true;
    			break;
    		}
    	}
    	if (!softtokentypefound) {
    		throw new UserDoesntFullfillEndEntityProfile("Soft token type is not available in End Entity Profile.");
    	}
    	// If soft token check for hardwaretoken issuer id = 0.
    	if (tokentype <= SecConst.TOKEN_SOFT) {
    		if (hardwaretokenissuerid != 0) {
    			throw new UserDoesntFullfillEndEntityProfile("Soft tokens cannot have a hardware token issuer.");
    		}
    	}
    	// If Hard token type check if hardware token issuer is among available hardware token issuers.
    	if (tokentype > SecConst.TOKEN_SOFT && getUse(AVAILTOKENISSUER, 0) ) { // Hardware token.
    		String[] availablehardtokenissuers;
    		try {
    			availablehardtokenissuers = getValue(AVAILTOKENISSUER, 0).split(SPLITCHAR);
    		} catch (Exception e) {
    			throw new UserDoesntFullfillEndEntityProfile("Error parsing end entity profile.");
    		}
    		if (availablehardtokenissuers == null) {
    			throw new UserDoesntFullfillEndEntityProfile("Error available hard token issuers is null.");
    		}
    		final String hardwaretokenissueridString = Integer.valueOf(hardwaretokenissuerid).toString();
    		boolean hardtokentypefound = false;
    		for (final String currentAvailableHardTokenIssuer : availablehardtokenissuers) {
    			if (hardwaretokenissueridString.equals(currentAvailableHardTokenIssuer)) {
    				hardtokentypefound = true;
    				break;
    			}
    		}
    		if (!hardtokentypefound) {
    			throw new UserDoesntFullfillEndEntityProfile("Couldn't find hard token issuers among available hard token issuers.");
    		}
    	}
    	// Check if ca id is among available ca ids.
    	String[] availablecaids;
    	try {
    		availablecaids = getValue(AVAILCAS,0).split(SPLITCHAR);
    	} catch (Exception e) {
    		throw new UserDoesntFullfillEndEntityProfile("Error parsing end entity profile.");
    	}
    	if (availablecaids == null) {
    		throw new UserDoesntFullfillEndEntityProfile("Error End Entity Profiles Available CAs is null.");
    	}
    	boolean caidfound=false;
    	for (final String currentAvailableCaId : availablecaids) {
    		final int tmp = Integer.parseInt(currentAvailableCaId);
    		if (tmp == caid || tmp == SecConst.ALLCAS) {
    			caidfound = true;
    			break;
    		}
    	}
    	if (!caidfound) {
    		throw new UserDoesntFullfillEndEntityProfile("Couldn't find CA ("+caid+") among End Entity Profiles Available CAs.");
    	}
    	// Check if time constraints are valid
    	String startTime = null;
    	String endTime = null;
    	if ( ei != null ) {
    		startTime = ei.getCustomData(EndEntityProfile.STARTTIME);
    		log.debug("startTime is: "+startTime);
    		endTime = ei.getCustomData(EndEntityProfile.ENDTIME);
    		log.debug("endTime is: "+endTime);
    	}
    	final String[] datePatterns = {"yyyy-MM-dd HH:mm"};
    	final Date now = new Date();
    	Date startTimeDate = null;
    	if( getUse(STARTTIME, 0) && startTime != null && !startTime.equals("") ) {
    		if ( startTime.matches("^\\d+:\\d?\\d:\\d?\\d$") ) { //relative time
    			final String[] startTimeArray = startTime.split(":");
    			if ( Long.parseLong(startTimeArray[0]) < 0 || Long.parseLong(startTimeArray[1]) < 0 || Long.parseLong(startTimeArray[2]) < 0 ) {
    				throw new UserDoesntFullfillEndEntityProfile("Cannot use negtive relative time.");
    			}
    			final long relative = (Long.parseLong(startTimeArray[0])*24*60 + Long.parseLong(startTimeArray[1])*60 +
    					Long.parseLong(startTimeArray[2])) * 60 * 1000;
    			startTimeDate = new Date(now.getTime() + relative);
    		} else {
    			try {
    				startTimeDate = DateUtils.parseDate(startTime, datePatterns);
    			} catch (ParseException e) {
    			}
    		}
    		if (startTimeDate == null) {
    			// If we could not parse the date string, something was awfully wrong
    			throw new UserDoesntFullfillEndEntityProfile("Invalid start time: "+startTime);    		  
    		}
    	}
    	Date endTimeDate = null;
    	if( getUse(ENDTIME, 0) && endTime != null && !endTime.equals("") ) {
    		if ( endTime.matches("^\\d+:\\d?\\d:\\d?\\d$") ) { //relative time
    			final String[] endTimeArray = endTime.split(":");
    			if ( Long.parseLong(endTimeArray[0]) < 0 || Long.parseLong(endTimeArray[1]) < 0 || Long.parseLong(endTimeArray[2]) < 0 ) {
    				throw new UserDoesntFullfillEndEntityProfile("Cannot use negtive relative time.");
    			}
    			final long relative = (Long.parseLong(endTimeArray[0])*24*60 + Long.parseLong(endTimeArray[1])*60 +
    					Long.parseLong(endTimeArray[2])) * 60 * 1000;
    			// If we haven't set a startTime, use "now"
    			final Date start = (startTimeDate == null) ? new Date(): startTimeDate;
    			endTimeDate = new Date(start.getTime() + relative);
    		} else {
    			try {
    				endTimeDate = DateUtils.parseDate(endTime, datePatterns);
    			} catch (ParseException e) {
    			}
    		}
    		if (endTimeDate == null) {
    			// If we could not parse the date string, something was awfulyl wrong
    			throw new UserDoesntFullfillEndEntityProfile("Invalid end time: "+endTime);    		  
    		}
    	}
    	if ( (startTimeDate != null) && (endTimeDate != null) ) {
    		if ( getUse(STARTTIME, 0) && getUse(ENDTIME, 0) && !startTimeDate.before(endTimeDate) ) {
    			throw new UserDoesntFullfillEndEntityProfile("Dates must be in right order. "+startTime+" "+endTime+" "+
    					ValidityDate.formatAsUTC(startTimeDate)+" "+ValidityDate.formatAsUTC(endTimeDate));
    		}    	  
    	}
    	// Check number of allowed requests
    	String allowedRequests = null;
    	if ( ei != null ) {
    		allowedRequests = ei.getCustomData(ExtendedInformation.CUSTOM_REQUESTCOUNTER);
    	}
    	if ( (allowedRequests != null) && !getUse(ALLOWEDREQUESTS, 0) ) {
    		throw new UserDoesntFullfillEndEntityProfile("Allowed requests used, but not permitted by profile.");
    	}
    	// Check initial issuance revocation reason
    	String issuanceRevReason = null;
    	if ( ei != null ) {
    		issuanceRevReason = ei.getCustomData(ExtendedInformation.CUSTOM_REVOCATIONREASON);
    	}
    	if ( (issuanceRevReason != null) && !getUse(ISSUANCEREVOCATIONREASON, 0) ) {
    		throw new UserDoesntFullfillEndEntityProfile("Issuance revocation reason used, but not permitted by profile.");
    	}
    	if ( getUse(ISSUANCEREVOCATIONREASON, 0) && !isModifyable(ISSUANCEREVOCATIONREASON, 0) ) {
    		final String value = getValue(ISSUANCEREVOCATIONREASON, 0);
    		if (!StringUtils.equals(issuanceRevReason, value)) {
    			throw new UserDoesntFullfillEndEntityProfile("Issuance revocation reason '"+issuanceRevReason+"' does not match required value '"+value+"'.");
    		}
    	}
    	// Check maximum number of failed logins
    	if ( getUse(MAXFAILEDLOGINS, 0) && !isModifyable(MAXFAILEDLOGINS,0)) {
    		// If we MUST have MAXFAILEDLOGINS, ei can not be null
    		if ( (ei == null) || !getValue(MAXFAILEDLOGINS,0).equals(Integer.toString(ei.getMaxLoginAttempts())) ) {
    			throw new UserDoesntFullfillEndEntityProfile("Max failed logins is not modifyable.");
    		}
    	}      
    	if (log.isTraceEnabled()) {
    		log.trace("<doesUserFullfillEndEntityProfileWithoutPassword()");
    	}
    }

    /**
     * This function tries to match each field in the profile to a corresponding field in the DN/AN/AD-fields.
     * Can not be used for DNFieldExtractor.TYPE_SUBJECTDIRATTR yet.
     *   
     * @param fields
     * @param type One of DNFieldExtractor.TYPE_SUBJECTDN, DNFieldExtractor.TYPE_SUBJECTALTNAME
     * @param email The end entity's email address
     * @throws UserDoesntFullfillEndEntityProfile
     */
    private void checkIfFieldsMatch(final DNFieldExtractor fields, final int type, final String email) throws UserDoesntFullfillEndEntityProfile {
    	final int REQUIRED_FIELD		= 2;
    	final int NONMODIFYABLE_FIELD	= 1;
    	final int MATCHED_FIELD			= -1;
    	final Integer[] dnids = DNFieldExtractor.getUseFields(type);
    	// For each type of field
    	for ( int i=0; i<dnids.length; i++ ) {
    		final int dnid = dnids[i].intValue();
    		final int profileID = DnComponents.dnIdToProfileId(dnid);
    		final int dnFieldExtractorID = DnComponents.profileIdToDnId(profileID);
    		final int nof = fields.getNumberOfFields(dnFieldExtractorID);
    		final int numberOfProfileFields = getNumberOfField(profileID);
    		if ( nof == 0 && numberOfProfileFields == 0 ) {
    			continue;	// Nothing to see here..
    		}
    		// Create array with all entries of that type
    		final String[] subjectsToProcess = new String[nof];
    		for ( int j=0; j<nof; j++ ) {
    			String fieldValue = fields.getField(dnFieldExtractorID, j);
    			// Only keep domain for comparison of RFC822NAME, DNEMAIL and UPN fields
    			if ( DnComponents.RFC822NAME.equals(DnComponents.dnIdToProfileName(dnid)) || DnComponents.DNEMAIL.equals(DnComponents.dnIdToProfileName(dnid)) || DnComponents.UPN.equals(DnComponents.dnIdToProfileName(dnid)) ) {
        			if ( fieldValue.indexOf('@') == -1 ) {
        				throw new UserDoesntFullfillEndEntityProfile(DnComponents.dnIdToProfileName(dnid) + " does not seem to be in something@somthingelse format.");
        			}
        			fieldValue = fieldValue.split("@")[1];
    			} else {
        			// Check that postalAddress has #der_encoding_in_hex format, i.e. a full der sequence in hex format
        			if ( DnComponents.POSTALADDRESS.equals(DnComponents.dnIdToProfileName(dnid))) {
        				if (!StringUtils.startsWith(fieldValue, "#30")) {
            				throw new UserDoesntFullfillEndEntityProfile(DnComponents.dnIdToProfileName(dnid) + " ("+fieldValue+") does not seem to be in #der_encoding_in_hex format. See \"http://ejbca.org/userguide.html#End Entity Profile fields\" for more information about the postalAddress (2.5.4.16) field.");        					
        				}
        			}    				
    			}
    			subjectsToProcess[j] = fieldValue;
    		}
    		//	Create array with profile values 3 = required and non-mod, 2 = required, 1 = non-modifiable, 0 = neither
    		final int[] profileCrossOffList = new int[numberOfProfileFields];
    		for ( int j=0; j< getNumberOfField(profileID); j++ ) {
    			profileCrossOffList[j] += ( isModifyable(profileID, j) ? 0 : NONMODIFYABLE_FIELD ) + ( isRequired(profileID, j) ? REQUIRED_FIELD : 0 ); 
    		}
    		// Start by matching email strings
			if ( DnComponents.RFC822NAME.equals(DnComponents.dnIdToProfileName(dnid)) || DnComponents.DNEMAIL.equals(DnComponents.dnIdToProfileName(dnid)) ) {
	    		for ( int k=3; k>=0; k--) {
	    			//	For every value in profile
	    			for ( int l=0; l<profileCrossOffList.length; l++ ) {
	    				if ( profileCrossOffList[l] == k ) {
	    					//	Match with every value in field-array
	    					for ( int m=0; m<subjectsToProcess.length; m++ ) {
	    						if ( subjectsToProcess[m] != null && profileCrossOffList[l] != MATCHED_FIELD ) {
	    							if ( !(!getUse(profileID, l) && DnComponents.RFC822NAME.equals(DnComponents.dnIdToProfileName(dnid))) ) {
	    								if ( fields.getField(dnFieldExtractorID, m).equals(email) ){
	    									subjectsToProcess[m] = null;
	    									profileCrossOffList[l] = MATCHED_FIELD;
	    								}
	    							}
	    						}
	    					}
	    				}
	    			}
	    		}
			}
    		// For every field of this type in profile (start with required and non-modifiable, 2 + 1)
    		for ( int k=3; k>=0; k--) {
    			// For every value in profile
    			for ( int l=0; l<profileCrossOffList.length; l++ ) {
    				if ( profileCrossOffList[l] == k ) {
    					// Match with every value in field-array
    					for ( int m=0; m<subjectsToProcess.length; m++ ) {
    						if ( subjectsToProcess[m] != null && profileCrossOffList[l] != MATCHED_FIELD ) {
        						// Match actual value if required + non-modifiable or non-modifiable
        						if ( (k == (REQUIRED_FIELD + NONMODIFYABLE_FIELD) || k == (NONMODIFYABLE_FIELD)) ) {
        							// Try to match with all possible values
        							String[] fixedValues = getValue(profileID, l).split(SPLITCHAR);
        							for ( int n=0; n<fixedValues.length; n++) {
        								if ( subjectsToProcess[m] != null && subjectsToProcess[m].equals(fixedValues[n]) ) {
        	    							// Remove matched pair
        	    							subjectsToProcess[m] = null;
        	    							profileCrossOffList[l] = MATCHED_FIELD;
        								}
        							}
           						// Otherwise just match present fields
        						} else {
        							// Remove matched pair
        							subjectsToProcess[m] = null;
        							profileCrossOffList[l] = MATCHED_FIELD;
        						}
    						}
    					}
    				}
    			}
    		}
    		// If not all fields in profile were found
    		for ( int j=0; j< nof; j++ ) {
    			if ( subjectsToProcess[j] != null ) {
    				throw new UserDoesntFullfillEndEntityProfile("End entity profile does not contain matching field for " +
    						DnComponents.dnIdToProfileName(dnid) + " with value \"" + subjectsToProcess[j] + "\".");
    			}
    		}
    		// If not all required fields in profile were found in subject 
    		for ( int j=0; j< getNumberOfField(profileID); j++ ) {
    			if ( profileCrossOffList[j] >= REQUIRED_FIELD ) {
    				throw new UserDoesntFullfillEndEntityProfile("Data does not contain required " + DnComponents.dnIdToProfileName(dnid) + " field.");
    			}
    		}
    	}
    } // checkIfFieldsMatch

	public void doesPasswordFulfillEndEntityProfile(String password, boolean clearpwd) throws UserDoesntFullfillEndEntityProfile {
		boolean fullfillsprofile = true;
		if (useAutoGeneratedPasswd()) {
			if (password !=null) {
				throw new UserDoesntFullfillEndEntityProfile("Autogenerated password must have password==null");
			}
		} else {           		            
			if (!isModifyable(EndEntityProfile.PASSWORD,0)) {
				if(!password.equals(getValue(EndEntityProfile.PASSWORD,0))) {		   
					fullfillsprofile = false;
				}
			} else {
				if (isRequired(EndEntityProfile.PASSWORD,0)) {
					if((!clearpwd && password == null) || (password != null && password.trim().equals(""))) {			
						fullfillsprofile = false;
					}
				}
			}
		}
		if (clearpwd && isRequired(EndEntityProfile.CLEARTEXTPASSWORD,0) && getValue(EndEntityProfile.CLEARTEXTPASSWORD,0).equals(EndEntityProfile.FALSE)){		 	
			fullfillsprofile = false;
		}
		if (!fullfillsprofile) {
			throw new UserDoesntFullfillEndEntityProfile("Password doesn't fullfill profile.");
		}
	}

    public Object clone() throws CloneNotSupportedException {
    	final EndEntityProfile clone = new EndEntityProfile(0);
    	// We need to make a deep copy of the hashmap here
    	clone.data = new HashMap<Object,Object>(data.size());
    	for (final Entry<Object,Object> entry : data.entrySet()) {
    		Object value = entry.getValue();
    		if (value instanceof ArrayList<?>) {
    			// We need to make a clone of this object, but the stored Integers can still be referenced
    			value = ((ArrayList<?>)value).clone();
    		}
    		clone.data.put(entry.getKey(), value);
    	}
    	return clone;
    }

    /** Implementation of UpgradableDataHashMap function getLatestVersion */
    public float getLatestVersion(){
       return LATEST_VERSION;
    }

    /** Implementation of UpgradableDataHashMap function upgrade. */
    public void upgrade() {
        log.trace(">upgrade");        
    	if (Float.compare(LATEST_VERSION, getVersion()) != 0) {
			String msg = intres.getLocalizedMessage("ra.eeprofileupgrade", new Float(getVersion()));
            log.info(msg);
            // New version of the class, upgrade
            if (getVersion() < 1) {
                ArrayList<Integer> numberarray = (ArrayList<Integer>) data.get(NUMBERARRAY);
                while (numberarray.size() < 37) {
                   numberarray.add(Integer.valueOf(0));
                }
                data.put(NUMBERARRAY,numberarray);
            }
            if (getVersion() < 2) {
                ArrayList<Integer> numberarray = (ArrayList<Integer>) data.get(NUMBERARRAY);
                while (numberarray.size() < 39) {
                   numberarray.add(Integer.valueOf(0));
                }
                data.put(NUMBERARRAY,numberarray);
                addField(AVAILCAS);
                addField(DEFAULTCA);
                setRequired(AVAILCAS,0,true);
                setRequired(DEFAULTCA,0,true);
            }
            if (getVersion() < 3) {
            	// These fields have been removed in version 8, no need for this upgrade
                //setNotificationSubject("");
                //setNotificationSender("");
                //setNotificationMessage("");
            }
            if (getVersion() < 4) {
                ArrayList<Integer> numberoffields = (ArrayList<Integer>) data.get(NUMBERARRAY);                
                for (int i =numberoffields.size(); i < dataConstants.size(); i++) {
                  numberoffields.add(Integer.valueOf(0));
                }               
                data.put(NUMBERARRAY,numberoffields);                
            }
            // Support for DirectoryName altname field in profile version 5
            if (getVersion() < 5) {
                addField(DnComponents.DIRECTORYNAME);
                setValue(DnComponents.DIRECTORYNAME,0,"");
                setRequired(DnComponents.DIRECTORYNAME,0,false);
                setUse(DnComponents.DIRECTORYNAME,0,true);
                setModifyable(DnComponents.DIRECTORYNAME,0,true);            	
            }
            // Support for Subject Directory Attributes field in profile version 6
            if (getVersion() < 6) {
                ArrayList<Integer> numberoffields = (ArrayList<Integer>) data.get(NUMBERARRAY);                
                for(int i =numberoffields.size(); i < dataConstants.size(); i++){
                  numberoffields.add(Integer.valueOf(0));
                }               
                data.put(NUMBERARRAY,numberoffields);
                data.put(SUBJECTDIRATTRFIELDORDER,new ArrayList<Integer>());
                
                for(int i=getParameterNumber(DnComponents.DATEOFBIRTH); i <= getParameterNumber(DnComponents.COUNTRYOFRESIDENCE); i++){
                	addField(getParameter(i));
                	setValue(getParameter(i),0,"");
                	setRequired(getParameter(i),0,false);
                	setUse(getParameter(i),0,false);
                	setModifyable(getParameter(i),0,true);
                }  
            }
            // Support for Start Time and End Time field in profile version 7
            if (getVersion() < 7) {
                ArrayList<Integer> numberoffields = (ArrayList<Integer>) data.get(NUMBERARRAY);                
                for(int i =numberoffields.size(); i < dataConstants.size(); i++){
                	numberoffields.add(Integer.valueOf(0));
                }               
                data.put(NUMBERARRAY,numberoffields);
                addField(STARTTIME);
                setValue(STARTTIME, 0, "");
                setRequired(STARTTIME, 0, false);
                setUse(STARTTIME, 0, false);
                setModifyable(STARTTIME, 0, true);            	
                addField(ENDTIME);
                setValue(ENDTIME, 0, "");
                setRequired(ENDTIME, 0, false);
                setUse(ENDTIME, 0, false);
                setModifyable(ENDTIME, 0, true);            	
            }
            // Notifications is now a more general mechanism in version 8
            if (getVersion() < 8) {
            	log.debug("Upgrading User Notifications");
            	if (data.get(UserNotification.NOTIFICATIONSENDER) != null) {
            		UserNotification not = new UserNotification();
            		not.setNotificationSender((String)data.get(UserNotification.NOTIFICATIONSENDER));
            		if (data.get(UserNotification.NOTIFICATIONSUBJECT) != null) {
                		not.setNotificationSubject((String)data.get(UserNotification.NOTIFICATIONSUBJECT));            			
            		}
            		if (data.get(UserNotification.NOTIFICATIONMESSAGE) != null) {
                		not.setNotificationMessage((String)data.get(UserNotification.NOTIFICATIONMESSAGE));            			
            		}
            		// Add the statuschanges we used to send notifications about
            		String events = UserNotification.EVENTS_EDITUSER;
            		not.setNotificationEvents(events);
            		// The old recipients where always the user
            		not.setNotificationRecipient(UserNotification.RCPT_USER);
            		addUserNotification(not);
            	}
            }
            // Support for allowed requests in profile version 9
            if (getVersion() < 9) {
                ArrayList<Integer> numberoffields = (ArrayList<Integer>) data.get(NUMBERARRAY);                
                for(int i =numberoffields.size(); i < dataConstants.size(); i++){
                	numberoffields.add(Integer.valueOf(0));
                }               
                data.put(NUMBERARRAY,numberoffields);
                addField(ALLOWEDREQUESTS);
                setValue(ALLOWEDREQUESTS, 0, "");
                setRequired(ALLOWEDREQUESTS, 0, false);
                setUse(ALLOWEDREQUESTS, 0, false);
                setModifyable(ALLOWEDREQUESTS, 0, true);            	
            }
            // Support for merging DN from WS-API with default values in profile, in profile version 10
            if (getVersion() < 10) {
                setAllowMergeDnWebServices(false);
            }
            // Support for issuance revocation status in profile version 11
            if (getVersion() < 11) {
                setRequired(ISSUANCEREVOCATIONREASON, 0, false);
                setUse(ISSUANCEREVOCATIONREASON, 0, false);
                setModifyable(ISSUANCEREVOCATIONREASON, 0, true);
                setValue(ISSUANCEREVOCATIONREASON, 0, "" + RevokedCertInfo.NOT_REVOKED);
                setRequired(CARDNUMBER, 0, false);
                setUse(CARDNUMBER, 0, false);
                setModifyable(CARDNUMBER, 0, true);            	
            }
            // Support for maximum number of failed login attempts in profile version 12
            if (getVersion() < 12) {
            	setRequired(MAXFAILEDLOGINS, 0, false);
            	setUse(MAXFAILEDLOGINS, 0, false);
            	setModifyable(MAXFAILEDLOGINS, 0, true);
            	setValue(MAXFAILEDLOGINS, 0, Integer.toString(ExtendedInformation.DEFAULT_MAXLOGINATTEMPTS));
            }
            /* In EJBCA 4.0.0 we changed the date format to ISO 8601.
             * In the Admin GUI the example was:
             *     DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, ejbcawebbean.getLocale())
             * but the only absolute format that could have worked is the same enforced by the
             * doesUserFullfillEndEntityProfile check and this is what need to upgrade from:
             *     DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.US)
             */
        	if (getVersion() < 13) {
        		final DateFormat oldDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.US);
        		final FastDateFormat newDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm");
        		try {
        			final String oldStartTime = getValue(STARTTIME, 0);
        			if (!isEmptyOrRelative(oldStartTime)) {
        				// We use an absolute time format, so we need to upgrade
            			final String newStartTime = newDateFormat.format(oldDateFormat.parse(oldStartTime));
    					setValue(STARTTIME, 0, newStartTime);
    					if (log.isDebugEnabled()) {
    						log.debug("Upgraded " + STARTTIME + " from \"" + oldStartTime + "\" to \"" + newStartTime + "\" in EndEntityProfile.");
    					}
        			}
				} catch (ParseException e) {
					log.error("Unable to upgrade " + STARTTIME + " in EndEntityProfile! Manual interaction is required (edit and verify).", e);
				}
        		try {
        			final String oldEndTime = getValue(ENDTIME, 0);
        			if (!isEmptyOrRelative(oldEndTime)) {
        				// We use an absolute time format, so we need to upgrade
            			final String newEndTime = newDateFormat.format(oldDateFormat.parse(oldEndTime));
    					setValue(ENDTIME, 0, newEndTime);
    					if (log.isDebugEnabled()) {
    						log.debug("Upgraded " + ENDTIME + " from \"" + oldEndTime + "\" to \"" + newEndTime + "\" in EndEntityProfile.");
    					}
        			}
				} catch (ParseException e) {
					log.error("Unable to upgrade " + ENDTIME + " in EndEntityProfile! Manual interaction is required (edit and verify).", e);
				}
        	}
        	/*
        	 * In version 13 we converted some dates to the "yyyy-MM-dd HH:mm" format using default Locale.
        	 * These needs to be converted to the same format but should be stored in UTC, so we always know what the times are.
        	 */
        	if (getVersion() < 14) {
        		final String[] timePatterns = {"yyyy-MM-dd HH:mm"};
    			final String oldStartTime = getValue(STARTTIME, 0);
    			if (!isEmptyOrRelative(oldStartTime)) {
            		try {
            			final String newStartTime = ValidityDate.formatAsUTC(DateUtils.parseDateStrictly(oldStartTime, timePatterns));
    					setValue(STARTTIME, 0, newStartTime);
    					if (log.isDebugEnabled()) {
    						log.debug("Upgraded " + STARTTIME + " from \"" + oldStartTime + "\" to \"" + newStartTime + "\" in EndEntityProfile.");
    					}
					} catch (ParseException e) {
						log.error("Unable to upgrade " + STARTTIME + " to UTC in EndEntityProfile! Manual interaction is required (edit and verify).", e);
					}
    			}
    			final String oldEndTime = getValue(ENDTIME, 0);
    			if (!isEmptyOrRelative(oldEndTime)) {
    				// We use an absolute time format, so we need to upgrade
					try {
						final String newEndTime = ValidityDate.formatAsUTC(DateUtils.parseDateStrictly(oldEndTime, timePatterns));
						setValue(ENDTIME, 0, newEndTime);
						if (log.isDebugEnabled()) {
							log.debug("Upgraded " + ENDTIME + " from \"" + oldEndTime + "\" to \"" + newEndTime + "\" in EndEntityProfile.");
						}
					} catch (ParseException e) {
						log.error("Unable to upgrade " + ENDTIME + " to UTC in EndEntityProfile! Manual interaction is required (edit and verify).", e);
					}
    			}
        	}
        	// Finally, update the version stored in the map to the current version
            data.put(VERSION, new Float(LATEST_VERSION));
        }
        log.trace("<upgrade");
    }
    
    /** @return true if argument is null, empty or in the relative time format. */
    private boolean isEmptyOrRelative(final String time) {
    	return (time == null || time.length()==0 || time.matches("^\\d+:\\d?\\d:\\d?\\d$"));
    }

    public static boolean isFieldImplemented(final int field) {
    	final String f = getParameter(field);
    	if (f == null) {
    		log.info("isFieldImplemented got call for non-implemented field: "+field);
    		return false;
    	}
    	return isFieldImplemented(f);
    }

    public static boolean isFieldImplemented(final String field) {
    	boolean ret = true;
        if (field.equals(DnComponents.OTHERNAME) 
        		|| field.equals(DnComponents.X400ADDRESS) 
        		|| field.equals(DnComponents.EDIPARTNAME) 
        		|| field.equals(DnComponents.REGISTEREDID)) {
    		log.info("isFieldImplemented got call for non-implemented field: "+field);
        	ret = false;
        }
        return ret;
    }

	public static boolean isFieldOfType(final int fieldNumber, final String fieldString) {
		boolean ret = false;
		final int number = getParameterNumber(fieldString);
		if (fieldNumber == number) {
			ret = true;
		}
		return ret;
	}

    //
    // Private Methods
    //

    /**
     * Verify that the field contains an address and that data of non-modifyable domain-fields is available in profile 
     * Used for email, upn and rfc822 fields
     */
    private void checkIfDomainFullfillProfile(final String field, final int number, final String nameAndDomain, final String text) throws UserDoesntFullfillEndEntityProfile {
    	if (!nameAndDomain.trim().equals("") && nameAndDomain.indexOf('@') == -1) {
    		throw new UserDoesntFullfillEndEntityProfile("Invalid " + text + "(" + nameAndDomain + "). There must be a '@' character in the field.");
    	}
    	final String domain = nameAndDomain.substring(nameAndDomain.indexOf('@') + 1);
    	// All fields except RFC822NAME has to be empty if not used flag is set.
    	if ( !DnComponents.RFC822NAME.equals(field) && !getUse(field,number) && !nameAndDomain.trim().equals("") ) {
    		throw new UserDoesntFullfillEndEntityProfile(text + " cannot be used in end entity profile.");
    	}
    	if (!isModifyable(field,number) && !nameAndDomain.equals("")) {
    		String[] values;
    		try {
    			values = getValue(field, number).split(SPLITCHAR);
    		} catch (Exception e) {
    			throw new UserDoesntFullfillEndEntityProfile("Error parsing end entity profile.");
    		}
    		boolean exists = false;
    		for (final String value : values) {
    			if(domain.equals(value.trim())) {
    				exists = true;
    				break;
    			}
    		}
    		if (!exists) {
    			throw new UserDoesntFullfillEndEntityProfile("Field " + text + " data didn't match requirement of end entity profile.");
    		}
    	}
    }
    
    private void checkForIllegalChars(final String str) throws UserDoesntFullfillEndEntityProfile {
    	if (StringTools.hasSqlStripChars(str)) {
    		throw new UserDoesntFullfillEndEntityProfile("Invalid " + str + ". Contains illegal characters.");    		
    	}    	
    }

    /** Used for iso 3166 country codes. */
    private void checkIfISO3166FullfillProfile(final String field, final int number, final String country, final String text) throws UserDoesntFullfillEndEntityProfile {
    	final String countryTrim = country.trim();
    	final int countryTrimLength = countryTrim.length();
    	if (countryTrimLength != 0 && countryTrimLength != 2) {
    		throw new UserDoesntFullfillEndEntityProfile("Invalid " + text + ". Must be of length two.");
    	}
    	if (!getUse(field,number) && countryTrimLength != 0) {
    		throw new UserDoesntFullfillEndEntityProfile(text + " cannot be used in end entity profile.");
    	}
    	if (!isModifyable(field,number) && countryTrimLength != 0) {
    		String[] values;
    		try {
    			values = getValue(field, number).split(SPLITCHAR);
    		} catch (Exception e) {
    			throw new UserDoesntFullfillEndEntityProfile("Error parsing end entity profile.");
    		}
    		boolean exists = false;
    		for (final String value : values) {
    			if (country.equals(value.trim())) {
    				exists = true;
    				break;
    			}
    		}
    		if (!exists) {
    			throw new UserDoesntFullfillEndEntityProfile("Field " + text + " data didn't match requirement of end entity profile.");
    		}
    	}
    }
    
    /** Used to check if it is an M or an F */
    private void checkIfGenderFullfillProfile(final String field, final int number, final String gender, final String text) throws UserDoesntFullfillEndEntityProfile {
    	final boolean isGenerEmpty = gender.trim().isEmpty();
    	if (!isGenerEmpty && !(gender.equalsIgnoreCase("m") || gender.equalsIgnoreCase("f"))) {
    		throw new UserDoesntFullfillEndEntityProfile("Invalid " + text + ". Must be M or F.");
    	}
    	if (!getUse(field,number) && !isGenerEmpty) {
    		throw new UserDoesntFullfillEndEntityProfile(text + " cannot be used in end entity profile.");
    	}
    	if (!isModifyable(field,number) && !isGenerEmpty) {
    		String[] values;
    		try {
    			values = getValue(field, number).split(SPLITCHAR);
    		} catch(Exception e) {
    			throw new UserDoesntFullfillEndEntityProfile("Error parsing end entity profile.");
    		}
    		boolean exists = false;
    		for (final String value: values) {
    			if (gender.equals(value.trim())) {
    				exists = true;
    				break;
    			}
    		}
    		if (!exists) {
    			throw new UserDoesntFullfillEndEntityProfile("Field " + text + " data didn't match requirement of end entity profile.");
    		}
    	}
    }

    /** Used for date strings, should be YYYYMMDD */
    private void checkIfDateFullfillProfile(final String field, final int number, final String date, final String text) throws UserDoesntFullfillEndEntityProfile {
    	final String dateTrim = date.trim();
    	final boolean isDateEmpty = dateTrim.isEmpty();
    	if (!isDateEmpty && dateTrim.length() != 8) {
    		throw new UserDoesntFullfillEndEntityProfile("Invalid " + text + ". Must be of length eight.");
    	}
    	if (!isDateEmpty && !StringUtils.isNumeric(dateTrim)) {
    		throw new UserDoesntFullfillEndEntityProfile("Invalid " + text + ". Must be only numbers.");
    	}
    	if (!getUse(field,number) && !isDateEmpty) {
    		throw new UserDoesntFullfillEndEntityProfile(text + " cannot be used in end entity profile.");
    	}
    	if (!isModifyable(field,number) && !isDateEmpty) {
    		String[] values;
    		try {
    			values = getValue(field, number).split(SPLITCHAR);
    		} catch (Exception e) {
    			throw new UserDoesntFullfillEndEntityProfile("Error parsing end entity profile.");
    		}
    		boolean exists = false;
    		for (final String value : values) {
    			if (date.equals(value.trim())) {
    				exists = true;
    				break;
    			}
    		}
    		if (!exists) {
    			throw new UserDoesntFullfillEndEntityProfile("Field " + text + " data didn't match requirement of end entity profile.");
    		}
    	}
    }

    /**
     * Verifies that non-modifiable data is available in profile.
     * @throws UserDoesntFullfillEndEntityProfile
     */
    private void checkIfDataFullfillProfile(final String field, final int number, final String data, final String text, final String email) throws UserDoesntFullfillEndEntityProfile {
    	if (data == null && !field.equals(EMAIL)) {
    		throw new UserDoesntFullfillEndEntityProfile("Field " +  text + " cannot be null.");
    	}
    	if (data !=null) {
    		if (!getUse(field,number) && !data.trim().isEmpty()) {
    			throw new UserDoesntFullfillEndEntityProfile(text + " cannot be used in end entity profile.");
    		}
    	}
    	if (field.equals(DnComponents.DNEMAIL)) {
    		if (isRequired(field,number)) {
    			if (!data.trim().equals(email.trim())) {
    				throw new UserDoesntFullfillEndEntityProfile("Field " + text + " data didn't match Email field.");
    			}
    		}
    	} else if( field.equals(DnComponents.RFC822NAME) && isRequired(field,number) && getUse(field,number) ) {
    		if (!data.trim().equals(email.trim())) {
    			throw new UserDoesntFullfillEndEntityProfile("Field " + text + " data didn't match Email field.");
    		}
    	} else {
    		if (!isModifyable(field,number)) {
    			String[] values;
    			try {
    				values = getValue(field, number).split(SPLITCHAR);
    			} catch (Exception e) {
    				throw new UserDoesntFullfillEndEntityProfile("Error parsing end entity profile.");
    			}
    			boolean exists = false;
    			for (final String value : values) {
    				if(data.equals(value.trim())) {
    					exists = true;
    				}
    			}
    			if (!exists) {
    				throw new UserDoesntFullfillEndEntityProfile("Field " + text + " data didn't match requirement of end entity profile.");
    			}
    		}
    	}
    }

    private void checkIfAllRequiredFieldsExists(final DNFieldExtractor subjectdnfields, final DNFieldExtractor subjectaltnames, final DNFieldExtractor subjectdirattrs, final String username, final String email)
    throws UserDoesntFullfillEndEntityProfile {
    	// Check if Username exists.
    	if (isRequired(USERNAME,0)) {
    		if (username == null || username.trim().isEmpty()) {
    			throw new UserDoesntFullfillEndEntityProfile("Username cannot be empty or null.");
    		}
    	}
    	// Check if required Email fields exists.
    	if (isRequired(EMAIL,0)) {
    		if (email == null || email.trim().isEmpty()) {
    			throw new UserDoesntFullfillEndEntityProfile("Email address cannot be empty or null.");
    		}
    	}
    	// Check if all required subjectdn fields exists.
    	final List<String> dnfields = DnComponents.getDnProfileFields();
    	final List<Integer> dnFieldExtractorIds = DnComponents.getDnDnIds();
    	for (int i = 0; i<dnfields.size(); i++) {
    		final String currentDnField = dnfields.get(i);
    		if (getReverseFieldChecks()) {
    			final int nof = subjectdnfields.getNumberOfFields(dnFieldExtractorIds.get(i).intValue());
    			final int numRequiredFields = getNumberOfRequiredFields(currentDnField);
    			if (nof < numRequiredFields) {
    				throw new UserDoesntFullfillEndEntityProfile("Subject DN field '" + currentDnField + "' must exist.");
    			}
    		} else {
    			final int size = getNumberOfField(currentDnField);
    			for (int j = 0; j<size; j++) {
    				if (isRequired(currentDnField,j)) {
    					if (subjectdnfields.getField(dnFieldExtractorIds.get(i).intValue(),j).trim().isEmpty()) {
    						throw new UserDoesntFullfillEndEntityProfile("Subject DN field '" + currentDnField + "' must exist.");
    					}
    				}
    			}
    		}
    	}
    	// Check if all required subject alternate name fields exists.
    	final List<String> altnamefields = DnComponents.getAltNameFields();
    	final List<Integer> altNameFieldExtractorIds = DnComponents.getAltNameDnIds();
    	for (int i=0; i<altnamefields.size(); i++) {
    		final String currentAnField = altnamefields.get(i);
    		if (getReverseFieldChecks()) {
    			final int nof = subjectaltnames.getNumberOfFields(altNameFieldExtractorIds.get(i).intValue());
    			final int numRequiredFields = getNumberOfRequiredFields(currentAnField);
    			if (nof < numRequiredFields) {
    				throw new UserDoesntFullfillEndEntityProfile("Subject Alternative Name field '" + currentAnField + "' must exist.");
    			}
    		} else {
    			// Only verify fields that are actually used
    			// size = getNumberOfField(altnamefields[i]);
    			final int size = subjectaltnames.getNumberOfFields(altNameFieldExtractorIds.get(i).intValue());
    			for (int j = 0; j < size; j++) {
    				if (isRequired(currentAnField,j)) {
    					if (subjectaltnames.getField(altNameFieldExtractorIds.get(i).intValue(),j).trim().isEmpty()) {
    						throw new UserDoesntFullfillEndEntityProfile("Subject Alterntive Name field '" + currentAnField + "' must exist.");
    					}
    				}
    			}
    		}
    	}
    	// Check if all required subject directory attribute fields exists.
    	final List<String> dirattrfields = DnComponents.getDirAttrFields();
    	final List<Integer> dirAttrFieldExtractorIds = DnComponents.getDirAttrDnIds();
    	for (int i = 0; i < dirattrfields.size(); i++) {        	
    		final String currentDaField = dirattrfields.get(i);
    		final int size = getNumberOfField(currentDaField);
    		for (int j = 0; j < size; j++) {
    			if (isRequired(currentDaField,j)) {
    				if (subjectdirattrs.getField(dirAttrFieldExtractorIds.get(i).intValue(),j).trim().isEmpty()) {
    					throw new UserDoesntFullfillEndEntityProfile("Subject Directory Attribute field '" + currentDaField + "' must exist.");
    				}
    			}
    		}
    	}
    }

    /**
     * Method calculating the number of required fields of on kind that is configured for this profile.
     * @param field, one of the field constants
     * @return The number of required fields of that kind.
     */
    private int getNumberOfRequiredFields(final String field) {
    	int retval = 0;
    	final int size = getNumberOfField(field);
    	for (int j = 0; j<size; j++) {
    		if (isRequired(field,j)) {
    			retval++;
    		}
    	}
    	return retval;
    }

    private void checkIfForIllegalNumberOfFields(final DNFieldExtractor subjectdnfields, final DNFieldExtractor subjectaltnames, final DNFieldExtractor subjectdirattrs) throws UserDoesntFullfillEndEntityProfile {
    	// Check number of subjectdn fields.
    	final List<String> dnfields = DnComponents.getDnProfileFields();
    	final List<Integer> dnFieldExtractorIds = DnComponents.getDnDnIds();
    	for (int i=0; i<dnfields.size(); i++) {
    		if (getNumberOfField(dnfields.get(i)) < subjectdnfields.getNumberOfFields(dnFieldExtractorIds.get(i).intValue())) {
    			throw new UserDoesntFullfillEndEntityProfile("Wrong number of " + dnfields.get(i) + " fields in Subject DN.");
    		}
    	}
    	// Check number of subject alternate name fields.
    	final List<String> altnamefields = DnComponents.getAltNameFields();
    	final List<Integer> altNameFieldExtractorIds = DnComponents.getAltNameDnIds();
    	for (int i=0; i<altnamefields.size(); i++) {
    		if (getNumberOfField(altnamefields.get(i)) < subjectaltnames.getNumberOfFields(altNameFieldExtractorIds.get(i).intValue())) {
    			throw new UserDoesntFullfillEndEntityProfile("Wrong number of " + altnamefields.get(i) + " fields in Subject Alternative Name.");
    		}
    	}
    	// Check number of subject directory attribute fields.
    	final List<String> dirattrfields = DnComponents.getDirAttrFields();
    	final List<Integer> dirAttrFieldExtractorIds = DnComponents.getDirAttrDnIds();
    	for (int i=0; i<dirattrfields.size(); i++) {
    		if (getNumberOfField(dirattrfields.get(i)) < subjectdirattrs.getNumberOfFields(dirAttrFieldExtractorIds.get(i).intValue())) {
    			throw new UserDoesntFullfillEndEntityProfile("Wrong number of " + dirattrfields.get(i) + " fields in Subject Directory Attributes.");
    		}
    	}
    }

	/** methods for mapping the DN, AltName, DirAttr constants from string->number */
	private static int getParameterNumber(final String parameter) {
		final Integer number = dataConstants.get(parameter);
		if (number != null) {
			return number.intValue();			
		}
		log.error("No parameter number for "+parameter);
		return -1;
	}

	/** methods for mapping the DN, AltName, DirAttr constants from number->string */
	private static String getParameter(final int parameterNumber) {
		String ret = null;
		for (final Entry<String, Integer> entry : dataConstants.entrySet()) {
			if (entry.getValue().intValue() == parameterNumber) {
				ret = entry.getKey();
				break;
			}
		}
		if (ret == null) {
			log.error("No parameter for "+parameterNumber);			
		}
		return ret;
	}
	
    private void incrementFieldnumber(final int parameter){
    	final ArrayList<Integer> numberarray = (ArrayList<Integer>) data.get(NUMBERARRAY);
    	numberarray.set(parameter, Integer.valueOf(numberarray.get(parameter).intValue() + 1));
    }

    private void decrementFieldnumber(final int parameter){
    	final ArrayList<Integer> numberarray = (ArrayList<Integer>) data.get(NUMBERARRAY);
    	numberarray.set(parameter, Integer.valueOf(numberarray.get(parameter).intValue() - 1));
    }

    public static String[] getSubjectDNProfileFields() {
    	return (String[])DnComponents.getDnProfileFields().toArray(new String[0]);
    }

    public static String[] getSubjectAltnameProfileFields() {
    	return (String[])DnComponents.getAltNameFields().toArray(new String[0]);
    }

    public static String[] getSubjectDirAttrProfileFields() {
    	return (String[])DnComponents.getDirAttrFields().toArray(new String[0]);
    }
    
    /**
     * @return true if it should be possible to add extension data in the GUI.
     */
    public boolean getUseExtensiondata(){
    	return getValueDefaultFalse(USEEXTENSIONDATA);
    }

    public void setUseExtensiondata(final boolean use){
    	data.put(USEEXTENSIONDATA, Boolean.valueOf(use));
    }
}
