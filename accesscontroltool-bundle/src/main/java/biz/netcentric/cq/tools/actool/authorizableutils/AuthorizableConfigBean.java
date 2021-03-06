/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.cq.tools.actool.authorizableutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import biz.netcentric.cq.tools.actool.dumpservice.AcDumpElement;
import biz.netcentric.cq.tools.actool.dumpservice.AcDumpElementVisitor;

public class AuthorizableConfigBean implements AcDumpElement {

    private String principalID;
    private String principalName;

    private String[] memberOf;
    String memberOfStringFromConfig;

    private String[] members;
    String membersStringFromConfig;

    private String description;
    private String path;
    private String password;
    private boolean isGroup = true;
    private String assertedExceptionString = null;

    public String getAssertedExceptionString() {
        return assertedExceptionString;
    }

    public void setAssertedExceptionString(final String assertedException) {
        this.assertedExceptionString = assertedException;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setMemberOfString(final String memberOfString) {
        this.memberOfStringFromConfig = memberOfString;
    }

    public void setMembersString(final String membersString) {
        this.membersStringFromConfig = membersString;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(final boolean isGroup) {
        this.isGroup = isGroup;
    }

    public void memberOf(final boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setAuthorizableName(final String principalName) {
        this.principalName = principalName;
    }

    public String getPrincipalID() {
        return principalID;
    }

    public void setPrincipalID(final String principalID) {
        this.principalID = principalID;
    }

    public String[] getMemberOf() {
        return memberOf;
    }

    public boolean isMemberOfOtherGroups() {
        return memberOf != null;
    }

    public String getMemberOfStringFromConfig() {
        return this.memberOfStringFromConfig;
    }

    public String getMemberOfString() {
        if (memberOf == null) {
            return "";
        }

        final StringBuilder memberOfString = new StringBuilder();

        for (final String group : memberOf) {
            memberOfString.append(group).append(",");
        }
        return StringUtils.chop(memberOfString.toString());
    }

    public void setMemberOf(final String[] memberOf) {
        this.memberOf = memberOf;
    }

    public void setMemberOf(final List<String> memberOf) {
        if (memberOf != null && !memberOf.isEmpty()) {
            this.memberOf = memberOf.toArray(new String[memberOf.size()]);
        }
    }

    public void addMemberOf(final String member) {
        if (memberOf == null) {
            memberOf = new String[] { member };
            return;
        }
        final List<String> memberList = new ArrayList<String>();
        memberList.addAll(Arrays.asList(memberOf));
        if (!memberList.contains(member)) {
            memberList.add(member);
            memberOf = memberList.toArray(new String[memberList.size()]);
        }
    }

    public String getMembersStringFromConfig() {
        return this.membersStringFromConfig;
    }

    public String getMembersString() {
        if (members == null) {
            return "";
        }

        final StringBuilder membersString = new StringBuilder();

        for (final String group : members) {
            membersString.append(group).append(",");
        }
        return StringUtils.chop(membersString.toString());
    }

    public String[] getMembers() {
        return members;
    }

    public void setMembers(final String[] members) {
        this.members = members;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n" + "id: " + this.principalID + "\n");
        sb.append("name: " + this.principalName + "\n");
        sb.append("description: " + this.description + "\n");
        sb.append("path: " + this.path + "\n");
        sb.append("memberOf: " + this.getMemberOfString() + "\n");
        sb.append("members: " + this.getMembersString() + "\n");
        return sb.toString();
    }

    @Override
    public void accept(final AcDumpElementVisitor acDumpElementVisitor) {
        acDumpElementVisitor.visit(this);
    }
}
