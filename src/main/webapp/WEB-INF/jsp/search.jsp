<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%!static public class MenuSearchVo {

		// 메뉴 레벨
		private String menu_level;
		// url
		private String url;
		// 사용 여부
		private String use_yn;
		// 
		private String srt_seq;
		// 메뉴 url
		private String menu_url;
		// 메뉴명
		private String menu_name;
		// 메뉴 id
		private String menu_id;
		// display 정보
		private String display_yn;
		// 
		private String login_only_yn;
		// 이미지 url
		private String img_url;
		// 
		private String para_nm;
		// input 타입
		private String input_type;
		// 문서 보기 권한
		private String navi_id;
		// 문서 수정 권한
		private String navi_txt;
		// 문서 삭제 권한
		private String android_min;
		// 문서 목록보기 권한
		private String iphone_min;
		//  권한 없음
		private String access_rule;
		// 파일 크기
		private String net_funnel_id;

		public MenuSearchVo(String menu_level, String url, String use_yn, String srt_seq, String menu_url,
				String menu_name, String menu_id, String display_yn, String login_only_yn, String img_url,
				String para_nm, String input_type, String navi_id, String navi_txt, String android_min,
				String iphone_min, String access_rule, String net_funnel_id) {
			super();
			this.menu_level = menu_level;
			this.url = url;
			this.use_yn = use_yn;
			this.srt_seq = srt_seq;
			this.menu_url = menu_url;
			this.menu_name = menu_name;
			this.menu_id = menu_id;
			this.display_yn = display_yn;
			this.login_only_yn = login_only_yn;
			this.img_url = img_url;
			this.para_nm = para_nm;
			this.input_type = input_type;
			this.navi_id = navi_id;
			this.navi_txt = navi_txt;
			this.android_min = android_min;
			this.iphone_min = iphone_min;
			this.access_rule = access_rule;
			this.net_funnel_id = net_funnel_id;
		}

		@Override
		public String toString() {
			return "{\"menu_level\": " + (menu_level == null ? menu_level : "\"" + menu_level + "\"") + ", \"url\": "
					+ (url == null ? url : "\"" + url + "\"") + ", \"use_yn\": "
					+ (use_yn == null ? use_yn : "\"" + use_yn + "\"") + ", \"srt_seq\": "
					+ (srt_seq == null ? srt_seq : "\"" + srt_seq + "\"") + ", \"menu_url\": "
					+ (menu_url == null ? menu_url : "\"" + menu_url + "\"") + ", \"menu_name\": "
					+ (menu_name == null ? menu_name : "\"" + menu_name + "\"") + ", \"menu_id\": "
					+ (menu_id == null ? menu_id : "\"" + menu_id + "\"") + ", \"display_yn\": "
					+ (display_yn == null ? display_yn : "\"" + display_yn + "\"") + ", \"login_only_yn\": "
					+ (login_only_yn == null ? login_only_yn : "\"" + login_only_yn + "\"") + ", \"img_url\": "
					+ (img_url == null ? img_url : "\"" + img_url + "\"") + ", \"para_nm\": "
					+ (para_nm == null ? para_nm : "\"" + para_nm + "\"") + ", \"input_type\": "
					+ (input_type == null ? input_type : "\"" + input_type + "\"") + ", \"navi_id\": "
					+ (navi_id == null ? navi_id : "\"" + navi_id + "\"") + ", \"navi_txt\": "
					+ (navi_txt == null ? navi_txt : "\"" + navi_txt + "\"") + ", \"android_min\": "
					+ (android_min == null ? android_min : "\"" + android_min + "\"") + ", \"iphone_min\": "
					+ (iphone_min == null ? iphone_min : "\"" + iphone_min + "\"") + ", \"access_rule\": "
					+ (access_rule == null ? access_rule : "\"" + access_rule + "\"") + ", \"net_funnel_id\": "
					+ (net_funnel_id == null ? net_funnel_id : "\"" + net_funnel_id + "\"") + "}";
		}
	}%>
