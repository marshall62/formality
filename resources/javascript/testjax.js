var xmlHttp

function showHint(str)
{
if (str.length==0)
  { 
alert ("str len=0");
  document.getElementById("txtHint").innerHTML="";
  return;
  }
xmlHttp=GetXmlHttpObject();
if (xmlHttp==null)
  {
  alert ("Your browser does not support AJAX!");
  return;
  } 
var url="http://bean.cs.umass.edu:8080/4mality/servlet/FormalityServlet";

//alert (" url "+url);
try{
xmlHttp.open("POST",url,true);
}catch(e)
{
   alert ("send error "+e);
}


xmlHttp.onreadystatechange=stateChanged;
xmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");



var params="un=1&fxn=test";
xmlHttp.send(params);
} 

function stateChanged() 
{ 
if (xmlHttp.readyState==4)
{ 
//alert ("state changed");
document.getElementById("txtHint").innerHTML=xmlHttp.responseText;
}
}

function GetXmlHttpObject()
{
var xmlHttp=null;
try
  {
  // Firefox, Opera 8.0+, Safari
  xmlHttp=new XMLHttpRequest();
//alert ("make xml req obj "+xmlHttp.readyState);
  }
catch (e)
  {
  // Internet Explorer
  try
    {
    xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
    }
  catch (e)
    {
    xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
  }
return xmlHttp;
}