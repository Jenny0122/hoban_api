<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%
    String schKwd = request.getParameter("sch_kwd"); // 검색어
    String aclFilterInfos = request.getParameter("aclFilterInfos");;
	
	// 세션에 aclFilterInfos 저장
    if(aclFilterInfos != null && !aclFilterInfos.isEmpty()) {
        session.setAttribute("aclFilterInfos", aclFilterInfos);
    } else {
        // 세션에서 기존의 aclFilterInfos를 가져옴
        aclFilterInfos = (String) session.getAttribute("aclFilterInfos");
        if (aclFilterInfos == null) aclFilterInfos = "";
    }
    // aclFilterInfos = "0OxadYAA1t5@UR|z"; // 개발 테스트용, 운영반영시 주석 처리
	
    String errorMessage = "";
	
    // 문서중앙화, 그룹웨어에서 넘어오는 값 모두 jsonString 형태로 받기
    String jsonString = request.getParameter("jsonString");
    Map<String, String> jsonStringMap = new HashMap<>();
    if(jsonString != null && !jsonString.contentEquals("null") && !jsonString.contentEquals("")) {

        ObjectMapper mapper = new ObjectMapper();

        try {
            jsonStringMap = mapper.readValue(jsonString, Map.class);

            // 문서중앙화에서 넘어오는 검색어 처리
            if(schKwd == null)
                schKwd = jsonStringMap.get("query");

            // 그룹웨어, 문서중앙화에서 넘어오는 권한값 동시 처리
            aclFilterInfos = jsonStringMap.getOrDefault("aclFilterInfos", "");

        } catch(Exception e) {                
            errorMessage = e.getMessage();
        }
    }

    if( schKwd == null )			schKwd = "";
    if( aclFilterInfos == null )	aclFilterInfos = "";

