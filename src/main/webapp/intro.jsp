<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>호반건설 - 검색화면1</title>
    <link rel="stylesheet" href="css/common.css">
</head>
<body>
    <form name="fm" method="post" action="./gsch.jsp">
    <div class="hobanS">
        <div class="hobanS_intro">
            <h1><img src="img/logo.png" alt=""></h1>
            <div class="searchBar">
                <input type="text" name="sch_kwd" required />
                <button type="submit"><img src="img/search.png" alt="검색"></button>
            </div>
        </div>
    </div>
    </form>
</body>
</html>