<%!static public class ProductSearchVo {

		// 상품 번호
		private String artId;
		// 상품 종류
		private String pd_type;
		// 상품 메인명
		private String pd_title;
		// 상품 서브명
		private String pd_sub_title;
		// 상품 서브명2
		private String pd_sub_title2;
		// 상품 url
		private String url;

		public ProductSearchVo(String artId, String pd_type, String pd_title, String pd_sub_title, String pd_sub_title2,
				String url) {
			super();
			this.artId = artId;
			this.pd_type = pd_type;
			this.pd_title = pd_title;
			this.pd_sub_title = pd_sub_title;
			this.pd_sub_title2 = pd_sub_title2;
			this.url = url;
		}

		@Override
		public String toString() {
			return "{\"artId\": " + (artId == null ? artId : "\"" + artId + "\"") + ", \"pd_type\": "
					+ (pd_type == null ? pd_type : "\"" + pd_type + "\"") + ", \"pd_title\": "
					+ (pd_title == null ? pd_title : "\"" + pd_title + "\"") + ", \"pd_sub_title\": "
					+ (pd_sub_title == null ? pd_sub_title : "\"" + pd_sub_title + "\"") + ", \"pd_sub_title2\": "
					+ (pd_sub_title2 == null ? pd_sub_title2 : "\"" + pd_sub_title2 + "\"") + ", \"url\": "
					+ (url == null ? url : "\"" + url + "\"") + "}";
		}
	}%>
<%!static public class NoticeSearchVo {
		// 공지사항 번호
		private String artId;

		// 제목
		private String title;

		// 내용
		private String contents;

		// 등록날짜
		private String date;

		// url
		private String url;

		@Override
		public String toString() {
			return "{\"artId\": " + (artId == null ? artId : "\"" + artId + "\"") + ", \"title\": "
					+ (title == null ? title : "\"" + title + "\"") + ", \"contents\": "
					+ (contents == null ? contents : "\"" + contents + "\"") + ", \"date\": "
					+ (date == null ? date : "\"" + date + "\"") + ", \"url\": "
					+ (url == null ? url : "\"" + url + "\"") + "}";
		}

		public NoticeSearchVo(String artId, String title, String contents, String date, String url) {
			super();
			this.artId = artId;
			this.title = title;
			this.contents = contents;
			this.date = date;
			this.url = url;
		}
	}%>
<%!static public class FAQSearchVo {

		// 게시번호
		private String artId;

		//제목
		private String cat_title;

		//제목
		private String art_title;

		//내용
		private String art_contents;

		@Override
		public String toString() {
			return "{\"artId\": " + (artId == null ? artId : "\"" + artId + "\"") + ", \"cat_title\": "
					+ (cat_title == null ? cat_title : "\"" + cat_title + "\"") + ", \"art_title\": "
					+ (art_title == null ? art_title : "\"" + art_title + "\"") + ", \"art_contents\": "
					+ (art_contents == null ? art_contents : "\"" + art_contents + "\"") + "}";
		}

		public FAQSearchVo(String artId, String cat_title, String art_title, String art_contents) {
			super();
			this.artId = artId;
			this.cat_title = cat_title;
			this.art_title = art_title;
			this.art_contents = art_contents;
		}
	}%>
<%
String server_ip = "";
int server_port = -1;
int server_timeout = -1;

String Query = "";

// collection, 검색필드, 출력필드 정의
String COLLECTION = "";
int QUERY_LOG = -1;
int EXTEND_OR = -1;
int PAGE_START = -1; // 검색 결과를 받아오는 시작 위치
int RESULT_COUNT = -1; // 한번에 출력되는 검색 건수
String SORT_FIELD = ""; // 정렬필드
String SEARCH_FIELD = ""; // 검색필드
String DOCUMENT_FIELD = ""; // 출력필드

