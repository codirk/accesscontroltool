/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.cq.tools.actool.dumpservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import biz.netcentric.cq.tools.actool.authorizableutils.AuthorizableConfigBean;
import biz.netcentric.cq.tools.actool.helper.AceBean;
import biz.netcentric.cq.tools.actool.helper.AclBean;

public interface Dumpservice {

    public Set<AuthorizableConfigBean> getGroupBeans(Session session)
            throws AccessDeniedException,
            UnsupportedRepositoryOperationException, RepositoryException;

    public boolean isIncludeUsers();

    public boolean isShowLegacyAces();

    /**
     * returns the paths under jcr:root witch are excluded from search for
     * rep:policy nodes in OSGi configuration
     * 
     * @return String array containing the paths
     */
    public String[] getQueryExcludePaths();

    /**
     * 
     * @param aceMap
     * @param authorizableSet
     * @param mapOrder
     * @param serverUrl
     * @return
     * @throws IOException
     */
    public String getConfigurationDumpAsString(final AceDumpData aceDumpData,
            final Set<AuthorizableConfigBean> groupSet,
            final Set<AuthorizableConfigBean> userSet, final int mapOrder,
            final String serverUrl) throws IOException;

    /**
     * 
     * @param sb
     * @param aceMap
     * @param mapOrder
     * @return
     * @throws IOException
     */
    // public StringBuilder getAceDumpAsString(final StringBuilder sb, final
    // Map<String, Set<AceBean>> aceMap, final int mapOrder) throws IOException;

    /**
     * 
     * @return
     */
    public String getDumplLinks();

    /**
     * 
     * @param session
     * @return
     * @throws RepositoryException
     */
    public Set<AclBean> getACLDumpBeans(final Session session)
            throws RepositoryException;

    /**
     * returns a Map with holds either principal or path based ACE data
     * 
     * @param request
     * @param keyOrder
     *            either principals (AceHelper.PRINCIPAL_BASED_ORDERING) or node
     *            paths (AceHelper.PATH_BASED_ORDERING) as keys
     * @param aclOrdering
     *            specifies whether the allow and deny ACEs within an ACL should
     *            be divided in separate blocks (first deny then allow)
     * @return
     * @throws ValueFormatException
     * @throws IllegalStateException
     * @throws RepositoryException
     */
    public AceDumpData createFilteredAclDumpMap(final Session session,
            final int keyOrder, final int aclOrdering,
            final String[] excludePaths) throws ValueFormatException,
            IllegalArgumentException, IllegalStateException,
            RepositoryException;

    public AceDumpData createUnfilteredAclDumpMap(final Session session,
            final int keyOrder, final int aclOrdering,
            final String[] excludePaths) throws ValueFormatException,
            IllegalArgumentException, IllegalStateException,
            RepositoryException;

    // public Map <String, Set<AceBean>> createAclDumpMap(final Session session,
    // final int keyOrder, final int aclOrdering, final String[] excludePaths,
    // final boolean isFilterACEs) throws ValueFormatException,
    // IllegalArgumentException, IllegalStateException, RepositoryException;

    /**
     * method that return a dump comprising of all groups and all aces in path
     * based view
     * 
     * @return
     */
    public String getCompletePathBasedDumpsAsString();

    /**
     * method that return a dump comprising of all groups and all aces in
     * principal based view
     * 
     * @return
     */
    public String getCompletePrincipalBasedDumpsAsString();

    /**
     * returns a dump of the ACEs installed in the system using a PrintWriter.
     * 
     * @param out
     *            PrintWriter
     * @param aceMap
     *            map containing all ACE data, either path based or group based
     * @param mapOrdering
     * @param aceOrdering
     */
    public void returnAceDump(final PrintWriter out,
            Map<String, Set<AceBean>> aceMap, final int mapOrdering,
            final int aceOrdering);

    /**
     * method that returns a dump comprising aces as file
     * 
     * @param response
     * @param aceMap
     * @param authorizableSet
     * @param session
     * @param mapOrder
     * @param aceOrder
     * @throws IOException
     */

    public void returnAceDumpAsFile(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response, final Session session,
            final int mapOrder, final int aceOrder);

    /**
     * 
     * @param response
     * @param aceMap
     * @param authorizableSet
     * @param session
     * @param mapOrder
     * @param aceOrder
     * @throws IOException
     */
    public void returnCompleteDumpAsFile(
            final SlingHttpServletResponse response,
            final Map<String, Set<AceBean>> aceMap,
            final Set<AuthorizableConfigBean> authorizableSet,
            final Session session, final int mapOrder, final int aceOrder)
            throws IOException;

    /**
     * 
     * @param response
     * @param aceMap
     * @param mapOrder
     * @throws IOException
     */
    public void returnAceDumpAsFile(final SlingHttpServletResponse response,
            final Map<String, Set<AceBean>> aceMap, final int mapOrder)
            throws IOException;

    /**
     * 
     * @param response
     * @param aceMap
     * @param authorizableSet
     * @param mapOrder
     * @throws IOException
     */
    public void returnConfigurationDumpAsFile(
            final SlingHttpServletResponse response,
            final Map<String, Set<AceBean>> aceMap,
            final Set<AuthorizableConfigBean> authorizableSet,
            final int mapOrder) throws IOException;

}