%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>호반건설 - 일반검색</title>
    <link rel="stylesheet" href="css/font/pretendard.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="icon" href="img/favicon.ico">
    <script src="js/jquery-1.12.3.js"></script>
    <script src="js/common.js"></script>
    <!--sweetalert-->
    <link rel="stylesheet" href="css/sweetalert2.min.css">
    <script src="js/sweetalert2.all.min.js"></script>
    <script>
        var jsonString = <%=jsonString %>
        var pageFileTot = 0; // 파일 전체페이지
        var pageFileNum = 0; // 파일 현재페이지
        var pageFolderTot = 0; // 폴더 전체페이지
        var pageFolderNum = 0; // 폴더 현재페이지
        var schTotal = 0; // 총 건수
        var schFileTotal = 0; // 파일 총 건수
        var schFolderTotal = 0; // 폴더 총 건수

        $(document).ready(function(){
            // 파일 상세검색에서 라디오 버튼 선택시
            $("input[name=term_dvs]").click(function(){
                var term_dvs = $("input[name=term_dvs]:checked").val();
                if( term_dvs == "C" ){ // 사용자 정의 선택
                    $("#s_date").prop("disabled", false);
                    $("#e_date").prop("disabled", false);
                }else{
                    $("#s_date").val("");
                    $("#e_date").val("");
                    $("#s_date").prop("disabled", true);
                    $("#e_date").prop("disabled", true);
                }
            })
            // 폴더 상세검색에서 라디오 버튼 선택시
            $("input[name=term_dvs2]").click(function(){
                var term_dvs = $("input[name=term_dvs2]:checked").val();
                if( term_dvs == "C" ){ // 사용자 정의 선택
                    $("#s_date2").prop("disabled", false);
                    $("#e_date2").prop("disabled", false);
                }else{
                    $("#s_date2").val("");
                    $("#e_date2").val("");
                    $("#s_date2").prop("disabled", true);
                    $("#e_date2").prop("disabled", true);
                }
            })
            // 정렬 선택 시
            $("input[name=list_sort]").click(function(){
                fileJson(); // 파일
                folderJson(); // 폴더
            })
        })

        $(window).load(function(){ // 페이지 로딩 후
			// 문서유형 가져오기
            $.ajax({
                url : '/doctypes',
                data : null,
                type : 'GET',
                dataType: 'JSON',
                contentType: 'application/json; charset=utf-8'
            }).done(function(resultData) {
                //console.log(resultData);
                //console.log(resultData.length); // 문서유형 갯수
                var dovTypeRadinHtml = "";
                var dovTypeRadinHtml2 = "";
                for( i = 0; i < resultData.length; i++ )
                {
                	var active = "";
                	if( i == 0 ) active = "selected";
                	//dovTypeRadinHtml += '<li><input type="radio" name="doctype_dvs" id="doctype_'+(i+1)+'" value="'+resultData[i].oid+'" '+active+' /><label for="doctype_'+(i+1)+'">'+resultData[i].name+'</label></li>';
                	//dovTypeRadinHtml2 += '<li><input type="radio" name="doctype_dvs2" id="doctype2_'+(i+1)+'" value="'+resultData[i].oid+'" '+active+' /><label for="doctype2_'+(i+1)+'">'+resultData[i].name+'</label></li>';
                	dovTypeRadinHtml += '<option value="'+resultData[i].oid+'" '+active+'>'+resultData[i].name+'</option>';
                }
				$("#radio-doc-type-wrap").html(dovTypeRadinHtml); // 파일 상세검색
				$("#radio-doc-type-wrap2").html(dovTypeRadinHtml); // 폴더 상세검색

	            fileJson(); // 파일
	            folderJson(); // 폴더

            }).fail(function(xhr, status, errorThrown) {
                console.log("API FILE DATA ERROR");
            });
        })

        // 총 카운터 갯수 정의
        function getTotalCountSum()
        {
            $("#sch-total").html(schFileTotal + schFolderTotal);
        }

        // 날짜 포맷
        function getDateStr(myDate){
            var year = myDate.getFullYear();
            var month = (myDate.getMonth() + 1);
            var day = myDate.getDate();
            month = (month < 10) ? "0" + String(month) : month;
            day = (day < 10) ? "0" + String(day) : day;
            return  year + month + day;
        }

        // 클립보드 복사
        function copyToClipBoard(copyTxt)
        {
            var copyTxt2 = "S:\\";
            copyTxt2 += copyTxt.replaceAll('>','\\');
            $("#copy_txt").val(copyTxt2);
            var content = document.getElementById('copy_txt');
            content.select();
            document.execCommand('copy');
            //alert("폴더경로가 클립보드에 복사되었습니다.");
			Swal.fire({
				position: "top-end",
				icon: "success",
				title: "폴더경로가 클립보드에 복사",
				text: "윈도우탐색기 주소창에 붙여넣기하시면, 해당경로로 바로 이동됩니다.",
				showConfirmButton: false,
				timer: 1050
			});
            $("#copy_txt").val("");
        }

        function fileOpen(oid) // 파일보기
        {
            if( oid != "" ){
                // var theURL = "https://ecmdev.e-hoban.co.kr/url/"; // 개발
                var theURL = "https://ecm.ihoban.co.kr/url/"; // 운영
                theURL += "?fileOID="+oid;
                theURL += "&urlType=B";
                //console.log(theURL);
                window.open(theURL);
            }
        }

        function documentOpen(oid) // 파일 속성보기
        {
            if( oid != "" ){
                // var theURL = "https://ecmdev.e-hoban.co.kr/url/"; // 개발
                var theURL = "https://ecm.ihoban.co.kr/url/"; // 운영
                theURL += "?fileOID="+oid;
                theURL += "&urlType=A";
                //console.log(theURL);
                window.open(theURL);
            }
        }

        function fileJson(clickPageNum='') // 파일
        {
            // 상세검색 정의
            var term_dvs = $("input[name=term_dvs]:checked").val();
            //var doctype_dvs = $("input[name=doctype_dvs]:checked").val();
            var doctype_dvs = $("#radio-doc-type-wrap option:selected").val();
            var s_date = $("#s_date").val();
            var e_date = $("#e_date").val();
            if( term_dvs == "D" ){ // 1일(오늘)
                var d = new Date();
                s_date = getDateStr(d);
                e_date = s_date;
            }else if( term_dvs == "W" ){ // 1주일
                var d = new Date();
                e_date = getDateStr(d); // 종료일은 오늘
                var dayOfMonth = d.getDate();
                d.setDate(dayOfMonth - 7);
                s_date = getDateStr(d); // 시작일은 1주일전
            }else if( term_dvs == "M" ){ // 1개월
                var d = new Date();
                e_date = getDateStr(d); // 종료일은 오늘
                var monthOfYear = d.getMonth();
                d.setMonth(monthOfYear - 1);
                s_date = getDateStr(d); // 시작일은 1개월전
            }else if( term_dvs == "C" ){ // 사용자 정의
                if( s_date == "" ){
                    //alert("시작일을 선택해주세요!");
                    Swal.fire({text: "시작일을 선택해주세요!",icon: "warning"});
                    return false;
                }
                if( e_date == "" ){
                    //alert("종료일을 선택해주세요!");
                    Swal.fire({text: "종료일을 선택해주세요!",icon: "warning"});
                    return false;
                }
            }

            if( clickPageNum != "" ){
                pageFileNum = clickPageNum - 1;
            }else{
                pageFileNum = 0;
            }
            var paramData = {};
            paramData.searchTargetOID = "ALL";
            paramData.query = "<%=schKwd %>";
            paramData.searchTargetOID = "fileinfo"; // folderinfo, fileinfo
            paramData.aclFilterInfos = "<%= aclFilterInfos %>";

            // 상세검색으로 카운터 정의
            if( s_date != "" && e_date != "" ){ // 검색기간이 있으면(최종수정일자)
                s_date = s_date.replaceAll("-","");
                e_date = e_date.replaceAll("-","");
                paramData.modifyFrom = s_date;
                paramData.modifyTo = e_date;
            }
            paramData.pageStart = pageFileNum;
            paramData.doctype = doctype_dvs; // 문서유형
            // 정렬 정의
            var list_sort = $("input[name=list_sort]:checked").val();
            if( list_sort == "R" ){ // 정확도순 DESC
                paramData.sortColumnIndex = "RANK";
                paramData.sortDirection = "DESC";
            }else if( list_sort == "D" ){ // 날짜순 DESC
                paramData.sortColumnIndex = "DATE";
                paramData.sortDirection = "DESC";
            }else if( list_sort == "T" ){ // 제목순(파일명순) ASC
                paramData.sortColumnIndex = "FILENAME";
                paramData.sortDirection = "ASC";
            }

            $.ajax({
                url : '/search',
                data : JSON.stringify(paramData),
                type : 'POST',
                dataType: 'JSON',
                async: true,
                contentType: 'application/json; charset=utf-8',
                beforeSend: function(xhr){
                    document.body.style.cursor = 'wait';
                },
                complete: function(){
                    document.body.style.cursor = 'auto';
                }
            }).done(function(resultData) {
                //console.log("FAIL => ");
                //console.log(resultData);
                var apiDataArr = resultData.data[1]; // fileinfo
                pageFileTot = parseInt(apiDataArr.totalCount) / 10;
                pageFile(); // 페이징 정의

                schFileTotal = parseInt(apiDataArr.totalCount); // 파일 count
                $("#sch-file-total").html(schFileTotal);

                // 파일
                var fileHtml = "";
                var fileNo = (pageFileNum * 10) + 1;
                for( i = 0; i < apiDataArr.result.length; i++ )
                {
                    if( fileNo < 10 ){
                        var fileNoTxt = "0"+fileNo;
                    }else{
                        var fileNoTxt = fileNo;
                    }
                    var list_data = apiDataArr.result[i];
                    var regDate = "";
                    if( list_data['createdat'] != "" ){
                        regDate = list_data['createdat'].substr(0,10);
                        regDate = regDate.replaceAll("-",".");
                    }
                    //if( list_data['lastmodifiedat'] != "" ){ // 등록일 일단 최종수정일로 처리
                    //   regDate = list_data['lastmodifiedat'].substr(0,10);
                    //    regDate = regDate.replaceAll("-",".");
                    //}
                    var modDate = "";
                    if( list_data['lastmodifiedat'] != "" ){
                        modDate = list_data['lastmodifiedat'].substr(0,10);
                        modDate = modDate.replaceAll("-",".");
                    }
                    fileHtml += '<div class="contents">';
                    fileHtml += '<div class="tit">';
                    fileHtml += '<div class="title" onclick="fileOpen(\''+list_data['oid']+'\');" style="cursor:pointer">'+fileNoTxt+'. '+list_data['filename']+'';
                    fileHtml += '<button style="margin:0 0 0 10px;background-color:#fff"><img src="img/folder.png" alt="파일속성보기" onclick="documentOpen(\''+list_data['oid']+'\');"></button>';
                    fileHtml += '</div>';
                    fileHtml += '<div class="information">';
                    fileHtml += '<dl>';
                    fileHtml += '<dt class="hobanOrange">등록자</dt>';
                    fileHtml += '<dd>'+list_data['creatorname']+'</dd>';
                    fileHtml += '<dt class="hobanGray">등록일</dt>';
                    fileHtml += '<dd>'+regDate+'</dd>';
                    fileHtml += '<dt>최종 수정일</dt>';
                    fileHtml += '<dd>'+modDate+'</dd>';
                    fileHtml += '</dl>';
                    fileHtml += '</div>';
                    fileHtml += '</div>';
                    fileHtml += '<div class="summary" onclick="">'+list_data['content']+'</div>';
                    fileHtml += '<div class="location">';
                    fileHtml += list_data['folderfullpathname'];
                    //fileHtml += '<button><img src="img/folder.png" alt="폴더경로복사" onclick="copyToClipBoard(\''+list_data['folderfullpathname']+'\');"></button>';
                    fileHtml += '<button><img src="img/copy.png" alt="파일경로복사" onclick="copyToClipBoard(\''+list_data['folderfullpathname']+'\');"></button>';
                    fileHtml += '</div>';
                    fileHtml += '</div>';
                    fileNo++;
                }
                // 검색 데이터가 없으면
                if( fileHtml == "" ) fileHtml = '<div class="no-data">검색된 정보가 없습니다.</div>';
                $("#file-content-wrap").html(fileHtml);

                // 총 카운터 갯수 정의
                getTotalCountSum();

            }).fail(function(xhr, status, errorThrown) {
                console.log("API FILE DATA ERROR");
            });
        }

        function pageFile()
        {
            var pageHtml = "";
            pageFileTot = Math.ceil(pageFileTot); // 올림
            var curPage = pageFileNum + 1; // 현재 페이지
            if( curPage > 1 ){
                pageHtml += '<a href="#" class="arrow" onclick="fileJson(\'1\');"><img src="img/first.png" alt="처음페이지"></a>';
            }
            var pageS = ( parseInt((curPage - 1) / 10) * 10 ) + 1;
            var pageE = pageS + 10 - 1;
            if( pageE >= pageFileTot ) pageE = pageFileTot;
            if( pageS > 1 ){
                pageHtml += '<a href="#" class="arrow" onclick="fileJson(\''+(pageS-1)+'\');"><img src="img/prev.png" alt="이전페이지"></a>';
            }
            if (pageFileTot > 1) {
                for (k=pageS;k<=pageE;k++) {
                    if (curPage != k){
                        pageHtml += '<a href="#" onclick="fileJson(\''+k+'\');">'+k+'</a>';
                    }else{
                        pageHtml += '<a href="#" class="active" onclick="fileJson(\''+k+'\');">'+k+'</a>';
                    }
                }
            }
            if (pageFileTot > pageE){
                pageHtml += '<a href="#" class="arrow" onclick="fileJson(\''+(pageE+1)+'\');"><img src="img/next.png" alt="다음페이지"></a>';
            }
            if (curPage < pageFileTot) {
                pageHtml += '<a href="#" class="arrow" onclick="fileJson(\''+pageFileTot+'\');"><img src="img/last.png" alt="마지막페이지"></a>';
            }
            $("#page-file-wrap").html(pageHtml);
        }

        function folderJson(clickPageNum='') // 폴더
        {
            // 상세검색 정의
            var term_dvs = $("input[name=term_dvs2]:checked").val();
            //var doctype_dvs = $("input[name=doctype_dvs2]:checked").val();
            var doctype_dvs = $("#radio-doc-type-wrap2 option:selected").val();
            var s_date = $("#s_date2").val();
            var e_date = $("#e_date2").val();
            if( term_dvs == "D" ){ // 1일(오늘)
                var d = new Date();
                s_date = getDateStr(d);
                e_date = s_date;
            }else if( term_dvs == "W" ){ // 1주일
                var d = new Date();
                e_date = getDateStr(d); // 종료일은 오늘
                var dayOfMonth = d.getDate();
                d.setDate(dayOfMonth - 7);
                s_date = getDateStr(d); // 시작일은 1주일전
            }else if( term_dvs == "M" ){ // 1개월
                var d = new Date();
                e_date = getDateStr(d); // 종료일은 오늘
                var monthOfYear = d.getMonth();
                d.setMonth(monthOfYear - 1);
                s_date = getDateStr(d); // 시작일은 1개월전
            }else if( term_dvs == "C" ){ // 사용자 정의
                if( s_date == "" ){
                    //alert("시작일을 선택해주세요!");
                    Swal.fire({text: "시작일을 선택해주세요!",icon: "warning"});
                    return false;
                }
                if( e_date == "" ){
                    //alert("종료일을 선택해주세요!");
                    Swal.fire({text: "종료일을 선택해주세요!",icon: "warning"});
                    return false;
                }
            }

            if( clickPageNum != "" ){
                pageFolderNum = clickPageNum - 1;
            }else{
                pageFolderNum = 0;
            }

            var paramData = {};
            paramData.searchTargetOID = "ALL";
            paramData.query = "<%=schKwd %>";
            paramData.searchTargetOID = "folderinfo"; // folderinfo, fileinfo
            paramData.aclFilterInfos = "<%= aclFilterInfos %>";
            // 상세검색으로 카운터 정의
            if( s_date != "" && e_date != "" ){ // 검색기간이 있으면(최종수정일자)
                s_date = s_date.replaceAll("-","");
                e_date = e_date.replaceAll("-","");
                paramData.modifyFrom = s_date;
                paramData.modifyTo = e_date;
            }
            paramData.pageStart = pageFolderNum;
            paramData.doctype = doctype_dvs; // 문서유형
            // 정렬 정의
            var list_sort = $("input[name=list_sort]:checked").val();
            //console.log("list_sort => "+list_sort);
            if( list_sort == "R" ){ // 정확도순 DESC
                paramData.sortColumnIndex = "RANK";
                paramData.sortDirection = "DESC";
            }else if( list_sort == "D" ){ // 날짜순 DESC
                paramData.sortColumnIndex = "DATE";
                paramData.sortDirection = "DESC";
            }else if( list_sort == "T" ){ // 제목순(폴더명순) ASC
                paramData.sortColumnIndex = "NAME";
                paramData.sortDirection = "ASC";
            }

            $.ajax({
                url : '/search',
                data : JSON.stringify(paramData),
                type : 'POST',
                dataType: 'JSON',
                contentType: 'application/json; charset=utf-8'
            }).done(function(resultData) {
                //console.log("FOLDER => ");
                //console.log(resultData);
                var apiDataArr = resultData.data[0]; // fileinfo
                pageFolderTot = parseInt(apiDataArr.totalCount) / 10;
                pageFolder(); // 페이징 정의

                schFolderTotal = parseInt(apiDataArr.totalCount); // 폴더 count
                $("#sch-folder-total").html(schFolderTotal);

                // 폴더
                var folderHtml = "";
                var fileNo = (pageFolderNum * 10) + 1;
                for( i = 0; i < apiDataArr.result.length; i++ )
                {
                    if( fileNo < 10 ){
                        var fileNoTxt = "0"+fileNo;
                    }else{
                        var fileNoTxt = fileNo;
                    }
                    var list_data = apiDataArr.result[i];
                    var regDate = "";
                    if( list_data['createdat'] != "" ){
                        regDate = list_data['createdat'].substr(0,10);
                        regDate = regDate.replaceAll("-",".");
                    }
                    var modDate = "";
                    if( list_data['lastmodifiedat'] != "" ){
                        modDate = list_data['lastmodifiedat'].substr(0,10);
                        modDate = modDate.replaceAll("-",".");
                    }

                    folderHtml += '<div class="contents">';
                    folderHtml += '<div class="tit">';
                    folderHtml += '<div class="title">'+fileNoTxt+'. '+list_data['foldername']+'</div>';
                    folderHtml += '<div class="information">';
                    folderHtml += '<dl>';
                    folderHtml += '<dt class="hobanGray">등록일</dt>';
                    folderHtml += '<dd>'+regDate+'</dd>';
                    folderHtml += '<dt>최종 수정일</dt>';
                    folderHtml += '<dd>'+modDate+'</dd>';
                    folderHtml += '</dl>';
                    folderHtml += '</div>';
                    folderHtml += '</div>';
                    folderHtml += '<div class="location">'+list_data['folderfullpathname'];
                    folderHtml += '<button><img src="img/copy.png" alt="폴더경로복사" onclick="copyToClipBoard(\''+list_data['folderfullpathname']+'\');"></button>';
                    folderHtml += '</div>';
                    folderHtml += '</div>';
                    fileNo++;
                }
                // 검색 데이터가 없으면
                if( folderHtml == "" ) folderHtml = '<div class="no-data">검색된 정보가 없습니다.</div>';
                $("#folder-content-wrap").html(folderHtml);

                // 총 카운터 갯수 정의
                getTotalCountSum();

            }).fail(function(xhr, status, errorThrown) {
                console.log("API FOLDER DATA ERROR");
            });
        }

        function pageFolder()
        {
            var pageHtml = "";
            pageFolderTot = Math.ceil(pageFolderTot); // 올림
            var curPage = pageFolderNum + 1; // 현재 페이지
            if( curPage > 1 ){
                pageHtml += '<a href="#" class="arrow" onclick="folderJson(\'1\');"><img src="img/first.png" alt="처음페이지"></a>';
            }
            var pageS = ( parseInt((curPage - 1) / 10) * 10 ) + 1;
            var pageE = pageS + 10 - 1;
            if( pageE >= pageFolderTot ) pageE = pageFolderTot;
            if( pageS > 1 ){
                pageHtml += '<a href="#" class="arrow" onclick="folderJson(\''+(pageS-1)+'\');"><img src="img/prev.png" alt="이전페이지"></a>';
            }
            if (pageFolderTot > 1) {
                for (k=pageS;k<=pageE;k++) {
                    if (curPage != k){
                        pageHtml += '<a href="#" onclick="folderJson(\''+k+'\');">'+k+'</a>';
                    }else{
                        pageHtml += '<a href="#" class="active" onclick="folderJson(\''+k+'\');">'+k+'</a>';
                    }
                }
            }
            if (pageFolderTot > pageE){
                pageHtml += '<a href="#" class="arrow" onclick="folderJson(\''+(pageE+1)+'\');"><img src="img/next.png" alt="다음페이지"></a>';
            }
            if (curPage < pageFolderTot) {
                pageHtml += '<a href="#" class="arrow" onclick="folderJson(\''+pageFolderTot+'\');"><img src="img/last.png" alt="마지막페이지"></a>';
            }
            $("#page-folder-wrap").html(pageHtml);
        }
    </script>