// create object
QueryAPI530.Search search = new QueryAPI530.Search();
int ret = 0;

int totalCount = -1;
int resultCount = -1;

//
//
List<MenuSearchVo> menuList = new ArrayList<>();

// 검색기 server 설정
server_ip = "10.207.1.51";
server_port = 7005;
server_timeout = 10 * 1000;

Query = "하나은행";

// collection, 검색필드, 출력필드 정의
COLLECTION = "menu";
QUERY_LOG = 1;
EXTEND_OR = 0;
PAGE_START = 0; // 검색 결과를 받아오는 시작 위치
RESULT_COUNT = 10; // 한번에 출력되는 검색 건수
SORT_FIELD = "RANK/DESC"; // 정렬필드
SEARCH_FIELD = "MENU_URL,MENU_NAME,NAVI_ID,NAVI_TXT"; // 검색필드
DOCUMENT_FIELD = "DOCID,Date,MENU_LEVEL,URL,USE_YN,SRT_SEQ,MENU_URL,MENU_NAME,MENU_ID,DISPLAY_YN,LOGIN_ONLY_YN,IMG_URL,PARA_NM,INPUT_TYPE,NAVI_ID,NAVI_TXT,ANDROID_MIN,IPHONE_MIN,ACCESS_RULE,NET_FUNNEL_ID"; // 출력필드

search = new QueryAPI530.Search();
ret = 0;

// common query 설정
ret = search.w3SetCodePage("UTF-8");
ret = search.w3SetQueryLog(QUERY_LOG);
ret = search.w3SetCommonQuery(Query, EXTEND_OR);

// collection, 검색 필드, 출력 필드 설정
ret = search.w3AddCollection(COLLECTION);
ret = search.w3SetPageInfo(COLLECTION, PAGE_START, RESULT_COUNT);
ret = search.w3SetSortField(COLLECTION, SORT_FIELD);
ret = search.w3SetSearchField(COLLECTION, SEARCH_FIELD);
ret = search.w3SetDocumentField(COLLECTION, DOCUMENT_FIELD);

// request
ret = search.w3ConnectServer(server_ip, server_port, server_timeout);
ret = search.w3ReceiveSearchQueryResult(3);

// check error
if (search.w3GetError() != 0) {
	MenuSearchVo vo = new MenuSearchVo("4", "/cmb/resource/html/BIAT/INQ01/BINQ0101001_T01.html", "Y", "01", "/BIAT/INQ", "보유계좌조회", "mi://BINQ0101001_T01", "S", "Y", "", "menuAuth=/BIAT/INQ01/BINQ010100101_T01.do", "A", "BANK_MENU > BIAT > BIAT_INQ > mi://BINQ0101001_T01", "뱅킹메뉴 > 조회/이체 > 조회 > 보유계좌조회", "1.00", "1.00", "", "");
	menuList.add(vo);
} else {
	// 전체건수, 결과건수 출력
	totalCount = search.w3GetResultTotalCount(COLLECTION);
	resultCount = search.w3GetResultCount(COLLECTION);

	System.out.println(COLLECTION + " 검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건");
	for (int i = 0; i < resultCount; i++) {

		// 기본 검색결과 객체 생성
		String menu_level = search.w3GetField(COLLECTION, "MENU_LEVEL", i);
		String url = search.w3GetField(COLLECTION, "URL", i);
		String use_yn = search.w3GetField(COLLECTION, "USE_YN", i);
		String srt_seq = search.w3GetField(COLLECTION, "SRT_SEQ", i);
		String menu_url = search.w3GetField(COLLECTION, "MENU_URL", i);
		String menu_name = search.w3GetField(COLLECTION, "MENU_NAME", i);
		String menu_id = search.w3GetField(COLLECTION, "MENU_ID", i);
		String display_yn = search.w3GetField(COLLECTION, "DISPLAY_YN", i);
		String login_only_yn = search.w3GetField(COLLECTION, "LOGIN_ONLY_YN", i);
		String img_url = search.w3GetField(COLLECTION, "IMG_URL", i);
		String para_nm = search.w3GetField(COLLECTION, "PARA_NM", i);
		String input_type = search.w3GetField(COLLECTION, "INPUT_TYPE", i);
		String navi_id = search.w3GetField(COLLECTION, "NAVI_ID", i);
		String navi_txt = search.w3GetField(COLLECTION, "NAVI_TXT", i);
		String android_min = search.w3GetField(COLLECTION, "ANDROID_MIN", i);
		String iphone_min = search.w3GetField(COLLECTION, "IPHONE_MIN", i);
		String access_rule = search.w3GetField(COLLECTION, "ACCESS_RULE", i);
		String net_funnel_id = search.w3GetField(COLLECTION, "NET_FUNNEL_ID", i);

		MenuSearchVo vo = new MenuSearchVo(menu_level, url, use_yn, srt_seq, menu_url, menu_name, menu_id, display_yn,
		login_only_yn, img_url, para_nm, input_type, navi_id, navi_txt, android_min, iphone_min, access_rule,
		net_funnel_id);

		menuList.add(vo);

	}
}
	


