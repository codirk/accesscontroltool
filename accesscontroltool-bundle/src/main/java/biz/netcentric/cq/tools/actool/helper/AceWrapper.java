/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.cq.tools.actool.helper;

import java.security.Principal;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.netcentric.cq.tools.actool.configuration.CqActionsMapping;

/**
 * class that wraps an javax.jcr.security.AccessControlEntry and stores an
 * additional path information Also provides some getter methods which return
 * ACE data. Created and used during the reading of ACEs from a system, in order
 * to create a ACE Dump
 * 
 * @author jochenkoschorke
 *
 */
public class AceWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(AceWrapper.class);
    private static final String PERM_DENY = "deny";
    private static final String PERM_ALLOW = "allow";
    private static final String REP_GLOB_RESTRICTION = "rep:glob";
    private AccessControlEntry ace;
    private String jcrPath;

    public AceWrapper(AccessControlEntry ace, String jcrPath) {
        super();
        this.ace = ace;
        this.jcrPath = jcrPath;
    }

    public AccessControlEntry getAce() {
        return ace;
    }

    public void setAce(AccessControlEntry ace) {
        this.ace = ace;
    }

    public String getJcrPath() {
        return jcrPath;
    }

    public Principal getPrincipal() {
        return this.ace.getPrincipal();
    }

    public void setJcrPath(String jcrPath) {
        this.jcrPath = jcrPath;
    }

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return this.ace.toString();
    }

    public Privilege[] getPrivileges() {
        return ace.getPrivileges();
    }

    public boolean isAllow() {
        return ((JackrabbitAccessControlEntry) this.ace).isAllow();
    }

    public Value getRestrictionAsValue(String name) throws RepositoryException {
        return ((JackrabbitAccessControlEntry) ace).getRestriction(name);
    }

    public String getRestrictionAsString(String name)
            throws RepositoryException {
        Value val = ((JackrabbitAccessControlEntry) ace).getRestriction(name);
        if (val != null) {
            return ((JackrabbitAccessControlEntry) ace).getRestriction(name)
                    .getString();
        }
        // fix for #11: All ACE from groups not contained in a config get an empty repGlob after installation
        return null;
    }

    public String getPrivilegesString() {
        Privilege[] privileges = this.ace.getPrivileges();
        String privilegesString = "";
        for (Privilege privilege : privileges) {
            privilegesString = privilegesString + privilege.getName() + ",";
        }
        privilegesString = StringUtils.chop(privilegesString);
        return privilegesString;
    }


    public int hashCode() {
        return this.ace.hashCode();
    }

}