</head>
<body>
<div style="height:0;position:absolute;z-index: -1;"><textarea id="copy_txt"></textarea></div>

<div class="hobanS">

    <!-- 헤더 S -->
    <div class="hobanS_search01">
        <form name="fm" method="post" action="./gsch.jsp">
            <header>
                <h1><a href="./intro.jsp"><img src="img/logo.png" alt="HOBAN"></a></h1>
                <div class="searchBar">
                    <input type="text" name="sch_kwd" required value="<%=schKwd %>" />
                    <button type="submit"><img src="img/search.png" alt="검색"></button>
                </div>
                <p>띄어쓰기는 and 검색, | 는 or 검색, !는 not 검색(제외 검색)입니다.</p>
            </header>
        </form>
    </div>
    <!-- 헤더 E -->

    <!-- 네비 S -->
    <div class="hobanS_search02">
        <nav>
            <ul class="tabMenu01">
                <li><a href="javascript:;" class="active">파일</a></li>
                <li><a href="javascript:;">폴더</a></li>
            </ul>
            <p>'<%=schKwd %>' 에 대한 검색결과는 <b>총<span id="sch-total">0</span>건</b> 입니다.</p>
            <div class="detail">상세검색</div>
            <div class="listUp">
                <label><input type="radio" name="list_sort" value="R" checked />정확도순</label>
                <label><input type="radio" name="list_sort" value="D" />날짜순</label>
                <label><input type="radio" name="list_sort" value="T" />제목순</label>
            </div>
        </nav>
    </div>
    <!-- 네비 E -->

    <!-- 검색결과 S -->
    <div class="hobanS_contents">
        <!-- 통합검색_파일 S -->
        <div class="on">
            <!-- 상세검색 S -->
            <div class="detailSearch">
                <div class="term">
                    <h3>검색기간 설정</h3>
                    <ul>
                        <li><input type="radio" name="term_dvs" id="rf_1" value="A" checked /><label for="rf_1">전체</label></li>
                        <li><input type="radio" name="term_dvs" id="rf_2" value="D" /><label for="rf_2">1일</label></li>
                        <li><input type="radio" name="term_dvs" id="rf_3" value="W" /><label for="rf_3">1주</label></li>
                        <li><input type="radio" name="term_dvs" id="rf_4" value="M" /><label for="rf_4">1개월</label></li>
                        <li><input type="radio" name="term_dvs" id="rf_5" value="C" /><label for="rf_5">사용자 정의</label></li>
                    </ul>
                    <div class="period">
                        <input type="date" name="s_date" id="s_date" disabled /> ~ <input type="date" name="e_date" id="e_date" disabled />
                    </div>
                    <div class="btn Orange">
                        <button type="button" onclick="fileJson();">검색하기</button>
                    </div>
                </div>
				<div class="range">
					<h3>검색문서유형</h3>
                    <%-- <ul id="radio-doc-type-wrap"></ul> --%>
                    <select name="doctype_dvs" id="radio-doc-type-wrap" style="padding:4px;border:1px solid #ccc"></select>
				</div>
            </div>
            <!-- 상세검색 E -->

            <div class="contentsWrap">
                <!-- 검색결과 상단 S -->
                <div class="header"><h2>파일(<span id="sch-file-total">0</span>건)</h2></div>
                <!-- 검색결과 상단 E -->

                <!-- 검색결과 단락 S -->
                <div id="file-content-wrap"></div>
                <!-- 검색결과 단락 E -->
            </div>

            <!-- 검색결과 하단 S -->
            <div class="bottomWrap">
                <!-- 페이지네이션 S -->
                <div class="pagination" id="page-file-wrap"></div>
                <!-- 페이지네이션 E -->
            </div>
            <!-- 검색결과 하단 E -->

        </div>
        <!-- 통합검색_파일 E -->

        <!-- 통합검색_폴더 S -->
        <div>
            <!-- 상세검색 S -->
            <div class="detailSearch">
                <div class="term">
                    <h3>검색기간 설정</h3>
                    <ul>
                        <li><input type="radio" name="term_dvs2" id="rf_12" value="A" checked /><label for="rf_12">전체</label></li>
                        <li><input type="radio" name="term_dvs2" id="rf_22" value="D" /><label for="rf_22">1일</label></li>
                        <li><input type="radio" name="term_dvs2" id="rf_32" value="W" /><label for="rf_32">1주</label></li>
                        <li><input type="radio" name="term_dvs2" id="rf_42" value="M" /><label for="rf_42">1개월</label></li>
                        <li><input type="radio" name="term_dvs2" id="rf_52" value="C" /><label for="rf_52">사용자 정의</label></li>
                    </ul>
                    <div class="period">
                        <input type="date" name="s_date2" id="s_date2" disabled /> ~ <input type="date" name="e_date2" id="e_date2" disabled />
                    </div>
                    <div class="btn Orange">
                        <button type="button" onclick="folderJson();">검색하기</button>
                    </div>
                </div>
				<div class="range">
					<h3>검색문서유형</h3>
                    <%-- <ul id="radio-doc-type-wrap2"></ul> --%>
                    <select name="doctype_dvs2" id="radio-doc-type-wrap2" style="padding:4px;border:1px solid #ccc"></select>
				</div>
            </div>
            <!-- 상세검색 E -->

            <div class="contentsWrap">
                <!-- 검색결과 상단 S -->
                <div class="header"><h2>폴더(<span id="sch-folder-total">0</span>건)</h2></div>
                <!-- 검색결과 상단 E -->

                <!-- 검색결과 단락 S -->
                <div id="folder-content-wrap"></div>
                <!-- 검색결과 단락 E -->
            </div>

            <!-- 검색결과 하단 S -->
            <div class="bottomWrap">
                <!-- 페이지네이션 S -->
                <div class="pagination" id="page-folder-wrap"></div>
                <!-- 페이지네이션 S -->
            </div>
            <!-- 검색결과 하단 E -->

        </div>
        <!-- 통합검색_폴더 E -->
    </div>
    <!-- 검색결과 E -->

</div>
</body>
</html>