//
//
//

/**
* FAQ 통합 검색* 

* @author 안선정
* @param query 검색어
* @param start 시작 위치
* @param count 한 페이지당 결과 수
* @return
*/
List<FAQSearchVo> faqList = new ArrayList<>();
// 검색기 server 설정
server_ip = "10.207.1.51";
server_port = 7005;
server_timeout = 10 * 1000;

Query = "하나은행";

// collection, 검색필드, 출력필드 정의
COLLECTION = "faq";
QUERY_LOG = 1;
EXTEND_OR = 0;
PAGE_START = 0; // 검색 결과를 받아오는 시작 위치
RESULT_COUNT = 10; // 한번에 출력되는 검색 건수
SORT_FIELD = "RANK/DESC"; // 정렬필드
SEARCH_FIELD = "ARTID,TITLE,CAT_TITLE,ART_TITLE,ART_CONTENTS"; // 검색필드
DOCUMENT_FIELD = "DOCID,Date,ARTID,CAT_TITLE,ART_TITLE,ART_CONTENTS"; // 출력필드

// create object
search = new QueryAPI530.Search();
ret = 0;

// common query 설정
ret = search.w3SetCodePage("UTF-8");
ret = search.w3SetQueryLog(QUERY_LOG);
ret = search.w3SetCommonQuery(Query, EXTEND_OR);

// collection, 검색 필드, 출력 필드 설정
ret = search.w3AddCollection(COLLECTION);
ret = search.w3SetPageInfo(COLLECTION, PAGE_START, RESULT_COUNT);
ret = search.w3SetSortField(COLLECTION, SORT_FIELD);
ret = search.w3SetSearchField(COLLECTION, SEARCH_FIELD);
ret = search.w3SetDocumentField(COLLECTION, DOCUMENT_FIELD);

// request
ret = search.w3ConnectServer(server_ip, server_port, server_timeout);
ret = search.w3ReceiveSearchQueryResult(3);

