var request =null;

function createRequest() {
    try {
        request = new XMLHttpRequest();
    }  catch (trymicrosoft) {
        try {
            request = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (othermicrosoft) {
            try {
                request = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (failed) {
                request = null;
            }
        }
    }
    if (request == null)
        alert("Error creating request object!");
}

function getUrl (url,isAsynch,callbackFn) {
    createRequest();
    request.open("GET",url,isAsynch);
//    request.onreadystatechange = callbackFn;
    request.send(null);
}

function getFileExists (filename) {
    createRequest();
    var url = "QuestionFileUploadServlet?action=uploadImageFile?name="+name;
    /* true is for asynchronous request */
    request.open("GET",url,true);
    request.onreadystatechange = informFile
    /* null means we aren't sending data to the server */
    request.send(null);
}

/* How do I get these parameters? */
function enterExternalActivity (userId,qid, acturl,moduleId,moduleType) {
    createRequest();
    var url = "FormalityServlet?fxn=st&un="+userId+"&mode=viewextact&extacturl="+acturl+"&mID="+moduleId+"&mtype="+mtype+"&qID="+qid;
    request.open("GET",url,true);
    request.send(null);
}