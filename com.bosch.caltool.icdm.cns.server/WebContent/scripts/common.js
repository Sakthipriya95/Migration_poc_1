function openPopupWindow(file, wId) {
	var wMax, hMax;
    if (document.all) {
        wMax = screen.width;
        hMax = screen.height;
    }
    else if (document.layers) {
        wMax = window.outerWidth;
        hMax = window.outerHeight;
    }
    else {
        wMax = 1400;
        hMax = 700;
    } 
    window.open(file, wId, 'width=' + wMax + ',height=' + hMax + ',top=40,left=40,scrollbars=yes,resizable=yes'); 
}

function checkNull(str){
	var ret ;
	
	if(str){
		ret = str;
	}else{
		ret = "";
	}
	return ret;
}

function invokeGetRequest(xmlHttpReq, reqUrl){
	xmlHttpReq.open("GET", reqUrl, true);
	var zoneOffset = 0 - new Date().getTimezoneOffset();
	xmlHttpReq.setRequestHeader("TimeZoneOffSet", zoneOffset);
	xmlHttpReq.setRequestHeader("Content-type", "application/json");
	   
	xmlHttpReq.send();
}

function selectContents(id){
	var element = document.getElementById(id);
	element.focus();
	element.select();
}

function createTableNavBar(navAreaId){
	document.write("<p id=\"" + navAreaId + "\"></p>");
	
	var sect = "";
	sect += "Showing records <input type=\"text\" id=\"" + navAreaId + "StartIdx\" value=\"1\" size=4  class=\"pageinfo_input\"/> "; 
	sect += "to <label id=\"" + navAreaId + "EndIdx\">1</label>, ";
	sect += "Total = <label id=\"" + navAreaId + "TotalRecs\">0</label>, ";
	sect += "Page Size = <input type=\"text\" id=\"" + navAreaId + "RetSize\" value=\"10\" size=1  class=\"pageinfo_input\"/> ";
	sect += "<button id=\"" + navAreaId + "BtnFirst\" class=\"tabdata_button\" onClick=\"goFirst('" + navAreaId + "')\" disabled title=\"First Page\">|&lt;&lt;</button>";
	sect += "<button id=\"" + navAreaId + "BtnPrev\" class=\"tabdata_button\" onClick=\"goPrevious('" + navAreaId + "')\"disabled title=\"Previous Page\">&lt;&lt;</button>";
	sect += "<button id=\"" + navAreaId + "BtnNext\" class=\"tabdata_button\" onClick=\"goNext('" + navAreaId + "')\"disabled title=\"Next Page\">&gt;&gt;</button>";
	sect += "<button id=\"" + navAreaId + "BtnLast\" class=\"tabdata_button\" onClick=\"goLast('" + navAreaId + "')\" title=\"Last Page\">&gt;&gt;|</button> ";
	sect += "<button id=\"" + navAreaId + "BtnRefresh\" class=\"tabdata_button\" onClick=\"loadByButton('" + navAreaId + "')\" title=\"Refresh table\">Refresh</button>";
	document.write(sect);
}

function getRetSize(navAreaId){
	return parseInt(document.getElementById(navAreaId + "RetSize").value);
}
function loadByButton(navAreaId){
    var startIdx = parseInt(document.getElementById(navAreaId + "StartIdx").value) - 1;
    doLoadData(navAreaId, startIdx, getRetSize(navAreaId));
}

function goFirst(navAreaId){
    doLoadData(navAreaId, 0, getRetSize(navAreaId));
}
function goPrevious(navAreaId){
    var retSize = getRetSize(navAreaId);
    var startIdx = parseInt(document.getElementById(navAreaId + "StartIdx").value) - 1 -  retSize;

    doLoadData(navAreaId, startIdx, retSize);
}

function goNext(navAreaId){
    var retSize = getRetSize(navAreaId);
    var startIdx = parseInt(document.getElementById(navAreaId + "StartIdx").value) - 1 +  retSize;
    
    doLoadData(navAreaId, startIdx, retSize);
}

function goLast(navAreaId){
    var retSize = getRetSize(navAreaId);
	var total = parseInt(document.getElementById(navAreaId + "TotalRecs").innerHTML);
    if(total <= 0){
        return;
    }
    
    var startIdx = parseInt(total/retSize) * retSize;
    if(startIdx == total){
        var r = parseInt(total % retSize);
        if(r == 0){
            startIdx = total - retSize;
        }else{
            startIdx = total - r;
        }
    }
    doLoadData(navAreaId, startIdx, retSize);
}

function updateNavigationArea(navAreaId, pageInfo){
    document.getElementById(navAreaId + "StartIdx").value = pageInfo.startIndex + 1;
    document.getElementById(navAreaId + "EndIdx").innerHTML = pageInfo.endIndex + 1;
    document.getElementById(navAreaId + "TotalRecs").innerHTML = pageInfo.totalRecords;
    document.getElementById(navAreaId + "RetSize").value = pageInfo.pageSize ;
    
    document.getElementById(navAreaId + "BtnFirst").disabled = pageInfo.startIndex == 0;
    document.getElementById(navAreaId + "BtnPrev").disabled = !pageInfo.previous;
    document.getElementById(navAreaId + "BtnNext").disabled = !pageInfo.next;
    document.getElementById(navAreaId + "BtnLast").disabled = (pageInfo.startIndex + pageInfo.pageSize)  >=  pageInfo.totalRecords;
}