// check error
if (search.w3GetError() != 0) {
	FAQSearchVo vo = new FAQSearchVo("1453489", "일반사항", "마스터와 사용자의 차이는 무엇입니까?", "◆ 마스터 or 마스터+사용자<br> 각 사용자에 사용권한을 부여하고 기업의 시스템사용에 관해 총괄관리하는 자로서 필요에 따라 자신도 사용자로 등록하여 거래업무를 수행할 수 있습니다.<br> (단, 회사가 문서로 요청하는 경우에 한하여 마스터는 실제 거래업무를 수행하는 사용자 권한을 제한할 수 있습니다.)<br> 고객사별로 마스터는 한분만 존재합니다.");
	faqList.add(vo);
} else {

	// 전체건수, 결과건수 출력
	totalCount = search.w3GetResultTotalCount(COLLECTION);
	resultCount = search.w3GetResultCount(COLLECTION);

	System.out.println(COLLECTION + "검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건");
	for (int i = 0; i < resultCount; i++) {

		// 기본 검색결과 객체 생성
		String artId = search.w3GetField(COLLECTION, "ARTID", i);
		String cat_title = search.w3GetField(COLLECTION, "CAT_TITLE", i);
		String art_title = search.w3GetField(COLLECTION, "ART_TITLE", i);
		String art_contents = search.w3GetField(COLLECTION, "ART_CONTENTS", i);

		FAQSearchVo vo = new FAQSearchVo(artId, cat_title, art_title, art_contents);

		faqList.add(vo);
	}

}

	
//
//

// 상품
List<ProductSearchVo> productList = new ArrayList<>();

// 검색기 server 설정
server_ip = "10.207.1.51";
server_port = 7005;
server_timeout = 10 * 1000;

Query = "하나은행";

// collection, 검색필드, 출력필드 정의
COLLECTION = "product";
QUERY_LOG = 1;
EXTEND_OR = 0;
PAGE_START = 0; // 검색 결과를 받아오는 시작 위치
RESULT_COUNT = 10; // 한번에 출력되는 검색 건수
SORT_FIELD = "RANK/DESC"; // 정렬필드
SEARCH_FIELD = "PD_TYPE,PD_TITLE,PD_SUB_TITLE2"; // 검색필드
DOCUMENT_FIELD = "DOCID,Date,ARTID,PD_TYPE,PD_TITLE,PD_SUB_TITLE,PD_SUB_TITLE2,URL"; // 출력필드

search = new QueryAPI530.Search();
ret = 0;

// common query 설정
ret = search.w3SetCodePage("UTF-8");
ret = search.w3SetQueryLog(QUERY_LOG);
ret = search.w3SetCommonQuery(Query, EXTEND_OR);

// collection, 검색 필드, 출력 필드 설정
ret = search.w3AddCollection(COLLECTION);
ret = search.w3SetPageInfo(COLLECTION, PAGE_START, RESULT_COUNT);
ret = search.w3SetSortField(COLLECTION, SORT_FIELD);
ret = search.w3SetSearchField(COLLECTION, SEARCH_FIELD);
ret = search.w3SetDocumentField(COLLECTION, DOCUMENT_FIELD);

// request
ret = search.w3ConnectServer(server_ip, server_port, server_timeout);
ret = search.w3ReceiveSearchQueryResult(3);

// check error
if (search.w3GetError() != 0) {
	ProductSearchVo vo = new ProductSearchVo("1486566", "입출금", "기업자유예금", "각종 입출금이 자유로운 통장", "여유자금 운용과 입출금이 가능한 예금통장", "/cont/html/product/product01/1486566_153484.html");
	productList.add(vo);

} else {
	// 전체건수, 결과건수 출력
	totalCount = search.w3GetResultTotalCount(COLLECTION);
	resultCount = search.w3GetResultCount(COLLECTION);

	System.out.println(COLLECTION + " 검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건");

	for (int i = 0; i < resultCount; i++) {

		// 기본 검색결과 객체 생성
		String artId = search.w3GetField(COLLECTION, "ARTID", i);
		String pd_type = search.w3GetField(COLLECTION, "PD_TYPE", i);
		String pd_title = search.w3GetField(COLLECTION, "PD_TITLE", i);
		String pd_sub_title = search.w3GetField(COLLECTION, "PD_SUB_TITLE", i);
		String pd_sub_title2 = search.w3GetField(COLLECTION, "PD_SUB_TITLE2", i);
		String url = search.w3GetField(COLLECTION, "URL", i);

		ProductSearchVo vo = new ProductSearchVo(artId, pd_type, pd_title, pd_sub_title, pd_sub_title2, url);

		productList.add(vo);
	}
}

	


