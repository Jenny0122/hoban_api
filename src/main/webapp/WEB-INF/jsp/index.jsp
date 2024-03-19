<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <title>Test</title>
</head>
<body>
<button id="sensitive-search">Search sensitive data</button>
<button id="download">Test excel download</button>
</body>
</html>

<script>
    $(function () {
        $("#sensitive-search").click(function () {
            var body = {};
            body.searchTargetOID = 'fileinfo'
            body.query = '호반건설'

            console.log(body)
            $.ajax({
                url: '/search/sensitive',
                data: JSON.stringify(body),
                type: 'POST',
                contentType: 'application/json'
            }).done(function (response) {
                console.dir(response.data);
            })
        })

        $("#download").click(function () {
            $.ajax({
                url: '/sensitive/excel',
                type: 'GET',
                cache: false,
                xhrFields: {
                    responseType: "blob",
                },
            })
                .done(function (blob, status, xhr) {
                    // check for a filename
                    var fileName = "";
                    var disposition = xhr.getResponseHeader("Content-Disposition");

                    if (disposition && disposition.indexOf("attachment") !== -1) {
                        var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                        var matches = filenameRegex.exec(disposition);

                        if (matches != null && matches[1]) {
                            fileName = decodeURI(matches[1].replace(/['"]/g, ""));
                        }
                    }

                    // for IE
                    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                        window.navigator.msSaveOrOpenBlob(blob, fileName);
                    } else {
                        var URL = window.URL || window.webkitURL;
                        var downloadUrl = URL.createObjectURL(blob);

                        if (fileName) {
                            var a = document.createElement("a");

                            // for safari
                            if (a.download === undefined) {
                                window.location.href = downloadUrl;
                            } else {
                                a.href = downloadUrl;
                                a.download = fileName;
                                document.body.appendChild(a);
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