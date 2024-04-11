<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="java.util.Map" %>
<%
    String schKwd = request.getParameter("sch_kwd"); // 검색어
    String jsonString = request.getParameter("jsonString");
	String aclFilterInfos = "";
	aclFilterInfos = "admin@UR|k, S000@PR|k"; // 개발 테스트용, 운영반영시 주석 처리

    // 그룹웨어에서 넘어오는 값
    ObjectMapper mapper = new ObjectMapper();
    if(jsonString != null) {
        Map<String, String> jsonStringMap = mapper.readValue(jsonString, Map.class);
        schKwd = jsonStringMap.get("query");
        aclFilterInfos = jsonStringMap.get("aclFilterInfos");
    }
    if( schKwd == null )			schKwd = "";
    if( aclFilterInfos == null )	aclFilterInfos = "";
%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>호반건설 - 특수검색</title>
	<link rel="stylesheet" href="css/font/pretendard.css">
	<link rel="stylesheet" href="css/common.css">
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
                sensJson(); // 민감
                personJson(); // 개인
            })
        })

		$(window).load(function(){ // 페이지 로딩 후
			// 부서정보 가져오기 API
	        $.ajax({
	            url : '/groups',
	            data : null,
	            type : 'POST',
	            dataType: 'JSON',
	            contentType: 'application/json; charset=utf-8'
	        }).done(function(resultData) {
	            //console.log(resultData.count);
	            //console.log(resultData.data.length);
	            //console.log(resultData);
	            var treeHtml = "";
	            for( i = 0; i < resultData.data.length; i++ ) // 1차 부서
	            {
	            	var list_data = resultData.data[i];
	            	if( list_data['fullpathindex'].length == 4 ){
	            		//console.log(list_data['groupcode']+"___"+list_data['name']+"___"+list_data['oid']+"___"+list_data['parentoid']+"___"+list_data['fullpathindex']);
	            		treeHtml += '<li><input type="checkbox" id="group'+i+'" name="group_name[]" value="'+list_data['name']+'" checked /> <label for="group'+i+'" class="lastTree">'+list_data['name']+'</label></li>';
	            	}
	            }
	            $("#tree1-wrap").html(treeHtml);
	        }).fail(function(xhr, status, errorThrown) {
	            console.log("API FILE DATA ERROR");
	        });

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
	            	dovTypeRadinHtml += '<option value="'+resultData[i].oid+'" '+active+'>'+resultData[i].name+'</option>';
	            }
				$("#radio-doc-type-wrap").html(dovTypeRadinHtml); // 민감 상세검색
				$("#radio-doc-type-wrap2").html(dovTypeRadinHtml); // 개인 상세검색

	            sensJson(); // 민감
				personJson(); // 개인

	        }).fail(function(xhr, status, errorThrown) {
	            console.log("API FILE DATA ERROR");
	        });
		});

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
                // https://ecmdev.e-hoban.co.kr/url/?documentOID={documentOID}&urlType={urlType}
                var theURL = "https://ecmdev.e-hoban.co.kr/url/";
                theURL += "?fileOID="+oid;
                theURL += "&urlType=B";
                //console.log(theURL);
                window.open(theURL);
            }
        }

        function documentOpen(oid) // 파일 속성보기
        {
            if( oid != "" ){
                // https://ecmdev.e-hoban.co.kr/url/?documentOID={documentOID}&urlType={urlType}
                var theURL = "https://ecmdev.e-hoban.co.kr/url/";
                theURL += "?fileOID="+oid;
                theURL += "&urlType=A";
                //console.log(theURL);
                window.open(theURL);
            }
        }

        function chkFm(f) // 민감 검색
        {
        	sensJson();
        	return false;
        }

		function sensJson(clickPageNum='') // 파일
		{
            // 상세검색 정의
            var term_dvs = $("input[name=term_dvs]:checked").val();
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
            paramData.query = $("#sch_kwd").val().trim(); // 검색어 키워드
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
            console.log(paramData);

            $.ajax({
                url : '/search/sensitive',
                data : JSON.stringify(paramData),
                type : 'POST',
                dataType: 'JSON',
                contentType: 'application/json; charset=utf-8'
            }).done(function(resultData) {
                //console.log("FAIL => ");
                //console.log(resultData);
                var apiDataArr = resultData.data[0]; // fileinfo
                pageFileTot = parseInt(apiDataArr.totalCount) / 10;
                pageFile(); // 페이징 정의

                //schFileTotal = parseInt(apiDataArr.totalCount); // 파일 count
                //$("#sch-file-total").html(schFileTotal);

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
                //getTotalCountSum();

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
                pageHtml += '<a href="#" class="arrow" onclick="sensJson(\'1\');"><img src="img/first.png" alt="처음페이지"></a>';
            }
            var pageS = ( parseInt((curPage - 1) / 10) * 10 ) + 1;
            var pageE = pageS + 10 - 1;
            if( pageE >= pageFileTot ) pageE = pageFileTot;
            if( pageS > 1 ){
                pageHtml += '<a href="#" class="arrow" onclick="sensJson(\''+(pageS-1)+'\');"><img src="img/prev.png" alt="이전페이지"></a>';
            }
            if (pageFileTot > 1) {
                for (k=pageS;k<=pageE;k++) {
                    if (curPage != k){
                        pageHtml += '<a href="#" onclick="sensJson(\''+k+'\');">'+k+'</a>';
                    }else{
                        pageHtml += '<a href="#" class="active" onclick="sensJson(\''+k+'\');">'+k+'</a>';
                    }
                }
            }
            if (pageFileTot > pageE){
                pageHtml += '<a href="#" class="arrow" onclick="sensJson(\''+(pageE+1)+'\');"><img src="img/next.png" alt="다음페이지"></a>';
            }
            if (curPage < pageFileTot) {
                pageHtml += '<a href="#" class="arrow" onclick="sensJson(\''+pageFileTot+'\');"><img src="img/last.png" alt="마지막페이지"></a>';
            }
            $("#page-file-wrap").html(pageHtml);
        }

        function chkFm2(f) // 개인 검색
        {
        	personJson();
        	return false;
        }

		function personJson(clickPageNum='') // 파일
		{
            // 상세검색 정의
            var term_dvs = $("input[name=term_dvs2]:checked").val();
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
            // 개인정보
            var group_name2 = $("input[name=group_name2]:checked").val();
            if( group_name2 == "undefined" || group_name2 == null ) group_name2 = "";
            console.log("group_name2 => "+group_name2);


            if( clickPageNum != "" ){
                pageFileNum = clickPageNum - 1;
            }else{
                pageFileNum = 0;
            }
            var paramData = {};
            paramData.searchTargetOID = "ALL";
            paramData.query = $("#sch_kwd2").val().trim(); // 검색어 키워드
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
            paramData.alias = group_name2; // 개인정부 구분(오늘쪽 트리 선택)
            console.log(paramData);

            $.ajax({
                url : '/search/personal',
                data : JSON.stringify(paramData),
                type : 'POST',
                dataType: 'JSON',
                contentType: 'application/json; charset=utf-8'
            }).done(function(resultData) {
                //console.log("FAIL => ");
                console.log(resultData);
                // 건수 처리
                var juminCnt	= parseInt(resultData.customCategoryMap.JUMIN);
                var foreignCnt	= parseInt(resultData.customCategoryMap.FOREIGN);
                var passPortCnt	= parseInt(resultData.customCategoryMap.PASS_PORT);
                var driveCnt	= parseInt(resultData.customCategoryMap.DRIVE);
                $("#juminCnt").html(juminCnt);
                $("#foreignCnt").html(foreignCnt);
                $("#passPortCnt").html(passPortCnt);
                $("#driveCnt").html(driveCnt);
                var apiDataArr = resultData.data[0]; // fileinfo
                pageFolderTot = parseInt(apiDataArr.totalCount) / 10;
                pageFolder(); // 페이징 정의

                //schFileTotal = parseInt(apiDataArr.totalCount); // 파일 count
                //$("#sch-file-total").html(schFileTotal);

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
                $("#folder-content-wrap").html(fileHtml);

                // 총 카운터 갯수 정의
                //getTotalCountSum();

            }).fail(function(xhr, status, errorThrown) {
                console.log("API FILE DATA ERROR");
            });
		}

        function pageFolder()
        {
            var pageHtml = "";
            pageFolderTot = Math.ceil(pageFolderTot); // 올림
            var curPage = pageFileNum + 1; // 현재 페이지
            if( curPage > 1 ){
                pageHtml += '<a href="#" class="arrow" onclick="personJson(\'1\');"><img src="img/first.png" alt="처음페이지"></a>';
            }
            var pageS = ( parseInt((curPage - 1) / 10) * 10 ) + 1;
            var pageE = pageS + 10 - 1;
            if( pageE >= pageFolderTot ) pageE = pageFolderTot;
            if( pageS > 1 ){
                pageHtml += '<a href="#" class="arrow" onclick="personJson(\''+(pageS-1)+'\');"><img src="img/prev.png" alt="이전페이지"></a>';
            }
            if (pageFolderTot > 1) {
                for (k=pageS;k<=pageE;k++) {
                    if (curPage != k){
                        pageHtml += '<a href="#" onclick="personJson(\''+k+'\');">'+k+'</a>';
                    }else{
                        pageHtml += '<a href="#" class="active" onclick="personJson(\''+k+'\');">'+k+'</a>';
                    }
                }
            }
            if (pageFolderTot > pageE){
                pageHtml += '<a href="#" class="arrow" onclick="personJson(\''+(pageE+1)+'\');"><img src="img/next.png" alt="다음페이지"></a>';
            }
            if (curPage < pageFolderTot) {
                pageHtml += '<a href="#" class="arrow" onclick="personJson(\''+pageFolderTot+'\');"><img src="img/last.png" alt="마지막페이지"></a>';
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
      <header>
        <h1><a href="javascript:;"><img src="img/logo.png" alt="HOBAN"></a></h1>
      </header>
    </div>
    <!-- 헤더 E -->

	<!-- 네비 S -->
	<div class="hobanS_search02">
		<nav>
			<ul class="tabMenu01">
				<li><a href="javascript:;" class="active">민감정보</a></li>
				<li><a href="javascript:;">개인정보</a></li>
			</ul>
			<p><!-- '홍길동' 에 대한 검색결과는 <b>총27건</b> 입니다. --></p>
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

		<!-- 특수검색_민감 S -->
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
                        <button type="button" onclick="sensJson();">검색하기</button>
                    </div>
                </div>
				<div class="range">
					<h3>검색문서유형</h3>
                    <select name="doctype_dvs" id="radio-doc-type-wrap" style="padding:4px;border:1px solid #ccc"></select>
				</div>
            </div>
            <!-- 상세검색 E -->

	        <div class="special"><!-- 특수검색에만 추가됨 -->
				<div class="contentsWrap">
					<!-- 검색결과 상단 S -->
					<form name="fm" method="post" action="" onsubmit="return chkFm(this);">
						<div class="header">
							<h2>민감정보 검색</h2>
							<div class="searchBar small">
		                    	<input type="text" name="sch_kwd" id="sch_kwd" required value="" />
		                    	<button type="submit"><img src="img/search.png" alt="검색"></button>
							</div>
						</div>
					</form>
					<!-- 검색결과 상단 E -->

	                <!-- 검색결과 단락 S -->
	                <div id="file-content-wrap"></div>
	                <!-- 검색결과 단락 E -->
	            </div>

				<!-- 부서 및 정보내용 S -->
				<div class="treeWrap">
					<!-- 부서별 S -->
					<div class="department">
						<h3 id="tree_label">부서별</h3>
						<ul class="tree label01">
							<li><input type="checkbox" id="root" checked> <label for="root">전체선택</label>
								<ul class="label02" id="tree1-wrap"></ul>
							</li>
						</ul>
					</div>
					<!-- 부서별 E -->
				</div>
				<!-- 부서 및 정보내용 E -->

        	</div>

	        <!-- 검색결과 하단 S -->
	        <div class="bottomWrap">
	            <!-- 페이지네이션 S -->
	            <div class="pagination" id="page-file-wrap"></div>
	            <!-- 페이지네이션 E -->

				<!-- 버튼R S -->
				<div class="btn">
					<button>Excel 다운로드</button>
				</div>
				<!-- 버튼R E -->
	        </div>
	        <!-- 검색결과 하단 E -->

		</div>
		<!-- 특수검색_민감 E -->

		<!-- 특수검색_개인 S -->
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
                        <button type="button" onclick="personJson();">검색하기</button>
                    </div>
                </div>
				<div class="range">
					<h3>검색문서유형</h3>
                    <%-- <ul id="radio-doc-type-wrap2"></ul> --%>
                    <select name="doctype_dvs2" id="radio-doc-type-wrap2" style="padding:4px;border:1px solid #ccc"></select>
				</div>
            </div>
            <!-- 상세검색 E -->

			<div class="special"><!-- 특수검색에만 추가됨 -->
				<div class="contentsWrap">
					<!-- 검색결과 상단 S -->
					<form name="fm" method="post" action="" onsubmit="return chkFm2(this);">
						<div class="header">
							<h2>개인정보 검색</h2>
							<div class="searchBar small">
		                    	<input type="text" name="sch_kwd2" id="sch_kwd2" required value="" />
		                    	<button type="submit"><img src="img/search.png" alt="검색"></button>
							</div>
						</div>
					</form>
					<!-- 검색결과 상단 E -->

					<!-- 개인정보 문서건수 S -->
					<div class="privacyList">
						<table>
							<thead>
							<tr>
								<th>개인정보</th>
								<td>주민등록번호</td>
								<td>외국인등록번호</td>
								<td>여권번호</td>
								<td>운전면허번호</td>
							</tr>
							</thead>
							<tbody>
							<tr>
								<th>문서건수</th>
								<td><span id="juminCnt">0</span>건</td>
								<td><span id="foreignCnt">0</span>건</td>
								<td><span id="passPortCnt">0</span>건</td>
								<td><span id="driveCnt">0</span>건</td>
							</tr>
							</tbody>
						</table>
						<p>※ 개인정보를 포함한 문서의 건수를 나타냅니다.</p>
					</div>
					<!-- 개인정보 문서건수 S -->


	                <!-- 검색결과 단락 S -->
	                <div id="folder-content-wrap"></div>
	                <!-- 검색결과 단락 E -->

			  </div>

			  <!-- 부서 및 정보내용 S -->
			  <div class="treeWrap">

			    <!-- 개인정보 S -->
			    <div class="department">
			      <h3 id="tree_label">개인정보</h3>

			      <ul class="tree label01">
			        <li>
			          <input type="checkbox" id="root02_1" checked>
			          <label for="root02_1">전체선택</label>
			          <ul class="label02">
			            <li>
			              <input type="radio" id="node02_21" name="group_name2" value="JUMIN" onclick="personJson();" />
			              <label for="node02_21" class="lastTree">주민등록번호</label>
			            </li>
			            <li>
			              <input type="radio" id="node02_22" name="group_name2" value="FOREIGN" onclick="personJson();" />
			              <label for="node02_22" class="lastTree">외국인등록번호</label>
			            </li>
			            <li>
			              <input type="radio" id="node02_23" name="group_name2" value="PASS_PORT" onclick="personJson();" />
			              <label for="node02_23" class="lastTree">여권번호</label>
			            </li>
			            <li>
			              <input type="radio" id="node02_24" name="group_name2" value="DRIVE" onclick="personJson();" />
			              <label for="node02_24" class="lastTree">운전면허번호</label>
			            </li>
			          </ul>
			        </li>
			      </ul>

			    </div>
			    <!-- 개인정보 E -->

			  </div>
			  <!-- 부서 및 정보내용 E -->

			</div>

			<!-- 검색결과 하단 S -->
			<div class="bottomWrap">
		        <!-- 페이지네이션 S -->
		        <div class="pagination" id="page-folder-wrap"></div>
		        <!-- 페이지네이션 E -->

				<!-- 버튼R S -->
				<div class="btn">
					<button>Excel 다운로드</button>
				</div>
				<!-- 버튼R E -->

			</div>
			<!-- 검색결과 하단 E -->

		</div>
		<!-- 특수검색_개인 E -->

	</div>
	<!-- 검색결과 E -->

  </div>
</body>
</html>