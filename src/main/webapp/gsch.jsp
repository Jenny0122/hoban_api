<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%
  String schKwd = request.getParameter("sch_kwd"); // 검색어
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>호반건설 - 일반검색</title>
    <link rel="stylesheet" href="css/font/pretendard.css">
    <link rel="stylesheet" href="css/common.css">
    <script src="js/jquery-1.12.3.js"></script>
    <script src="js/common.js"></script>
    <script>
        var pageFileTot = 0; // 파일 전체페이지
        var pageFileNum = 0; // 파일 현재페이지
        var pageFolderTot = 0; // 폴더 전체페이지
        var pageFolderNum = 0; // 폴더 현재페이지

        $(document).ready(function(){
            //console.log("SCH Keywoed => <%=schKwd %>");
        })

        $(window).load(function(){ // 페이지 로딩 후
            fileJson(); // 파일
            folderJson(); // 폴더
        })

        function fileJson(clickPageNum='') // 파일
        {
            //console.log("clickPageNum => "+clickPageNum);
            if( clickPageNum != "" )    pageFileNum = clickPageNum - 1;
            var paramData = {};
            paramData.searchTargetOID = "ALL";
            paramData.query = "<%=schKwd %>";
            paramData.aclFilterInfos = "admin@UR|k, S000@PR|k";
            paramData.pageStart = pageFileNum;

            var schTotal = 0;
            var schFileTotal = 0;
            var schFolderTotal = 0;

            $.ajax({
                url : '/search',
                data : JSON.stringify(paramData),
                type : 'POST',
                dataType: 'JSON',
                contentType: 'application/json; charset=utf-8'
            }).done(function(resultData) {
                //console.log(resultData);
                //console.log(resultData.data[0].collection); // documentinfo
                //console.log(resultData.data[1].collection); // folderinfo
                //console.log(resultData.data[2].collection); // fileinfo
                pageFileTot = parseInt(resultData.data[1].totalCount) / 10;
                //console.log("fileinfo totalCount => "+resultData.data[2].totalCount); // fileinfo totalCount
                pageFile(); // 페이징 정의

                schTotal += parseInt(resultData.data[0].totalCount); // 폴더
                schTotal += parseInt(resultData.data[1].totalCount); // 파일
                schFolderTotal += parseInt(resultData.data[0].totalCount); // 폴더 count
                schFileTotal += parseInt(resultData.data[1].totalCount); // 파일 count

                $("#sch-total").html(schTotal);
                //$("#sch-folder-total").html(schFolderTotal);
                $("#sch-file-total").html(schFileTotal);

                // 파일
                var fileHtml = "";
                var fileNo = (pageFileNum * 10) + 1;
                for( i = 0; i < resultData.data[1].result.length; i++ )
                {
                    if( fileNo < 10 ){
                        var fileNoTxt = "0"+fileNo;
                    }else{
                        var fileNoTxt = fileNo;
                    }
                    var list_data = resultData.data[1].result[i];
                    var regDate = "";
                    //if( list_data['createdat'] != "" ){
                    //    regDate = list_data['createdat'].substr(0,10);
                    //    regDate = regDate.replaceAll("-",".");
                    //}
                    if( list_data['lastmodifiedat'] != "" ){ // 등록일 일단 최종수정일로 처리
                        regDate = list_data['lastmodifiedat'].substr(0,10);
                        regDate = regDate.replaceAll("-",".");
                    }
                    var modDate = "";
                    if( list_data['lastmodifiedat'] != "" ){
                        modDate = list_data['lastmodifiedat'].substr(0,10);
                        modDate = modDate.replaceAll("-",".");
                    }
                    fileHtml += '<div class="contents">';
                    fileHtml += '<div class="tit">';
                    fileHtml += '<div class="title">'+fileNoTxt+'. '+list_data['filename']+'</div>';
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
                    fileHtml += '<button><img src="img/folder.png" alt="폴더이동하기"></button>';
                    fileHtml += '</div>';
                    fileHtml += '</div>';
                    fileNo++;
                }
                $("#file-content-wrap").html(fileHtml);

            }).fail(function(xhr, status, errorThrown) {
                console.log("API FILE DATA ERROR");
            });
        }

        function pageFile()
        {
            var pageHtml = "";
            //console.log("fileinfo 전체 페이지 1 => "+pageFileTot);
            pageFileTot = Math.ceil(pageFileTot); // 올림
            //console.log("fileinfo 전체 페이지 2 => "+pageFileTot);
            var curPage = pageFileNum + 1; // 현재 페이지
            //console.log("fileinfo 현재 페이지 => "+curPage);
            if( curPage > 1 ){
                pageHtml += '<a href="#" class="arrow" onclick="fileJson(\'1\');"><img src="img/first.png" alt="처음페이지"></a>';
            }

            var pageS = (((curPage - 1) / 10) * 10 ) + 1;
            var pageE = pageS + 10 - 1;
            if( pageE >= pageFileTot ) pageE = pageFileTot;

            if( pageS > 1 ){
                pageHtml += '<a href="#" class="arrow" onclick="fileJson(\''+(pageS-1)+'\');"><img src="img/prev.png" alt="이전페이지"></a>';
            }
            //console.log("fileinfo pageS 페이지 => "+pageS);
            //console.log("fileinfo pageE 페이지 => "+pageE);

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
            console.log("clickPageNum => "+clickPageNum);
            if( clickPageNum != "" )    pageFolderNum = clickPageNum - 1;
            var paramData = {};
            paramData.searchTargetOID = "ALL";
            paramData.query = "<%=schKwd %>";
            paramData.aclFilterInfos = "admin@UR|k, S000@PR|k";
            paramData.pageStart = pageFolderNum;

            var schTotal = 0;
            var schFileTotal = 0;
            var schFolderTotal = 0;

            $.ajax({
                url : '/search',
                data : JSON.stringify(paramData),
                type : 'POST',
                dataType: 'JSON',
                contentType: 'application/json; charset=utf-8'
            }).done(function(resultData) {
                //console.log(resultData);
                //console.log(resultData.data[0].collection); // documentinfo
                //console.log(resultData.data[1].collection); // folderinfo
                //console.log(resultData.data[2].collection); // fileinfo
                pageFolderTot = parseInt(resultData.data[0].totalCount) / 10;
                //console.log("folderinfo totalCount => "+resultData.data[1].totalCount); // folderinfo totalCount
                pageFolder(); // 페이징 정의

                schTotal += parseInt(resultData.data[0].totalCount); // 폴더
                schTotal += parseInt(resultData.data[1].totalCount); // 파일
                schFolderTotal += parseInt(resultData.data[0].totalCount); // 폴더 count
                schFileTotal += parseInt(resultData.data[1].totalCount); // 파일 count

                //$("#sch-total").html(schTotal);
                $("#sch-folder-total").html(schFolderTotal);
                //$("#sch-file-total").html(schFileTotal);

                // 폴더
                var folderHtml = "";
                var fileNo = (pageFolderNum * 10) + 1;
                for( i = 0; i < resultData.data[1].result.length; i++ )
                {
                    if( fileNo < 10 ){
                        var fileNoTxt = "0"+fileNo;
                    }else{
                        var fileNoTxt = fileNo;
                    }
                    var list_data = resultData.data[0].result[i];
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
                    folderHtml += '<button><img src="img/copy.png" alt="복사하기"></button>';
                    folderHtml += '</div>';
                    folderHtml += '</div>';
                    fileNo++;
                }
                $("#folder-content-wrap").html(folderHtml);

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

            var pageS = (((curPage - 1) / 10) * 10 ) + 1;
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
            </header>
            </form>
        </div>
        <!-- 헤더 E -->

        <!-- 네비 S -->
        <div class="hobanS_search02">
            <nav>
                <ul class="tabMenu01">
                    <li><a href="javascript:" class="active">파일</a></li>
                    <li><a href="javascript:">폴더</a></li>
                </ul>
                <p>'<%=schKwd %>' 에 대한 검색결과는 <b>총<span id="sch-total">0</span>건</b> 입니다.</p>
                <div class="detail">상세검색</div>
                <div class="listUp">
                    <input type="radio" checked>정확도순
                    <input type="radio">날짜순
                    <input type="radio">제목순
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
                        <a href="#">전체</a>
                        <a href="#">1일</a>
                        <a href="#">1주</a>
                        <a href="#">1개월</a>
                        <a href="#">사용자 정의</a>
                        <div class="period"><input type="date" name="" id=""> ~ <input type="date" name="" id=""></div>
                    </div>
                    <!-- <div class="range">
                    <h3>검색범위 설정</h3>
                    <a href="#">전체</a>
                    <a href="#">제목</a>
                    <a href="#">본문</a>
                    <a href="#">작성자</a>
                    </div> -->
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
                    <div class="pagination" id="page-file-wrap">
<%--
                        <a href="#" class="arrow"><img src="img/first.png" alt="처음페이지"></a>
                        <a href="#" class="arrow"><img src="img/prev.png" alt="이전페이지"></a>
                        <a href="#">1</a>
                        <a href="#" class="active">2</a>
                        <a href="#">3</a>
                        <a href="#">4</a>
                        <a href="#">5</a>
                        <a href="#">6</a>
                        <a href="#">7</a>
                        <a href="#">8</a>
                        <a href="#">9</a>
                        <a href="#">10</a>
                        <a href="#" class="arrow"><img src="img/next.png" alt="다음페이지"></a>
                        <a href="#" class="arrow"><img src="img/last.png" alt="마지막페이지"></a>
--%>
                    </div>
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
                        <a href="#">전체</a>
                        <a href="#">1일</a>
                        <a href="#">1주</a>
                        <a href="#">1개월</a>
                        <a href="#">사용자 정의</a>
                        <div class="period"><input type="date" name="" id=""> ~ <input type="date" name="" id=""></div>
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
                    <div class="pagination" id="page-folder-wrap">
<%--
                        <a href="#" class="arrow"><img src="img/first.png" alt="처음페이지"></a>
                        <a href="#" class="arrow"><img src="img/prev.png" alt="이전페이지"></a>
                        <a href="#">1</a>
                        <a href="#" class="active">2</a>
                        <a href="#">3</a>
                        <a href="#">4</a>
                        <a href="#">5</a>
                        <a href="#">6</a>
                        <a href="#">7</a>
                        <a href="#">8</a>
                        <a href="#">9</a>
                        <a href="#">10</a>
                        <a href="#" class="arrow"><img src="img/next.png" alt="다음페이지"></a>
                        <a href="#" class="arrow"><img src="img/last.png" alt="마지막페이지"></a>
--%>
                    </div>
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