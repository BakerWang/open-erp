<%@ page import="com.skysport.core.model.init.helper.SystemInitHelper" %>
<%
    String path = request.getContextPath();
    String version = SystemInitHelper.SINGLETONE.getVersion();
    String environment_current = SystemInitHelper.SINGLETONE.getEnvironment();
%>