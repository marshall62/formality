var extWin;
var glossWin;

function extraWindow(url, ht, wd)
{
    extWin=window.open(url, 'External_Activity', 'HEIGHT='+ht+',WIDTH='+wd+',resizable=yes,scrollbars=yes,toolbar=no');
}

function gWindow(url, ht, wd)
{
    glossWin=window.open(url, 'Glossary', 'HEIGHT='+ht+',WIDTH='+wd+'left=700,screenX=700,top=300,screenY=300,resizable=yes,scrollbars=yes,toolbar=no');
}

function checkModDel (url)
{
     if(window.confirm("Are you sure you want to delete this module from the database?")) {
         getUrl(url);
         return true;
     }
    else return false;
}


function closeWindows() {
  if(extWin)
    extWin.close();
  if(glossWin)
    glossWin.close();
}