//
//
//

List<NoticeSearchVo> noticeList = new ArrayList<>();
// 검색기 server 설정
server_ip = "10.207.1.51";
server_port = 7005;
server_timeout = 10 * 1000;

Query = "하나은행";

// collection, 검색필드, 출력필드 정의
COLLECTION = "notice";
QUERY_LOG = 1;
EXTEND_OR = 0;
PAGE_START = 0; // 검색 결과를 받아오는 시작 위치
RESULT_COUNT = 10; // 한번에 출력되는 검색 건수
SORT_FIELD = "RANK/DESC"; // 정렬필드
SEARCH_FIELD = "ARTID,TITLE,CONTENTS"; // 검색필드
DOCUMENT_FIELD = "DOCID,ARTID,TITLE,CONTENTS,Date,URL"; // 출력필드

search = new QueryAPI530.Search();
ret = 0;

// common query 설정
ret = search.w3SetCodePage("UTF-8");
ret = search.w3SetQueryLog(QUERY_LOG);
ret = search.w3SetCommonQuery(Query, EXTEND_OR);

// collection, 검색 필드, 출력 필드 설정
ret = search.w3AddCollection(COLLECTION);
ret = search.w3SetPageInfo(COLLECTION, PAGE_START, RESULT_COUNT);
ret = search.w3SetSortField(COLLECTION, SORT_FIELD);
ret = search.w3SetSearchField(COLLECTION, SEARCH_FIELD);
ret = search.w3SetDocumentField(COLLECTION, DOCUMENT_FIELD);

// request
ret = search.w3ConnectServer(server_ip, server_port, server_timeout);
ret = search.w3ReceiveSearchQueryResult(3);

// check error, 데이터 하드코딩
if (search.w3GetError() != 0) {
	NoticeSearchVo vo = new NoticeSearchVo("1480261", "맥(mac) PC Chrome 브라우저의 대체 이용 안내", "항상 하나은행을 이용해 주시는 손님께 진심으로 감사드립니다.<br>현재 chrome 업데이트 이후 맥PC에서 공동인증서가 원활하게 작동하지 않고 있습니다.(로그인/이체거래 등)공동인증서 패치 업데이트 작업 중으로 업데이트 완료 전까지는 Safari 브라우저를 이용해 주시기 바랍니다. 이용에 불편을 드려 죄송합니다.", "20221205-171834",
	"/cont/html/customer/bcm_71/1480261_153458.html");
	noticeList.add(vo);

} else {

	// 전체건수, 결과건수 출력
	totalCount = search.w3GetResultTotalCount(COLLECTION);
	resultCount = search.w3GetResultCount(COLLECTION);

	System.out.println(COLLECTION + " 검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건");

	for (int i = 0; i < resultCount; i++) {

		// 기본 검색결과 객체 생성
		String artId = search.w3GetField(COLLECTION, "ARTID", i);
		String title = search.w3GetField(COLLECTION, "TITLE", i);
		String contents = search.w3GetField(COLLECTION, "CONTENTS", i);
		String date = search.w3GetField(COLLECTION, "Date", i);
		String url = search.w3GetField(COLLECTION, "URL", i);

		NoticeSearchVo vo = new NoticeSearchVo(artId, title, contents, date, url);

		noticeList.add(vo);
	}
}

	out.println("<br>");
	out.print("{\"" + "menu" + "\" : ");
	out.println(menuList);
	out.print("<br>,");
	
	out.print("\"" + "faq" + "\" : ");
	out.println(faqList);
	out.print("<br>,");
	
	out.print("\"" + "product" + "\" : ");
	out.println(productList);
	out.print("<br>,");
	
	out.print("\"" + "notice" + "\" : ");
	out.println(noticeList);
	out.print("}");

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>하나은행 통합검색</title>
</head>
<body>
</body>
</html>