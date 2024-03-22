<!DOCTYPE html>
<html>
<head>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<meta charset="UTF-8">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<title>Test</title>
</head>
<body>
	<h1>엑셀 다운로드 테스트</h1>
	<input type="text" id="query"></input>
	<button id="sensitive-search">Search sensitive data</button><span style="width: 50px;"></span>
	<button id="download">Test excel download</button>
</body>
</html>

<script>
	$(function() {
		$("#sensitive-search").click(function() {
			var body = {};
			body.searchTargetOID = 'fileinfo'
			body.query = $("#query").val()

			console.log(body)
			$.ajax({
				url : '/search/sensitive',
				data : JSON.stringify(body),
				type : 'POST',
				contentType : 'application/json'
			}).done(function(response) {
				console.dir(response.data);
			})
		})

		$("#download")
				.click(
						function() {
							$
									.ajax({
										url : '/sensitive/excel',
										type : 'GET',
										cache : false,
										xhrFields : {
											responseType : "blob",
										},
									})
									.done(
											function(blob, status, xhr) {
												// check for a filename
												var fileName = "";
												var disposition = xhr
														.getResponseHeader("Content-Disposition");

												if (disposition
														&& disposition
																.indexOf("attachment") !== -1) {
													var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
													var matches = filenameRegex
															.exec(disposition);

													if (matches != null
															&& matches[1]) {
														fileName = decodeURI(matches[1]
																.replace(
																		/['"]/g,
																		""));
													}
												}

												// for IE
												if (window.navigator
														&& window.navigator.msSaveOrOpenBlob) {
													window.navigator
															.msSaveOrOpenBlob(
																	blob,
																	fileName);
												} else {
													var URL = window.URL
															|| window.webkitURL;
													var downloadUrl = URL
															.createObjectURL(blob);

													if (fileName) {
														var a = document
																.createElement("a");

														// for safari
														if (a.download === undefined) {
															window.location.href = downloadUrl;
														} else {
															a.href = downloadUrl;
															a.download = fileName;
															document.body
																	.appendChild(a);
															a.click();
														}
													} else {
														window.location.href = downloadUrl;
													}
												}
											});
						})
	})
</script>