<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>

<%
    String jsonString = request.getParameter("jsonString");
    String aclFilterInfos = "";
    // aclFilterInfos = "admin@UR|k, S000@PR|k"; // 개발 테스트용, 운영반영시 주석 처리

    // 그룹웨어에서 넘어오는 값
    ObjectMapper mapper = new ObjectMapper();
    if(jsonString != null) {
        Map<String, String> jsonStringMap = mapper.readValue(jsonString, Map.class);
        // schKwd = jsonStringMap.get("query");
        aclFilterInfos = jsonStringMap.get("aclFilterInfos");
    }
    // if( schKwd == null )            schKwd = "";
    if( aclFilterInfos == null )    aclFilterInfos = "";
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>호반건설 - 검색화면</title>
    <link rel="stylesheet" href="css/common.css">
    <link rel="icon" href="img/favicon.ico">
</head>
<body>
    <form name="fm" method="post" action="./gsch.jsp">
    <div class="hobanS">
        <div class="hobanS_intro">
            <h1><img src="img/logo.png" alt=""></h1>
            <div class="searchBar">
                <input type="text" name="sch_kwd" required />
                <input type="hidden" name="aclFilterInfos" value="<%= aclFilterInfos %>" />
                <button type="submit"><img src="img/search.png" alt="검색"></button>
            </div>
            <p style="margin: 20px; position: relative; top: 110%; left: 18%; color:#999; font-family: emoji;">
                띄어쓰기는 and 검색, | 는 or 검색, !는 not 검색 <br><br> 예시 : 홍길동 호반, 홍길동 | 호반, 홍길동 !호반
            </p>
        </div>
    </div>
    </form>
</body>
</html>
